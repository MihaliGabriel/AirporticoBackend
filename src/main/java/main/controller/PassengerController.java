package main.controller;

import main.controller.utils.ControllerUtils;
import main.dto.PassengerDTO;
import main.dto.TicketDTO;
import main.model.*;
import main.model.exceptions.FieldInvalidException;
import main.service.IPassengerService;
import main.service.ITicketService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/api")
public class PassengerController {

    private static final String MESSAGE = "message";
    private static final Logger logger = LoggerFactory.getLogger(PassengerController.class);

    IPassengerService passengerService;
    ITicketService ticketService;

    @Autowired
    public PassengerController(IPassengerService passengerService, ITicketService ticketService) {
        this.passengerService = passengerService;
        this.ticketService = ticketService;
    }

    /**
     * @Author GXM
     * Tipul metodei este ResponseEntity<Object> deoarece metoda returneaza obiecte de tip DTO, dar si map-uri de erori pentru exceptii,
     * care sunt prelucrate ca si erori de front-end.
     * @return
     */
    @CrossOrigin(origins = {"http://localhost:4200", "https://thankful-coast-03f536003.4.azurestaticapps.net"})
    @PostMapping("/passengers")
    public ResponseEntity<Object> getAllPassengers(@RequestHeader("Language") String language) {
        try {
            List<Passenger> passengers = passengerService.getPassengers();
            if (passengers == null || passengers.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
            }
            List<PassengerDTO> passengerDTOS = ControllerUtils.mapPassengerListToPassengerDtoList(passengers);
            return ResponseEntity.status(HttpStatus.OK).body(passengerDTOS);
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap(MESSAGE, e.getMessage()));
        }
    }

    /**
     * @Author GXM
     * @param page pagina curenta care trebuie afisata
     * @param size numarul de elemente afisate pe o pagina
     * @return
     */
    @CrossOrigin(origins = {"http://localhost:4200", "https://thankful-coast-03f536003.4.azurestaticapps.net"})
    @GetMapping("/passengers")
    public ResponseEntity<Object> getAllPassengers(@RequestHeader("Language") String language, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "4") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Passenger> passengersPage = passengerService.getPassengers(pageable);

            Page<PassengerDTO> passengerDTOS = ControllerUtils.mapPassengerListToPassengerDtoList(passengersPage);

            return ResponseEntity.status(HttpStatus.OK).body(passengerDTOS);
        }
        catch (Exception e) {
            logger.error("Error fetching all passengers: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap(MESSAGE, e.getMessage()));
        }
    }

    @CrossOrigin(origins = {"http://localhost:4200", "https://thankful-coast-03f536003.4.azurestaticapps.net"})
    @PostMapping("/passengersbyticket")
    public ResponseEntity<Object> getPassengerByTicket(@RequestBody TicketDTO ticketDTO) {
        try {
            Ticket ticket = ticketService.getTicketById(ticketDTO.getId());

            if (ticket == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            List<Passenger> passengers = passengerService.getPassengerByTicket(ticket);
            if (passengers == null || passengers.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
            }
            List<PassengerDTO> passengerDTOS = ControllerUtils.mapPassengerListToPassengerDtoList(passengers);
            return ResponseEntity.status(HttpStatus.OK).body(passengerDTOS);
        }
        catch (Exception e) {
            logger.error("Error fetching all passengers by ticket: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap(MESSAGE, e.getMessage()));
        }
    }

    @CrossOrigin(origins = {"http://localhost:4200", "https://thankful-coast-03f536003.4.azurestaticapps.net"})
    @PostMapping("/updatepassenger")
    public ResponseEntity<Object> updatePassenger(@RequestBody PassengerDTO passengerDTO) {
        try {
            logger.info("Updating passenger: {}", passengerDTO);

            Passenger passenger = passengerService.getPassengerById(passengerDTO.getId());
            passenger.setFirstName(passengerDTO.getFirstName());
            passenger.setLastName(passengerDTO.getLastName());
            passenger.setEmail(passengerDTO.getEmail());
            passenger.setPhoneNumber(passengerDTO.getPhoneNumber());

            passengerService.updatePassenger(passenger);

            passengerDTO = ControllerUtils.mapPassengerToPassengerDto(passenger);

            return ResponseEntity.status(HttpStatus.OK).body(passengerDTO);
        }
        catch (FieldInvalidException a) {
            logger.error("Field/fields invalid: {}", a.getMessage(), a);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap(MESSAGE, a.getMessage()));

        }
        catch (Exception e) {
            logger.error("Error updating passenger: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap(MESSAGE, e.getMessage()));
        }
    }

    @CrossOrigin(origins = {"http://localhost:4200", "https://thankful-coast-03f536003.4.azurestaticapps.net"})
    @PostMapping("/createpassenger")
    public ResponseEntity<Object> createPassenger(@RequestBody PassengerDTO passengerDTO) {
        try {
            Ticket ticket = ticketService.getTicketById(passengerDTO.getTicketId());

            if (ticket == null)
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);


            Passenger passenger = new Passenger();
            passenger.setFirstName(passengerDTO.getFirstName());
            passenger.setLastName(passengerDTO.getLastName());
            passenger.setEmail(passengerDTO.getEmail());
            passenger.setPhoneNumber(passengerDTO.getPhoneNumber());
            passenger.setTicket(ticketService.getTicketById(ticket.getId()));

            passengerService.addPassenger(passenger);

            List<Passenger> passengers = passengerService.getPassengers();


            if (passengers == null || passengers.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
            }
            passengerDTO = ControllerUtils.mapPassengerToPassengerDto(passenger);

            return ResponseEntity.status(HttpStatus.OK).body(passengerDTO);
        }
        catch (FieldInvalidException a) {
            logger.error("Field/fields invalid: {}", a.getMessage(), a);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap(MESSAGE, a.getMessage()));

        }
        catch (Exception e) {
            logger.error("Error adding passenger: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap(MESSAGE, e.getMessage()));
        }
    }
}
