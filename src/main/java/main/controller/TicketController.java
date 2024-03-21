package main.controller;

import main.controller.utils.ControllerUtils;
import main.dto.*;
import main.model.*;
import main.model.exceptions.FieldInvalidException;
import main.model.exceptions.FieldNotUniqueOrNullException;
import main.service.*;
import main.utils.DocumentUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/api")
public class TicketController {

    
    private ITicketService ticketService;
    private IFlightService flightService;
    private IUsersService usersService;
    private IPassengerService passengerService;
    private EmailService emailService;
    private IPersonService personService;
    private IVoucherService voucherService;

    public static final int SMALL_LUGGAGE_PRICE = 30;

    public static final int MEDIUM_LUGGAGE_PRICE = 60;

    public static final int BIG_LUGGAGE_PRICE = 100;

    private static final String MESSAGE = "message";

    private static final Logger logger = LoggerFactory.getLogger(TicketController.class);

    @Autowired
    public TicketController(ITicketService ticketService, IFlightService flightService, IUsersService usersService, IPassengerService passengerService, EmailService emailService, IPersonService personService, IVoucherService voucherService) {
        this.ticketService = ticketService;
        this.flightService = flightService;
        this.usersService = usersService;
        this.passengerService = passengerService;
        this.emailService = emailService;
        this.personService = personService;
        this.voucherService = voucherService;
    }


    @CrossOrigin(origins = {"http://localhost:4200", "https://thankful-coast-03f536003.4.azurestaticapps.net"})
    @PostMapping("/updatereservedticket")
    public ResponseEntity<Object> updateReservedTicket(@RequestBody TicketDTO ticketDTO) {
        try {
            logger.info("TicketDTO id: {}", ticketDTO.getId());
            Ticket ticket = ticketService.getTicketById(ticketDTO.getId());
            ticket.setTicketStatus(TicketStatus.BOUGHT);

            ticketService.updateTicket(ticket);

            return ResponseEntity.ok().body(ticketDTO);
        }
        catch(Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }
    /**
     * @Author GXM
     * Tipul metodei este ResponseEntity<Object> deoarece metoda returneaza obiecte de tip DTO, dar si map-uri de erori pentru exceptii,
     * care sunt prelucrate ca si erori de front-end.
     * @return
     */
    @CrossOrigin(origins = {"http://localhost:4200", "https://thankful-coast-03f536003.4.azurestaticapps.net"})
    @PostMapping("/tickets")
    public ResponseEntity<Object> getAllTickets(@RequestHeader("Language") String language) {
        try {
            List<Ticket> tickets = ticketService.getAllTickets();
            List<TicketDTO> ticketDTOS = new ArrayList<>();

            if (tickets == null || tickets.isEmpty())
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Tickets list is null or empty");

            List<Passenger> passengersForTicket;
            TicketDTO ticketDTO;

            for (Ticket ticket : tickets) {
                passengersForTicket = passengerService.getPassengerByTicket(ticket);

                List<PassengerDTO> passengerDTOSForTicket = ControllerUtils.mapPassengerListToPassengerDtoList(passengersForTicket);
                Flight flight = ticket.getFlight();

                ticketDTO = new TicketDTO();

                ticketDTO.setFlightName(flight.getName());
                ticketDTO.setTicketName(flight.getName());
                ticketDTO.setPrice(ticket.getPrice());
                ticketDTO.setPassengers(passengerDTOSForTicket);

                ticketDTOS.add(ticketDTO);
            }

            return ResponseEntity.status(HttpStatus.OK).body(ticketDTOS);
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap(MESSAGE, e.getMessage()));
        }
    }


    @GetMapping("/tickets/id/{id}")
    public ResponseEntity<Object> getTicketById(@PathVariable("id") Long id) {
        try {
            Ticket ticket = ticketService.getTicketById(id);

            if (ticket == null)
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Tickets not found");

            Flight flight = ticket.getFlight();

            List<Passenger> passengersForTicket = passengerService.getPassengerByTicket(ticket);

            List<PassengerDTO> passengerDTOSForTicket = ControllerUtils.mapPassengerListToPassengerDtoList(passengersForTicket);

            TicketDTO ticketDTO = new TicketDTO();

            ticketDTO.setId(ticket.getId());
            ticketDTO.setFlightName(flight.getName());
            ticketDTO.setTicketName(flight.getName());
            ticketDTO.setPassengers(passengerDTOSForTicket);
            ticketDTO.setPrice(ticket.getPrice());
            ticketDTO.setPrice(ticket.getPrice());

            return ResponseEntity.status(HttpStatus.OK).body(ticketDTO);
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap(MESSAGE, e.getMessage()));
        }
    }

    @GetMapping("/tickets/price/{price}")
    public ResponseEntity<Object> getTicketByPrice(@PathVariable("price") Double price) {
        try {
            Ticket ticket = ticketService.getTicketByPrice(price);

            if (ticket == null)
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);

            Flight flight = ticket.getFlight();

            List<Passenger> passengersForTicket = passengerService.getPassengerByTicket(ticket);

            List<PassengerDTO> passengerDTOSForTicket = ControllerUtils.mapPassengerListToPassengerDtoList(passengersForTicket);

            TicketDTO ticketDTO = new TicketDTO();

            ticketDTO.setId(ticket.getId());
            ticketDTO.setFlightName(flight.getName());
            ticketDTO.setTicketName(flight.getName());
            ticketDTO.setPassengers(passengerDTOSForTicket);
            ticketDTO.setPrice(ticket.getPrice());

            return ResponseEntity.status(HttpStatus.OK).body(ticketDTO);
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap(MESSAGE, e.getMessage()));
        }
    }

    @PostMapping("/updateticket")
    public ResponseEntity<Object> updateTicket(@RequestBody TicketDTO ticketDTO) {
        try {
            Ticket ticket = new Ticket();
            User user = usersService.getUserById(ticketDTO.getUserId());
            Flight flight = flightService.getFlightByName(ticketDTO.getFlightName());

            if (user == null || flight == null)
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);

            ticket.setFlight(flight);
            ticket.setUser(user);
            ticket.setPrice(ticketDTO.getPrice());

            ticketService.updateTicket(ticket);

            return ResponseEntity.status(HttpStatus.OK).body(ticketDTO);
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap(MESSAGE, e.getMessage()));
        }
    }

    @GetMapping("/nroftickets")
    public ResponseEntity<Object> getNrOfTicketsBought() {
        try {
            Integer nrTickets = ticketService.getTotalNrOfTickets();
            return ResponseEntity.status(HttpStatus.OK).body(nrTickets);
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap(MESSAGE, e.getMessage()));
        }
    }

    @Async
    @CrossOrigin(origins = {"http://localhost:4200", "https://thankful-coast-03f536003.4.azurestaticapps.net"})
    @PostMapping("/buyticket")
    public ResponseEntity<Object> buyTicket(@RequestBody BuyTicketDTO buyTicketDTO) {
        try {
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
            if (flight == null)
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("flight null");

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

            ticketService.createTicket(boughtTicket);

            if (person != null) {
                Passenger ticketOwner = getTicketOwner(buyTicketDTO, person, boughtTicket);
                passengerService.addPassenger(ticketOwner);
                passengers.add(ticketOwner);
            }
            Passenger passenger;
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

            DocumentUtil.generateTicketPDF(boughtTicketDTO);
            emailService.sendTicketEmail(boughtTicketDTO.getPassengers().get(0).getEmail());
            return ResponseEntity.status(HttpStatus.OK).body(boughtTicketDTO);

        }
        catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap(MESSAGE, e.getMessage()));
        }
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
            boughtTicket.setTicketStatus(TicketStatus.BOUGHT);

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
            boughtTicket.setTicketStatus(TicketStatus.BOUGHT);
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
            boughtTicket.setTicketStatus(TicketStatus.BOUGHT);
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
            boughtTicket.setTicketStatus(TicketStatus.BOUGHT);
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
            boughtTicket.setTicketStatus(TicketStatus.BOUGHT);
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
            boughtTicket.setTicketStatus(TicketStatus.BOUGHT);
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

    @CrossOrigin(origins = {"http://localhost:4200", "https://thankful-coast-03f536003.4.azurestaticapps.net"})
    @PostMapping("/reserveticket")
    public ResponseEntity<Object> reserveTicket(@RequestBody BuyTicketDTO buyTicketDTO) {
        try {
            logger.info("Reserving ticket..");
            TicketDTO ticketDTO = ticketService.reserveTicket(buyTicketDTO);

            Ticket ticket = ticketService.getTicketById(ticketDTO.getId());

            User user = usersService.getUserById(ticketDTO.getUserId());
            Person person = personService.findPersonByUserId(user.getId());

            emailService.sendReservedTicketEmail(ticket, person.getEmail());
            return ResponseEntity.status(HttpStatus.OK).body(ticketDTO);
        }
        catch(Exception e) {
            logger.error("Eroare la rezevarea biletului: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    @CrossOrigin(origins = {"http://localhost:4200", "https://thankful-coast-03f536003.4.azurestaticapps.net"})
    @PostMapping("/ticketsbyuser")
    public ResponseEntity<Object> getTicketsByUser(@RequestBody UserDTO userDTO) {
        try {
            User user = usersService.getUserById(userDTO.getId());

            if (user == null)
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);

            List<Ticket> ticketsByUser = ticketService.getTicketsByUser(user.getId());
            List<TicketDTO> ticketDTOS = new ArrayList<>();
            if (ticketsByUser == null || ticketsByUser.isEmpty())
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);


            TicketDTO ticketDTO;
            List<Passenger> passengers;
            List<PassengerDTO> passengerDTOS;

            for (Ticket ticket : ticketsByUser) {
                ticketDTO = new TicketDTO();
                passengers = passengerService.getPassengerByTicket(ticket);

                passengerDTOS = ControllerUtils.mapPassengerListToPassengerDtoList(passengers);

                ticketDTO.setId(ticket.getId());
                ticketDTO.setTicketName(ticket.getFlight().getName());
                ticketDTO.setUserId(user.getId());
                ticketDTO.setFlightName(ticket.getFlight().getName());
                ticketDTO.setPrice(ticket.getPrice());
                ticketDTO.setTicketType(ticket.getTicketType());
                ticketDTO.setPassengers(passengerDTOS);
                ticketDTOS.add(ticketDTO);
            }

            return ResponseEntity.status(HttpStatus.OK).body(ticketDTOS);
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap(MESSAGE, e.getMessage()));
        }
    }

    @GetMapping("/tickets/flightid/{id}")
    public ResponseEntity<Object> getTicketsByFlight(@PathVariable Long id) {
        try {
            Flight flight = flightService.getFlightById(id);

            if (flight == null)
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);

            List<Ticket> ticketsByFlight = ticketService.getTicketsByFlight(id);
            List<TicketDTO> ticketDTOS = new ArrayList<>();
            if (ticketsByFlight == null || ticketsByFlight.isEmpty())
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);

            for (Ticket ticket : ticketsByFlight) {
                TicketDTO ticketDTO = new TicketDTO();

                ticketDTO.setFlightName(ticket.getFlight().getName());
                ticketDTO.setPrice(ticket.getPrice());

                ticketDTOS.add(ticketDTO);
            }

            return ResponseEntity.status(HttpStatus.OK).body(ticketDTOS);
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap(MESSAGE, e.getMessage()));
        }
    }
}
