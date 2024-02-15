package main.controller;

import main.controller.utils.ControllerUtils;
import main.dto.LocationDTO;
import main.model.Language;
import main.model.exceptions.FieldNotUniqueOrNullException;
import main.model.Location;

import main.repository.LanguageRepository;
import main.repository.TextTypeEntityRepository;
import main.service.ILocationService;
import main.service.ILocationTranslationService;
import org.apache.commons.codec.language.bm.Lang;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class LocationController {


    ILocationService locationService;

    @Autowired
    ILocationTranslationService locationTranslationService;

    @Autowired
    private TextTypeEntityRepository textTypeEntityRepository;

    @Autowired
    private LanguageRepository languageRepository;
    private static final String MESSAGE = "message";
    private static final Logger logger = LoggerFactory.getLogger(LocationController.class);

    @Autowired
    public LocationController(ILocationService locationService) {
        this.locationService = locationService;
    }

    /**
     * @Author GXM
     * Tipul metodei este ResponseEntity<Object> deoarece metoda returneaza obiecte de tip DTO, dar si map-uri de erori pentru exceptii,
     * care sunt prelucrate ca si erori de front-end.
     * @return
     */
    @CrossOrigin(origins = "http://localhost:4200")
    @PostMapping("/locations")
    public ResponseEntity<Object> getAllLocations(@RequestHeader("Language") String language) {
        try {
            List<Location> locations = locationService.getLocations();

            if (locations == null || locations.isEmpty())
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);

            Optional<Language> languageEntity = languageRepository.findByName(language);

            List<LocationDTO> locationDTOS = ControllerUtils.mapLocationListToLocationDtoList(textTypeEntityRepository, locationTranslationService, languageEntity.orElse(null), locations);

            return ResponseEntity.ok().body(locationDTOS);
        }
        catch (Exception e) {
            logger.error("Error fetching all locations: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Collections.singletonMap(MESSAGE, e.getMessage()));
        }
    }

    /**
     * @Author GXM
     * @param page pagina curenta care trebuie afisata
     * @param size numarul de elemente afisate pe o pagina
     * @return
     */
    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/locations")
    public ResponseEntity<Object> getAllLocations(@RequestHeader("Language") String language, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "4") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Location> locationsPage = locationService.getLocations(pageable);

            Optional<Language> languageEntity = languageRepository.findByName(language);
            Page<LocationDTO> locationDTOS = ControllerUtils.mapLocationListToLocationDtoList(textTypeEntityRepository, locationTranslationService, languageEntity.orElse(null), locationsPage);

            return ResponseEntity.status(HttpStatus.OK).body(locationDTOS);
        }
        catch (Exception e) {
            logger.error("Error fetching all paginated locations: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Collections.singletonMap(MESSAGE, e.getMessage()));
        }
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @PostMapping("/admin/createlocation")
    public ResponseEntity<Object> createLocation(@RequestHeader("Language") String language, @RequestBody LocationDTO locationDTO) {
        try {
            logger.info("LocationDTO: {}", locationDTO);

            Location location = ControllerUtils.mapLocationDtoToEntity(locationDTO);
            locationService.addLocation(location);

            Optional<Language> languageEntity = languageRepository.findByName(language);

            locationDTO = ControllerUtils.mapLocationToLocationDto(location, textTypeEntityRepository, locationTranslationService,  languageEntity.orElse(null));

            return ResponseEntity.ok().body(locationDTO);
        }
        catch (FieldNotUniqueOrNullException a) {
            return ResponseEntity.internalServerError().body(Collections.singletonMap(MESSAGE, a.getMessage()));
        }
        catch (Exception e) {
            logger.error("Error adding location: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Collections.singletonMap(MESSAGE, e.getMessage()));
        }
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @PostMapping("/admin/updatelocation")
    public ResponseEntity<Object> updateLocation(@RequestHeader("Language") String language, @RequestBody LocationDTO locationDTO) {
        try {

            Location location = ControllerUtils.mapLocationDtoToEntity(locationDTO);

            //setam id-ul pentru a putea face update-ul
            location.setId(locationDTO.getId());
            locationService.updateLocation(location);

            Optional<Language> languageEntity = languageRepository.findByName(language);

            locationDTO = ControllerUtils.mapLocationToLocationDto(location, textTypeEntityRepository, locationTranslationService,  languageEntity.orElse(null));

            return ResponseEntity.ok().body(locationDTO);
        }
        catch (FieldNotUniqueOrNullException a) {
            logger.error("Field not unique or null: {}", a.getMessage(), a);
            return ResponseEntity.internalServerError().body(Collections.singletonMap(MESSAGE, a.getMessage()));
        }
        catch (Exception e) {
            logger.error("Error updating location: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Collections.singletonMap(MESSAGE, e.getMessage()));
        }
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @PostMapping("/admin/deletelocation")
    public ResponseEntity<Object> deleteLocation(@RequestHeader("Language") String language, @RequestBody LocationDTO locationDTO) {
        try {
            locationService.deleteLocationById(locationDTO.getId());
            List<Location> locations = locationService.getLocations();
            logger.info("Delete location");
            if (locations == null || locations.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
            }
            Optional<Language> languageEntity = languageRepository.findByName(language);
            List<LocationDTO> locationDTOS = ControllerUtils.mapLocationListToLocationDtoList(textTypeEntityRepository, locationTranslationService, languageEntity.orElse(null), locations);
            return ResponseEntity.status(HttpStatus.OK).body(locationDTOS);
        }
        catch (Exception e) {
            return ResponseEntity.internalServerError().body(Collections.singletonMap(MESSAGE, e.getMessage()));
        }
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @PostMapping("/admin/searchlocations")
    public ResponseEntity<Object> searchLocations(@RequestHeader("Language") String language, @RequestBody LocationDTO locationDTO) {
        try {
            List<Location> locations = locationService.searchLocation(locationDTO);

            if (locations == null || locations.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
            }

            Optional<Language> languageEntity = languageRepository.findByName(language);
            List<LocationDTO> locationDTOS = ControllerUtils.mapLocationListToLocationDtoList(textTypeEntityRepository, locationTranslationService, languageEntity.orElse(null), locations);
            return ResponseEntity.status(HttpStatus.OK).body(locationDTOS);
        }
        catch (Exception e) {
            logger.error("Error searching locations: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Collections.singletonMap(MESSAGE, e.getMessage()));
        }
    }

}
