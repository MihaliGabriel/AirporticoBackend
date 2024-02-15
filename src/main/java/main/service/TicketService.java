package main.service;

import main.controller.utils.ControllerUtils;
import main.dto.BuyTicketDTO;
import main.dto.PassengerDTO;
import main.dto.TicketDTO;
import main.model.*;
import main.model.exceptions.FieldInvalidException;
import main.model.exceptions.FieldNotUniqueOrNullException;
import main.repository.PassengerRepository;
import main.repository.TicketRepository;
import main.utils.DocumentUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;

import static main.controller.TicketController.*;

@Service
public class TicketService implements ITicketService {

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private PassengerRepository passengerRepository;

    @Autowired
    private UsersService usersService;

    @Autowired
    private PersonService personService;

    @Autowired
    private VoucherService voucherService;

    @Autowired
    private FlightService flightService;

    @Autowired
    private PassengerService passengerService;
    private static final Logger logger = LoggerFactory.getLogger(TicketService.class);
    @Override
    public List<Ticket> getAllTickets() {
        return ticketRepository.findAll();
    }

    public Page<Ticket> getAllTickets(Pageable pageable) {
        return ticketRepository.findAll(pageable);
    }

    @Override
    public Ticket getTicketById(Long id) {
        return ticketRepository.getById(id);
    }

    public Ticket getTicketByPrice(Double price) {
        return ticketRepository.findByPrice(price);
    }

    @Override
    public List<Ticket> getTicketsByUser(Long userId) {
        return ticketRepository.findTicketsByUser(userId);
    }

    @Override
    public List<Ticket> getTicketsByFlight(Long flightId) {
        return ticketRepository.findTicketsByFlight(flightId);
    }

    @Override
    public Integer getTotalNrOfTickets() {
        return ticketRepository.getNumberOfTicketsBought();
    }

    @Override
    public Integer getNrOfTicketsForFlight(Long flightId) {
        return ticketRepository.getNumberOfTicketsBoughtFlight(flightId);
    }


    @Override
    public Ticket createTicket(Ticket ticket) {
        return ticketRepository.save(ticket);
    }

    @Override
    public void updateTicket(Ticket ticket) {
        ticketRepository.save(ticket);
    }

    /**
     * @Author GXM
     * Stergere cascadata, mai intai se sterg pasagerii asociati biletului si dupa biletul.
     * @param id
     */
    public void deleteTicketById(Long id) {
        List<Passenger> passengers = passengerRepository.getPassengersByTicketId(id);
        if (passengers != null && !passengers.isEmpty()) {
            logger.info("Passengers {}", passengers);
            for (Passenger passenger : passengers) {
                passengerRepository.deleteById(passenger.getId());
            }
        }
        ticketRepository.deleteById(id);
    }

    @Override
    public List<Ticket> getTicketsByRoute(Long routeId) {
        return ticketRepository.findTicketsByRoute(routeId);
    }

    @Override
    public TicketDTO reserveTicket(BuyTicketDTO buyTicketDTO) throws EntityNotFoundException, FieldInvalidException, FieldNotUniqueOrNullException {
        User user = usersService.getUserById(buyTicketDTO.getUserId());
        Person person = personService.findPersonByUserId(user.getId());
        Voucher voucher = voucherService.getVoucherByCode(buyTicketDTO.getVoucherCode());
        Flight flight = flightService.getFlightByName(buyTicketDTO.getFlightName());

        double discountPercentage;
        int luggagePrice = 0;

        List<PassengerDTO> passengerDTOSLuggage = buyTicketDTO.getPassengers();

        for (PassengerDTO passengerDTO : passengerDTOSLuggage) {
            luggagePrice += (passengerDTO.getNrSmallLuggage() * SMALL_LUGGAGE_PRICE) + (passengerDTO.getNrMediumLuggage() + MEDIUM_LUGGAGE_PRICE) + (passengerDTO.getNrBigLuggage() + BIG_LUGGAGE_PRICE);
        }


        TicketDTO boughtTicketDTO = new TicketDTO();

        List<Passenger> passengers = new ArrayList<>();
        List<PassengerDTO> passengerDTOS;
        Ticket boughtTicket = new Ticket();

        if (voucher != null) {
            discountPercentage = voucher.getDiscountPercentage() / 100;
        } else {
            discountPercentage = 0.0;
        }

        Integer businessFlightSeatsBeforeBuy = flight.getRemainingBusinessSeats();
        Integer economyFlightSeatsBeforeBuy = flight.getRemainingEconomySeats();
        Integer firstClassFlightSeatsBeforeBuy = flight.getRemainingFirstClassSeats();
        Integer nrOfTickets = buyTicketDTO.getNrOfPassengers();

        logger.info("Nr of tickets: {}", nrOfTickets);

        computeTicket(buyTicketDTO, user, person, flight, discountPercentage, luggagePrice, boughtTicket, businessFlightSeatsBeforeBuy, economyFlightSeatsBeforeBuy, firstClassFlightSeatsBeforeBuy, nrOfTickets, flightService);

        createTicket(boughtTicket);

        if (person != null) {
            Passenger ticketOwner = getTicketOwner(buyTicketDTO, person, boughtTicket);
            passengerService.addPassenger(ticketOwner);
            passengers.add(ticketOwner);
        }
        Passenger passenger;
        logger.info("Passengers: {}", buyTicketDTO.getPassengers());
        for (int i = 0; i < nrOfTickets; i++) {
            passenger = new Passenger();


            passenger.setFirstName(buyTicketDTO.getPassengers().get(i).getFirstName());
            passenger.setLastName(buyTicketDTO.getPassengers().get(i).getLastName());
            passenger.setEmail(buyTicketDTO.getPassengers().get(i).getEmail());
            passenger.setPhoneNumber(buyTicketDTO.getPassengers().get(i).getPhoneNumber());
            passenger.setTicket(boughtTicket);
            passenger.setSeat(buyTicketDTO.getPassengers().get(i).getSeat());

            passengerService.addPassenger(passenger);
            passengers.add(passenger);
        }
        passengerDTOS = ControllerUtils.mapPassengerListToPassengerDtoList(passengers);

        boughtTicketDTO.setId(boughtTicket.getId());
        boughtTicketDTO.setUserId(boughtTicket.getUser().getId());
        boughtTicketDTO.setFlightName(boughtTicket.getFlight().getName());
        boughtTicketDTO.setPrice(boughtTicket.getPrice());
        boughtTicketDTO.setPassengers(passengerDTOS);
        boughtTicketDTO.setTicketType(boughtTicket.getTicketType());
        boughtTicketDTO.setTicketName(boughtTicket.getFlight().getName());

        return boughtTicketDTO;
    }

    private static void computeTicket(BuyTicketDTO buyTicketDTO, User user, Person person, Flight flight, double discountPercentage, int luggagePrice,
                                      Ticket boughtTicket, Integer businessFlightSeatsBeforeBuy, Integer economyFlightSeatsBeforeBuy, Integer firstClassFlightSeatsBeforeBuy, Integer nrOfTickets, IFlightService flightService) throws FieldNotUniqueOrNullException, FieldInvalidException {
        if (buyTicketDTO.getTicketType().equals("Business") && (businessFlightSeatsBeforeBuy >= nrOfTickets)) {
            computeBusinessTicket(buyTicketDTO, user, person, flight, discountPercentage, luggagePrice, boughtTicket, businessFlightSeatsBeforeBuy, nrOfTickets, flightService);
        }
        if (buyTicketDTO.getTicketType().equals("Economy") && (economyFlightSeatsBeforeBuy >= nrOfTickets)) {
            computeEconomyTicket(buyTicketDTO, user, person, flight, discountPercentage, luggagePrice, boughtTicket, economyFlightSeatsBeforeBuy, nrOfTickets, flightService);
        }
        if (buyTicketDTO.getTicketType().equals("First class") && (firstClassFlightSeatsBeforeBuy >= nrOfTickets)) {
            computeFirstClassTicket(buyTicketDTO, user, person, flight, discountPercentage, luggagePrice, boughtTicket, firstClassFlightSeatsBeforeBuy, nrOfTickets, flightService);
        }
    }

    private static void computeFirstClassTicket(BuyTicketDTO buyTicketDTO, User user, Person person, Flight flight, double discountPercentage, int luggagePrice,
                                                Ticket boughtTicket, Integer firstClassFlightSeatsBeforeBuy, Integer nrOfTickets, IFlightService flightService) throws FieldNotUniqueOrNullException, FieldInvalidException {
        if (nrOfTickets == 0 && person != null) {
            flight.setRemainingFirstClassSeats(firstClassFlightSeatsBeforeBuy - 1);
            flightService.updateFlight(flight);

            logger.info("remaining first class seats: {}", flight.getRemainingBusinessSeats());

            boughtTicket.setUser(user);
            boughtTicket.setFlight(flight);
            boughtTicket.setPrice(((flight.getDiscountedFirstClassPrice() - (flight.getDiscountedFirstClassPrice() * discountPercentage)) * 1) + luggagePrice);
            boughtTicket.setTicketType(buyTicketDTO.getTicketType());
            boughtTicket.setTicketStatus(TicketStatus.RESERVED);

        } else if (nrOfTickets > 0) {
            flight.setRemainingEconomySeats(firstClassFlightSeatsBeforeBuy - nrOfTickets);
            flightService.updateFlight(flight);

            logger.info("remaining first class seats: {}", flight.getRemainingBusinessSeats());

            boughtTicket.setUser(user);
            boughtTicket.setFlight(flight);
            if (person != null) {
                boughtTicket.setPrice(((flight.getDiscountedFirstClassPrice() - (flight.getDiscountedFirstClassPrice() * discountPercentage)) * (nrOfTickets + 1)) + luggagePrice);
            }
            boughtTicket.setTicketType(buyTicketDTO.getTicketType());
            boughtTicket.setTicketStatus(TicketStatus.RESERVED);
        }
    }

    private static void computeEconomyTicket(BuyTicketDTO buyTicketDTO, User user, Person person, Flight flight, double discountPercentage, int luggagePrice,
                                             Ticket boughtTicket, Integer economyFlightSeatsBeforeBuy, Integer nrOfTickets, IFlightService flightService) throws FieldNotUniqueOrNullException, FieldInvalidException {
        if (nrOfTickets == 0 && person != null) {
            flight.setRemainingEconomySeats(economyFlightSeatsBeforeBuy - (nrOfTickets + 1));
            flightService.updateFlight(flight);

            logger.info("remaining economy seats: {}", flight.getRemainingBusinessSeats());

            boughtTicket.setUser(user);
            boughtTicket.setFlight(flight);
            boughtTicket.setPrice(((flight.getDiscountedEconomyPrice() - (flight.getDiscountedEconomyPrice() * discountPercentage)) * 1) + luggagePrice);
            boughtTicket.setTicketType(buyTicketDTO.getTicketType());
            boughtTicket.setTicketStatus(TicketStatus.RESERVED);

        } else if (nrOfTickets > 0) {
            flight.setRemainingEconomySeats(economyFlightSeatsBeforeBuy - (nrOfTickets + 1));
            flightService.updateFlight(flight);

            logger.info("remaining economy seats: {}", flight.getRemainingBusinessSeats());

            boughtTicket.setUser(user);
            boughtTicket.setFlight(flight);
            if (person != null) {
                boughtTicket.setPrice(((flight.getDiscountedEconomyPrice() - (flight.getDiscountedEconomyPrice() * discountPercentage)) * (nrOfTickets + 1)) + luggagePrice);
            }
            boughtTicket.setTicketType(buyTicketDTO.getTicketType());
            boughtTicket.setTicketStatus(TicketStatus.RESERVED);
        }
    }

    private static void computeBusinessTicket(BuyTicketDTO buyTicketDTO, User user, Person person, Flight flight, double discountPercentage, int luggagePrice,
                                              Ticket boughtTicket, Integer businessFlightSeatsBeforeBuy, Integer nrOfTickets, IFlightService flightService) throws FieldNotUniqueOrNullException, FieldInvalidException {
        if (nrOfTickets == 0 && person != null) {
            flight.setRemainingBusinessSeats(businessFlightSeatsBeforeBuy - 1);
            flightService.updateFlight(flight);

            logger.info("remaining business seats: {}", flight.getRemainingBusinessSeats());

            boughtTicket.setUser(user);
            boughtTicket.setFlight(flight);
            boughtTicket.setPrice(((flight.getDiscountedBusinessPrice() - (flight.getDiscountedBusinessPrice() * discountPercentage)) * 1) + luggagePrice);
            boughtTicket.setTicketType(buyTicketDTO.getTicketType());
            boughtTicket.setTicketStatus(TicketStatus.RESERVED);

        } else if (nrOfTickets > 0) {
            flight.setRemainingBusinessSeats(businessFlightSeatsBeforeBuy - (nrOfTickets + 1));
            flightService.updateFlight(flight);

            logger.info("Nr of tickets: {}", nrOfTickets);
            logger.info("remaining business seats: {}", flight.getRemainingBusinessSeats());

            boughtTicket.setUser(user);
            boughtTicket.setFlight(flight);
            if (person != null) {
                boughtTicket.setPrice(((flight.getDiscountedBusinessPrice() - (flight.getDiscountedBusinessPrice() * discountPercentage)) * (nrOfTickets + 1)) + luggagePrice);
            }
            boughtTicket.setTicketType(buyTicketDTO.getTicketType());
            boughtTicket.setTicketStatus(TicketStatus.RESERVED);
        }
    }

    private static Passenger getTicketOwner(BuyTicketDTO buyTicketDTO, Person person, Ticket boughtTicket) {
        Passenger ticketOwner = new Passenger();

        ticketOwner.setFirstName(person.getFirstName());
        ticketOwner.setLastName(person.getLastName());
        ticketOwner.setEmail(person.getEmail());
        ticketOwner.setPhoneNumber(person.getPhoneNumber());
        ticketOwner.setSeat(buyTicketDTO.getBuyerSeat());
        ticketOwner.setTicket(boughtTicket);

        return ticketOwner;
    }
}
