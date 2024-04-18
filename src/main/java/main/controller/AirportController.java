package main.controller;

import main.controller.utils.ControllerUtils;
import main.dto.AirportDTO;
import main.model.*;
import main.repository.LanguageRepository;
import main.repository.TextTypeEntityRepository;
import main.service.DocumentService;
import main.service.IAirportService;
import main.service.IAirportTranslationService;
import main.service.LocationService;
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

import javax.validation.Valid;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/api")
public class AirportController {

    private static final Logger logger = LoggerFactory.getLogger(AirportController.class);
    private static final String MESSAGE = "message";

    IAirportService airportService;
    DocumentService documentService;
    LocationService locationService;
    TextTypeEntityRepository textTypeEntityRepository;
    IAirportTranslationService airportTranslationService;
    LanguageRepository languageRepository;


    @Autowired
    public AirportController(IAirportService airportService, DocumentService documentService, LocationService locationService, TextTypeEntityRepository textTypeEntityRepository, IAirportTranslationService airportTranslationService, LanguageRepository languageRepository) {
        this.airportService = airportService;
        this.documentService = documentService;
        this.locationService = locationService;
        this.textTypeEntityRepository = textTypeEntityRepository;
        this.airportTranslationService = airportTranslationService;
        this.languageRepository = languageRepository;
    }

    /**
     * @Author GXM
     * Tipul metodei este ResponseEntity<Object> deoarece metoda returneaza obiecte de tip DTO, dar si map-uri de erori pentru exceptii,
     * care sunt prelucrate ca si erori de front-end.
     * @return
     * Returns a ResponseEntity<Object>
     */
    @CrossOrigin(origins = {"http://localhost:4200", "https://thankful-coast-03f536003.4.azurestaticapps.net"})
    @PostMapping("/admin/airports")
    public ResponseEntity<Object> getAllAirports(@RequestHeader("Language") String language) {
        try {
            List<Airport> airports = airportService.getAirports();

            if (airports == null || airports.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
            }

            Optional<Language> languageEntity = languageRepository.findByName(language);

            List<AirportDTO> airportDTOS = ControllerUtils.mapAirportListToAirportDtoList(airports, textTypeEntityRepository, airportTranslationService,  languageEntity.orElse(null));
            return ResponseEntity.status(HttpStatus.OK).body(airportDTOS);

        }
        catch (Exception e) {
            logger.error("Error fetching all airports: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap(MESSAGE, e.getMessage()));
        }
    }

    /**
     * @Author GXM
     * @param page pagina curenta care trebuie afisata
     * @param size numarul de elemente afisate pe o pagina
     * @return
     * Returns a ResponseEntity<Object>
     */
    @CrossOrigin(origins = {"http://localhost:4200", "https://thankful-coast-03f536003.4.azurestaticapps.net"})
    @GetMapping("/admin/airports")
    public ResponseEntity<Object> getAllAirports(@RequestHeader("Language") String language, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "4") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Airport> airportsPage = airportService.getAirports(pageable);

            Optional<Language> languageEntity = languageRepository.findByName(language);

            Page<AirportDTO> airportDTOS = ControllerUtils.mapAirportListToAirportDtoList(airportsPage, textTypeEntityRepository, airportTranslationService,  languageEntity.orElse(null));

            return ResponseEntity.status(HttpStatus.OK).body(airportDTOS);
        }
        catch (Exception e) {
            logger.error("Error fetching all paginated airports: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap(MESSAGE, e.getMessage()));
        }
    }

    @CrossOrigin(origins = {"http://localhost:4200", "https://thankful-coast-03f536003.4.azurestaticapps.net"})
    @PostMapping("/admin/searchairports")
    public ResponseEntity<List<AirportDTO>> searchAirports(@RequestHeader("Language") String language, @RequestBody AirportDTO airportDTO) {
        try {
            logger.info("AirportDTO that comes from request searchAirport: {}", airportDTO);
            List<Airport> airports = airportService.searchAirport(airportDTO);
            if (airports == null || airports.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
            }
            Optional<Language> languageEntity = languageRepository.findByName(language);
            List<AirportDTO> airportDTOS = ControllerUtils.mapAirportListToAirportDtoList(airports, textTypeEntityRepository, airportTranslationService,  languageEntity.orElse(null));

            logger.info("Airports list in searchAirport: {}", airportDTOS);
            return ResponseEntity.status(HttpStatus.OK).body(airportDTOS);
        }
        catch (Exception e) {
            logger.error("Error fetching all paginated airports: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/admin/airports/id/{id}")
    public ResponseEntity<AirportDTO> getAirportById(@PathVariable("id") Long id) {
        try {
            Airport airport = airportService.getAirportById(id);

            if (airport == null)
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);

            AirportDTO airportDTO = new AirportDTO();
            airportDTO.setName(airport.getName());
            airportDTO.setCity(airport.getLocation().getCity());

            return ResponseEntity.status(HttpStatus.OK).body(airportDTO);
        }
        catch (Exception e) {
            logger.error("Error fetching airports by id: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/admin/airports/name/{name}")
    public ResponseEntity<AirportDTO> getAirportByName(@PathVariable("name") String name) {
        try {
            Airport airport = airportService.getAirportByName(name);

            if (airport == null)
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);

            AirportDTO airportDTO = new AirportDTO();
            airportDTO.setName(airport.getName());
            airportDTO.setCity(airport.getLocation().getCity());

            return ResponseEntity.status(HttpStatus.OK).body(airportDTO);
        }
        catch (Exception e) {
            logger.error("Error fetching airports by name: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @CrossOrigin(origins = {"http://localhost:4200", "https://thankful-coast-03f536003.4.azurestaticapps.net"})
    @PostMapping("/admin/createairport")
    public ResponseEntity<Object> createAirport(@Valid @RequestBody AirportDTO airportDTO) {
        try {
            Airport airport = new Airport();

            Location location = locationService.getLocationByCity(airportDTO.getCity());

            if (location == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }

            //preparing airport to be saved
            airport.setLocation(location);
            airport.setName(airportDTO.getName());
            airportService.addAirport(airport);


            ControllerUtils.buildAirportDtoFromEntity(airportDTO, airport);

            return ResponseEntity.status(HttpStatus.OK).body(airportDTO);
        }
        catch (Exception e) {
            logger.error("Error creating airports: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap(MESSAGE, e.getMessage()));
        }
    }

    @CrossOrigin(origins = {"http://localhost:4200", "https://thankful-coast-03f536003.4.azurestaticapps.net"})
    @PostMapping("/admin/updateairport")
    public ResponseEntity<Object> updateAirport(@Valid @RequestBody AirportDTO airportDTO) {
        try {
            Airport airport = new Airport();
            Location location = locationService.getLocationByCity(airportDTO.getCity());

            if (location == null)
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);

            airport.setLocation(location);
            airport.setId(airportDTO.getId());
            airport.setName(airportDTO.getName());
            airportService.updateAirport(airport);

            return ResponseEntity.status(HttpStatus.OK).body(airportDTO);
        }
        catch (Exception e) {
            logger.error("Error updating airports: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap(MESSAGE, e.getMessage()));
        }
    }

    @CrossOrigin(origins = {"http://localhost:4200", "https://thankful-coast-03f536003.4.azurestaticapps.net"})
    @PostMapping("/admin/deleteairport")
    public ResponseEntity<Object> deleteAirport(@RequestHeader("Language") String language, @RequestBody AirportDTO airportDTO) {
        try {
            airportService.deleteAirportById(airportDTO.getId());
            List<Airport> airports = airportService.getAirports();
            logger.info("Delete airports");
            if (airports == null || airports.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(Collections.singletonMap(MESSAGE, "no content"));
            }
            Optional<Language> languageEntity = languageRepository.findByName(language);

            List<AirportDTO> airportDTOS = ControllerUtils.mapAirportListToAirportDtoList(airports, textTypeEntityRepository, airportTranslationService,  languageEntity.orElse(null));
            return ResponseEntity.status(HttpStatus.OK).body(airportDTOS);
        }
        catch (Exception e) {
            logger.error("Error deleting airports: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap(MESSAGE, e.getMessage()));
        }
    }

    @CrossOrigin(origins = {"http://localhost:4200", "https://thankful-coast-03f536003.4.azurestaticapps.net"})
    @PostMapping("/admin/exportairportspdf")
    public ResponseEntity<Object> exportAirportToPdf() {
        try {
            List<Airport> airports = airportService.getAirports();

            if (airports == null || airports.isEmpty())
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Airports not found");


            documentService.exportAirportsToPdf(airports);

            return ResponseEntity.status(HttpStatus.OK).body("Airports exported succesfully");
        }
        catch (Exception e) {
            logger.error("Error exporting airports to PDF: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap(MESSAGE, e.getMessage()));
        }
    }

    @CrossOrigin(origins = {"http://localhost:4200", "https://thankful-coast-03f536003.4.azurestaticapps.net"})
    @PostMapping("/admin/exportairportsexcel")
    public ResponseEntity<Object> exportAirportToExcel() {
        try {
            List<Airport> airports = airportService.getAirports();

            if (airports == null || airports.isEmpty())
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Airports not found");

            documentService.exportAirportsToExcel(airports);

            return ResponseEntity.status(HttpStatus.OK).body(Collections.singletonMap(MESSAGE, "Airports exported succesfully"));
        }
        catch (Exception e) {
            logger.error("Error exporting airports to Excel: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap(MESSAGE, e.getMessage()));
        }
    }
}
