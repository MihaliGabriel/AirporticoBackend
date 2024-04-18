package main.controller;

import main.controller.utils.ControllerUtils;
import main.dto.*;
import main.model.Airplane;
import main.model.Language;
import main.model.exceptions.FieldInvalidException;
import main.repository.LanguageRepository;
import main.repository.TextTypeEntityRepository;
import main.service.IAirplaneService;
import main.service.IAirplaneTranslationService;
import main.service.IFlightService;
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

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/api")
public class AirplaneController {

    private static final Logger logger = LoggerFactory.getLogger(AirplaneController.class);


    IAirplaneService airplaneService;
    IAirplaneTranslationService airplaneTranslationService;
    LanguageRepository languageRepository;
    TextTypeEntityRepository textTypeEntityRepository;
    IFlightService flightService;
    ITicketService ticketService;
    private static final String MESSAGE = "message";

    @Autowired
    public AirplaneController(ITicketService ticketService, IAirplaneService airplaneService, IAirplaneTranslationService airplaneTranslationService, LanguageRepository languageRepository, TextTypeEntityRepository textTypeEntityRepository, IFlightService flightService) {
        this.airplaneService = airplaneService;
        this.airplaneTranslationService = airplaneTranslationService;
        this.languageRepository = languageRepository;
        this.textTypeEntityRepository = textTypeEntityRepository;
        this.flightService = flightService;
        this.ticketService = ticketService;
    }
    
    @CrossOrigin(origins = {"http://localhost:4200", "https://thankful-coast-03f536003.4.azurestaticapps.net"})
    @PostMapping("/admin/airplanes")
    public ResponseEntity<Object> getAllAirplanes(@RequestHeader("Language") String language) {
        try {
            List<Airplane> airplanes = airplaneService.getAllAirplanes();
            if (airplanes == null || airplanes.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
            }
            Optional<Language> languageEntity = languageRepository.findByName(language);
            List<AirplaneDTO> airplaneDTOS = ControllerUtils.mapAirplaneListToAirplaneDtoList(textTypeEntityRepository, airplaneTranslationService, languageEntity.orElse(null), airplanes);
            return ResponseEntity.status(HttpStatus.OK).body(airplaneDTOS);
        }
        catch (Exception e) {
            logger.error("Error fetching all airplanes: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap(MESSAGE, e.getMessage()));
        }
    }

    @CrossOrigin(origins = {"http://localhost:4200", "https://thankful-coast-03f536003.4.azurestaticapps.net"})
    @PostMapping("/airplanebyflightname")
    public ResponseEntity<Object> getAirplaneByFlightName(@RequestHeader("Language") String language, @RequestBody FlightDTO flightDTO) {
        try {
            Airplane airplane = airplaneService.getAirplaneByFlightName(flightDTO.getName());
            if (airplane == null) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
            }
            AirplaneDTO airplaneDTO = new AirplaneDTO();
            airplaneDTO.setId(airplane.getId());
            airplaneDTO.setName(airplane.getName());
            airplaneDTO.setRows(airplane.getRows());
            airplaneDTO.setColumns(airplane.getColumns());
            return ResponseEntity.status(HttpStatus.OK).body(airplaneDTO);
        }
        catch(EntityNotFoundException a) {
            logger.error("Error fecthing airplane by flight name: {}", a.getMessage(), a);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap(MESSAGE, a.getMessage()));
        }
        catch (Exception e) {
            logger.error("Error fetching airplane by flight name: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap(MESSAGE, e.getMessage()));
        }
    }

    @CrossOrigin(origins = {"http://localhost:4200", "https://thankful-coast-03f536003.4.azurestaticapps.net"})
    @GetMapping("/admin/airplanes")
    public ResponseEntity<Object> getAirplanes(@RequestHeader("Language") String language, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "4") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Airplane> airplanes = airplaneService.getAllAirplanes(pageable);
            if (airplanes.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
            }
            Optional<Language> languageEntity = languageRepository.findByName(language);
            Page<AirplaneDTO> airplaneDTOS = airplanes.map(airplane -> ControllerUtils.mapAirplaneListToAirplaneDtoList(textTypeEntityRepository, airplaneTranslationService, languageEntity.orElse(null), Collections.singletonList(airplane)).get(0));
            return ResponseEntity.status(HttpStatus.OK).body(airplaneDTOS);
        }
        catch (Exception e) {
            logger.error("Error fetching paginated airplanes: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap(MESSAGE, e.getMessage()));
        }
    }

    @CrossOrigin(origins = {"http://localhost:4200", "https://thankful-coast-03f536003.4.azurestaticapps.net"})
    @PostMapping("/admin/createairplane")
    public ResponseEntity<Object> createAirplane(@Valid @RequestBody AirplaneDTO airplaneDTO) {
        try {
            Airplane airplane = new Airplane();
            airplane.setName(airplaneDTO.getName());
            airplane.setRows(airplaneDTO.getRows());
            airplane.setColumns(airplaneDTO.getColumns());
            airplaneService.addAirplane(airplane);
            airplaneDTO.setId(airplane.getId());
            return ResponseEntity.status(HttpStatus.OK).body(airplaneDTO);
        }
        catch (FieldInvalidException a) {
            logger.error("Field invalid error: {}", a.getMessage(), a);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Collections.singletonMap(MESSAGE, a.getMessage()));
        }
        catch (Exception e) {
            logger.error("Error creating airplane: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap(MESSAGE, e.getMessage()));
        }
    }

    @CrossOrigin(origins = {"http://localhost:4200", "https://thankful-coast-03f536003.4.azurestaticapps.net"})
    @PostMapping("/admin/updateairplane")
    public ResponseEntity<Object> updateAirplane(@Valid @RequestBody AirplaneDTO airplaneDTO) {
        try {
            Airplane airplane = new Airplane();
            airplane.setId(airplaneDTO.getId());
            airplane.setName(airplaneDTO.getName());
            airplane.setRows(airplaneDTO.getRows());
            airplane.setColumns(airplaneDTO.getColumns());
            airplaneService.updateAirplane(airplane);
            return ResponseEntity.status(HttpStatus.OK).body(airplaneDTO);
        }
        catch (FieldInvalidException a) {
            logger.error("Field invalid error: {}", a.getMessage(), a);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Collections.singletonMap(MESSAGE, a.getMessage()));
        }
        catch (Exception e) {
            logger.error("Error updating airplane: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap(MESSAGE, e.getMessage()));
        }
    }

    @CrossOrigin(origins = {"http://localhost:4200", "https://thankful-coast-03f536003.4.azurestaticapps.net"})
    @PostMapping("/admin/deleteairplane")
    public ResponseEntity<Object> deleteAirplane(@RequestHeader("Language") String language, @RequestBody AirplaneDTO airplaneDTO) {
        try {
            airplaneService.deleteAirplaneById(airplaneDTO.getId());
            List<Airplane> airplanes = airplaneService.getAllAirplanes();
            if (airplanes == null || airplanes.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
            }
            Optional<Language> languageEntity = languageRepository.findByName(language);
            List<AirplaneDTO> airplaneDTOS = ControllerUtils.mapAirplaneListToAirplaneDtoList(textTypeEntityRepository, airplaneTranslationService, languageEntity.orElse(null),  airplanes);
            return ResponseEntity.status(HttpStatus.OK).body(airplaneDTOS);
        }
        catch (Exception e) {
            logger.error("Error deleting airplane: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap(MESSAGE, e.getMessage()));
        }
    }

    @CrossOrigin(origins = {"http://localhost:4200", "https://thankful-coast-03f536003.4.azurestaticapps.net"})
    @PostMapping("/reserveairplaneseats")
    public ResponseEntity<Object> reserveAirplaneSeats(@RequestBody BuyTicketDTO buyTicket) {
        try {
            logger.info("BuyTicketDTO : {}", buyTicket);
            //the reserved seats with "+"
            BuyTicketDTO buyTicketDTO = flightService.reserveSeats(buyTicket);
            TicketDTO ticketDTO = ticketService.reserveTicket(buyTicketDTO);

            return ResponseEntity.status(HttpStatus.OK).body(ticketDTO);
        }
        catch(EntityNotFoundException a) {
            logger.error("Could not find flight for this reservation");
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(Collections.singletonMap(MESSAGE, a.getMessage()));
        }
        catch (Exception e) {
            logger.error("Error reserving airplane seat: {}", e.getMessage(), e);
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap(MESSAGE, e.getMessage()));
        }
    }

    @CrossOrigin(origins = {"http://localhost:4200", "https://thankful-coast-03f536003.4.azurestaticapps.net"})
    @PostMapping("/freeairplaneseats")
    public ResponseEntity<Object> freeReservedAirplaneSeats(@RequestBody TicketDTO ticketDTO) {
        try {
            logger.info("Deleting ticket..");
            ticketService.deleteTicketById(ticketDTO.getId());

            return ResponseEntity.status(HttpStatus.OK).body("Ticket deleted succesfully");
        }
        catch(Exception e) {
            logger.error("Error freeing reserved airplane seats: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap(MESSAGE, e.getMessage()));
        }
    }
}
