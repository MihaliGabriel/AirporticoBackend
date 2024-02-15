package main.service;

import main.dto.BuyTicketDTO;
import main.dto.FlightDTO;
import main.dto.PassengerDTO;
import main.dto.ReserveSeatDTO;
import main.model.*;
import main.model.exceptions.FieldInvalidException;
import main.model.exceptions.FieldNotUniqueOrNullException;
import main.repository.CompanyRepository;
import main.repository.FlightRepository;
import main.utils.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.util.Collections;
import java.util.List;

@Service
public class FlightService implements IFlightService {

    @Autowired
    private FlightRepository flightRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private ITicketService ticketService;

    @Autowired
    private IPersonService personService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private IPassengerService passengerService;


    private static final Logger logger = LoggerFactory.getLogger(FlightService.class);
    private static final String INSERT = "INSERT";
    private static final String UPDATE = "UPDATE";
    
    @Override
    public List<Flight> getFlights() {
        List<Flight> flights = flightRepository.findAll();

        for (Flight flight : flights) {
            AirplaneSeatType[][] seatMap = generateSeatMap(flight);
            logger.info("Seat map: {}", seatMap);
            flight.setOccupiedSeats(seatMap);
        }
        return flights;
    }

    @Override
    public Page<Flight> getFlights(Pageable pageable) {
        try {
            Page<Flight> flights = flightRepository.findAll(pageable);

            for (Flight flight : flights.getContent()) {
                AirplaneSeatType[][] seatMap = generateSeatMap(flight);
                logger.info("Seat map getFlights: {}", seatMap.toString());
                flight.setOccupiedSeats(seatMap);
            }

            return flights;
        }
        catch (Exception e) {
            e.printStackTrace();
            return new PageImpl<>(Collections.emptyList(), pageable, 0);
        }
    }

    private boolean checkUniqueName(String name, String type) {
        Long count = flightRepository.countByName(name);
        logger.info("flight count: {}", count);

        return (count == 0 || !type.equals(INSERT)) && (count <= 1 || !type.equals(UPDATE));
    }

    private boolean checkNullName(String name) {
        return name == null || name.isEmpty();
    }

    @Override
    public void addFlight(Flight flight) throws FieldNotUniqueOrNullException, FieldInvalidException {
        if (checkNullName(flight.getName()))
            throw new FieldNotUniqueOrNullException("Flight name is null");
        if (!checkUniqueName(flight.getName(), INSERT))
            throw new FieldNotUniqueOrNullException("Flight name not unique");
        if (!Util.checkValidDouble(flight.getBusinessPrice()) && !Util.checkValidDouble(flight.getEconomyPrice())
                || !Util.checkValidDouble(flight.getFirstClassPrice()))
            throw new FieldInvalidException("Prices not valid");

        flightRepository.save(flight);
    }

    @Override
    public Flight getFlightById(Long id) {
        return flightRepository.getById(id);
    }

    @Override
    public Flight getFlightByName(String flightName) {
        return flightRepository.findByName(flightName);
    }

    @Override
    public Page<Flight> searchFlights(FlightDTO flightDTO, Pageable pageable) {

        String companyName = null;
        String sortByDuration = flightDTO.getSortByDuration() != null ? flightDTO.getSortByDuration() : "";

        if (flightDTO.getCompanyId() != null) {
            Company company = companyRepository.getById(flightDTO.getCompanyId());

            companyName = company.getName();

            logger.info("Company name: {}", company.getName());
        }

        Sort sort;
        if ("Ascending".equals(sortByDuration)) {
            sort = Sort.by("durationInSeconds").ascending();
        } else if ("Descending".equals(sortByDuration)) {
            sort = Sort.by("durationInSeconds").descending();
        } else {
            sort = pageable.getSort();  // Use the sort direction from the original pageable if neither Ascending nor Descending
        }

        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
        Page<Flight> flightsPage = flightRepository.searchFlights(flightDTO.getArrivalDate(), flightDTO.getDepartureDate(), flightDTO.getDepartureCity(), flightDTO.getArrivalCity(), companyName,
                                                                    flightDTO.getBusinessPriceMin(), flightDTO.getBusinessPriceMax(), flightDTO.getEconomyPriceMin(), flightDTO.getEconomyPriceMax(),
                                                                    flightDTO.getFirstClassPriceMin(), flightDTO.getFirstClassPriceMax(), sortedPageable);
        logger.info("Page content: {}", flightsPage.getContent());
        return flightsPage;
    }

    /**
     * @param flight
     * @throws FieldNotUniqueOrNullException
     * @Author Daca vreau sa updatez zborul cu alt nume, verific doar daca mai exista o intrare in db cu acelasi nume.
     * Daca numele ramane la fel la update, nu fac nimic, deoarece intrarea este valida in db.
     */
    @Override
    public void updateFlight(Flight flight) throws FieldNotUniqueOrNullException, FieldInvalidException {
        Flight flightFromDb = flightRepository.getById(flight.getId());
        List<Person> people = personService.findPersonByFlightId(flight.getId());
        try {
            if(checkIfFlightRouteWasChanged(flight, flightFromDb)) {
                for (Person person : people) {
                    emailService.sendFlightChangeToPersonEmail(person.getEmail());
                }
            }
            if (checkNullName(flight.getName()))
                throw new FieldNotUniqueOrNullException("Flight name is null");

            if (!flightFromDb.getName().equals(flight.getName()) && !checkUniqueName(flight.getName(), INSERT)) {
                throw new FieldNotUniqueOrNullException("Flight name not unique");
            }

            if (!Util.checkValidDouble(flight.getBusinessPrice()) && !Util.checkValidDouble(flight.getEconomyPrice())
                    || !Util.checkValidDouble(flight.getFirstClassPrice()))
                throw new FieldInvalidException("Prices are invalid");

            flightRepository.save(flight);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean checkIfFlightRouteWasChanged(Flight updatedFlight, Flight flightFromDb) {
        Route routeFromDb = flightFromDb.getRoute();
        Route updatedRoute = updatedFlight.getRoute();

        //checks if the route was updated for the flight
        if((routeFromDb.getArrivalAirport().getId() != updatedRoute.getArrivalAirport().getId()) ||
                (routeFromDb.getDepartureAirport().getId() != updatedRoute.getDepartureAirport().getId())) {
            return true;
        }

        return false;
    }
    /**
     * @param id
     * @Author GXM
     * Atunci cand un zbor este sters, este trimis un email persoanelor care au cumparat bilete pentru acel zbor, informandu-le ca zborul a fost anulat.
     * Stergerea zborului este cascadata, mai intai se sterg biletele asociate zborului respectiv si dupa zborul.
     */
    @Override
    public void deleteFlightById(Long id) throws NullPointerException, MessagingException {

        List<Ticket> tickets = ticketService.getTicketsByFlight(id);
        List<Person> people = personService.findPersonByFlightId(id);
        if(tickets.isEmpty()) {
            throw new NullPointerException("Tickets is null");
        }
        else {
            for (Person person : people) {
                emailService.sendFlightCancelToPersonEmail(person.getEmail());
            }
            for (Ticket ticket : tickets) {
                ticketService.deleteTicketById(ticket.getId());
            }
            flightRepository.deleteById(id);
        }
    }

    @Override
    public AirplaneSeatType[][] generateSeatMap(Flight flight) {

        List<Passenger> passengerList = passengerService.getPassengersByFlight(flight.getId());
        AirplaneSeatType[][] seatMap = new AirplaneSeatType[flight.getAirplane().getRows()][flight.getAirplane().getColumns()];

        for(int i = 0; i < flight.getAirplane().getRows(); i++) {
            for(int j = 0; j < flight.getAirplane().getColumns(); j++) {
                seatMap[i][j] = AirplaneSeatType.UNOCCUPIED;
            }
        }

        for (Passenger passenger : passengerList) {

            String seat = passenger.getSeat();
            logger.info("Seat {} for passenger {}", passenger.getLastName(), passenger.getSeat());
            //TODO verificam daca scaunul contine si +, atunci punem AirplaneSeatType.RESERVED.
            if (!seat.isEmpty()) {
                int[] seatSplit = Util.splitPassengerSeats(seat);
                seatMap[seatSplit[1]][seatSplit[0]] = AirplaneSeatType.OCCUPIED;
                logger.info("Seat map entry: {}", seatMap[seatSplit[1]][seatSplit[0]]);
            }
            else {
                int[] seatSplit = Util.splitPassengerSeats(seat);
                seatMap[seatSplit[1]][seatSplit[0]] = AirplaneSeatType.UNOCCUPIED;
            }
        }
        for(int i = 0; i < flight.getAirplane().getRows(); i++) {
            for(int j = 0; j < flight.getAirplane().getColumns(); j++) {
                logger.info("Seat map:{}", seatMap[i][j]);
            }
        }
        return seatMap;
    }

    @Override
    public BuyTicketDTO reserveSeats(BuyTicketDTO buyTicketDTO) {
        String buyerSeat = buyTicketDTO.getBuyerSeat();
        List<PassengerDTO> passengerDTOS = buyTicketDTO.getPassengers();

        //scaunele rezervate sunt notate cu un + (14A+) pentru a se sti ca sunt rezervate, urmand a fi procesate de generateSeatMap()
        String reservedBuyerSeat = buyerSeat + "+";

        PassengerDTO passengerDTO;
        for(int i = 0; i < passengerDTOS.size(); i++) {
            passengerDTO = passengerDTOS.get(i);

            String reservedSeat = passengerDTO.getSeat() + "+";
            passengerDTO.setSeat(reservedSeat);

            passengerDTOS.set(i, passengerDTO);
        }

        buyTicketDTO.setPassengers(passengerDTOS);
        buyTicketDTO.setBuyerSeat(buyerSeat);

        return buyTicketDTO;
    }
}
