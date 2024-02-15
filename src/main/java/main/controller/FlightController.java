package main.controller;

import main.controller.utils.ControllerUtils;
import main.dto.FlightDTO;
import main.model.*;
import main.model.exceptions.FieldInvalidException;
import main.model.exceptions.FieldNotUniqueOrNullException;
import main.repository.LanguageRepository;
import main.repository.TextTypeEntityRepository;
import main.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.xml.bind.ValidationException;
import java.util.*;

@RestController
@RequestMapping("/api")
public class FlightController {

    private static final String MESSAGE = "message";
    
    private IFlightService flightService;
    private IRouteService routeService;
    private ICompanyService companyService;
    private DocumentService documentService;
    private IAirplaneService airplaneService;
    private TextTypeEntityRepository textTypeEntityRepository;
    private ILocationTranslationService locationTranslationService;
    private LanguageRepository languageRepository;

    private static final Logger logger = LoggerFactory.getLogger(FlightController.class);

    @Autowired
    public FlightController(IFlightService flightService, IRouteService routeService, ICompanyService companyService, DocumentService documentService, IAirplaneService airplaneService, TextTypeEntityRepository textTypeEntityRepository, ILocationTranslationService locationTranslationService, LanguageRepository languageRepository) {
        this.flightService = flightService;
        this.routeService = routeService;
        this.companyService = companyService;
        this.documentService = documentService;
        this.airplaneService = airplaneService;
        this.textTypeEntityRepository = textTypeEntityRepository;
        this.locationTranslationService = locationTranslationService;
        this.languageRepository = languageRepository;
    }

    /**
     * @Author GXM
     * Tipul metodei este ResponseEntity<Object> deoarece metoda returneaza obiecte de tip DTO, dar si map-uri de erori pentru exceptii,
     * care sunt prelucrate ca si erori de front-end.
     * @return
     */
    @CrossOrigin(origins = "http://localhost:4200")
    @PostMapping("/admin/flights")
    public ResponseEntity<Object> getFlights(@RequestHeader("Language") String language) {
        try {
            List<Flight> flights = flightService.getFlights();

            if (flights == null || flights.isEmpty())
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);

            logger.info("Flights list: {}", flights);
            List<FlightDTO> flightDTOS = new ArrayList<>();

            Optional<Language> languageEntity = languageRepository.findByName(language);

            for (Flight flight : flights) {
                FlightDTO flightDTO = ControllerUtils.mapFlightToFlightDTO(flight, textTypeEntityRepository, locationTranslationService, languageEntity.orElse(null));

                flightDTOS.add(flightDTO);

                logger.info("Flight duration: {}", flight.getDuration());
            }
            return ResponseEntity.status(HttpStatus.OK).body(flightDTOS);
        }
        catch (Exception e) {
            logger.error("Error fetching all flights: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @PostMapping("/flights")
    public ResponseEntity<Object> getFlightsUser(@RequestHeader("Language") String language) {
        try {
            List<Flight> flights = flightService.getFlights();

            if (flights == null || flights.isEmpty())
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);

            logger.info("Flights list: {}", flights);
            List<FlightDTO> flightDTOS = new ArrayList<>();
            Optional<Language> languageEntity = languageRepository.findByName(language);

            for (Flight flight : flights) {
                FlightDTO flightDTO = ControllerUtils.mapFlightToFlightDTO(flight, textTypeEntityRepository, locationTranslationService, languageEntity.orElse(null));

                flightDTOS.add(flightDTO);

                logger.info("Flight duration: {}", flight.getDuration());
            }
            return ResponseEntity.status(HttpStatus.OK).body(flightDTOS);
        }
        catch (Exception e) {
            logger.error("Error fetching all flights: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * @Author GXM
     * @param page pagina curenta care trebuie afisata
     * @param size numarul de elemente afisate pe o pagina
     * @return
     */
    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/admin/flights")
    public ResponseEntity<Object> getFlights(@RequestHeader("Language") String language, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "4") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Flight> flightPage = flightService.getFlights(pageable);

            Optional<Language> languageEntity = languageRepository.findByName(language);
            Page<FlightDTO> flightDTOS = flightPage.map(flight -> ControllerUtils.mapFlightToFlightDTO(flight, textTypeEntityRepository, locationTranslationService, languageEntity.orElse(null)));


            return ResponseEntity.status(HttpStatus.OK).body(flightDTOS);
        }
        catch (Exception e) {
            logger.error("Error fetching all paginated flights: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * @Author GXM
     * @param page pagina curenta care trebuie afisata
     * @param size numarul de elemente afisate pe o pagina
     * @return
     */
    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/flights")
    public ResponseEntity<Object> getFlightsUser(@RequestHeader("Language") String language, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "4") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Flight> flightPage = flightService.getFlights(pageable);

            Optional<Language> languageEntity = languageRepository.findByName(language);
            Page<FlightDTO> flightDTOS = flightPage.map(flight -> ControllerUtils.mapFlightToFlightDTO(flight, textTypeEntityRepository, locationTranslationService, languageEntity.orElse(null)));

            return ResponseEntity.status(HttpStatus.OK).body(flightDTOS);
        }
        catch (Exception e) {
            logger.error("Error fetching all paginated flights: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/admin/flights/id/{id}")
    public ResponseEntity<Object> getFlightsById(@RequestHeader("Language") String language, @PathVariable("id") Long id) {
        try {
            Flight flight = flightService.getFlightById(id);

            if (flight == null)
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);

            Optional<Language> languageEntity = languageRepository.findByName(language);
            FlightDTO flightDTO = ControllerUtils.mapFlightToFlightDTO(flight, textTypeEntityRepository, locationTranslationService, languageEntity.orElse(null));

            return ResponseEntity.status(HttpStatus.OK).body(flightDTO);
        }
        catch (Exception e) {
            logger.error("Error fetching all flights by id: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @CrossOrigin("http://localhost:4200")
    @PostMapping("/getseatmap")
    private ResponseEntity<Object> getSeatMap(@RequestBody FlightDTO flightDTO) {
        try {
            Flight flight = flightService.getFlightByName(flightDTO.getName());

            if(flight == null)
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);

            AirplaneSeatType[][] airplaneSeats = flightService.generateSeatMap(flight);
            boolean[][] flightSeats = ControllerUtils.mapSeatMapToOccupiedSeatsList(airplaneSeats);

            return ResponseEntity.status(HttpStatus.OK).body(flightSeats);
        }
        catch(Exception e) {
            logger.error("Error fetching seat map: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/admin/flights/name/{name}")
    public ResponseEntity<Object> getFlightsByName(@RequestHeader("Language") String language, @PathVariable("name") String name) {
        try {
            Flight flight = flightService.getFlightByName(name);

            if (flight == null)
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);

            Optional<Language> languageEntity = languageRepository.findByName(language);
            FlightDTO flightDTO = ControllerUtils.mapFlightToFlightDTO(flight, textTypeEntityRepository, locationTranslationService, languageEntity.orElse(null));

            return ResponseEntity.status(HttpStatus.OK).body(flightDTO);
        }
        catch (Exception e) {
            logger.error("Error fetching all flights by name: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @PostMapping("/admin/createflight")
    public ResponseEntity<Object> createFlight(@RequestHeader("Langugage") String language, @RequestBody FlightDTO flightDTO) {
        try {
            Flight flight = new Flight();

            Route route = routeService.getRouteById(flightDTO.getRouteId());
            Company company = companyService.getCompanyById(flightDTO.getCompanyId());
            Airplane airplane = airplaneService.getAirplaneByName(flightDTO.getAirplaneName());

            logger.info("FlightDTO: airplane name: {}, departure date: {}, arrival date: {}", flightDTO.getAirplaneName(), flightDTO.getDepartureDate(), flightDTO.getArrivalDate());

            if (route == null || company == null)
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);

            if (!flightDTO.getDepartureDate().isBefore(flightDTO.getArrivalDate())) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Collections.singletonMap(MESSAGE, "Departure date should be before the arrival date."));
            }

            int departureHour = flightDTO.getDepartureDate().getHour();
            int departureMinute = flightDTO.getDepartureDate().getMinute();

            Integer airplaneSeats = (airplane.getColumns() * airplane.getRows());

            //60% economy seats, 30% first class seats, 10% business seats
            Integer economySeats = (int) (0.6 * airplaneSeats);
            Integer firstClassSeats = (int) (0.3 * airplaneSeats);
            Integer businessSeats = airplaneSeats - (economySeats + firstClassSeats);

            logger.info("Airplane calculated seats: economy seats: {}, first class seats: {}, business seats: {}", economySeats, firstClassSeats, businessSeats);

            String formattedHour = String.format("%02d", departureHour);
            String formattedMinute = String.format("%02d", departureMinute);

            String formattedTime = formattedHour + formattedMinute;

            flight.setFirstClassPrice(flightDTO.getFirstClassPrice());
            flight.setFirstClassSeats(firstClassSeats);
            flight.setRemainingFirstClassSeats(firstClassSeats);
            flight.setArrivalDate(flightDTO.getArrivalDate());
            flight.setDepartureDate(flightDTO.getDepartureDate());
            flight.computeDuration();
            flight.computeDiscount();
            flight.setName(company.getCode() + route.getDepartureAirport().getLocation().getCode() + route.getArrivalAirport().getLocation().getCode() + formattedTime);
            flight.setBusinessSeats(businessSeats);
            flight.setEconomySeats(economySeats);
            flight.setRemainingBusinessSeats(businessSeats);
            flight.setRemainingEconomySeats(economySeats);
            flight.setRoute(route);
            flight.setCompany(company);
            flight.setAirplane(airplane);
            flight.setBusinessPrice(flightDTO.getBusinessPrice());
            flight.setEconomyPrice(flightDTO.getEconomyPrice());

            flightService.addFlight(flight);

            logger.info("Discount percentage: {}", flight.getDiscountPercentage());

            Optional<Language> languageEntity = languageRepository.findByName(language);
            flightDTO = ControllerUtils.mapFlightToFlightDTO(flight, textTypeEntityRepository, locationTranslationService, languageEntity.orElse(null));

            return ResponseEntity.status(HttpStatus.OK).body(flightDTO);

        }
        catch (FieldNotUniqueOrNullException a) {
            logger.error("Field not unique or null: {}", a.getMessage(), a);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap(MESSAGE, a.getMessage()));
        }
        catch(FieldInvalidException b) {
            logger.error("Field invalid: {}", b.getMessage(), b);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap(MESSAGE, b.getMessage()));
        }
        catch (Exception e) {
            logger.error("Error creating flight: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap(MESSAGE, e.getMessage()));
        }
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @PostMapping("/admin/updateflight")
    public ResponseEntity<Object> updateFlight(@RequestHeader("Langugage") String language, @RequestBody FlightDTO flightDTO) {
        try {
            Route route = routeService.getRouteById(flightDTO.getRouteId());
            Company company = companyService.getCompanyById(flightDTO.getCompanyId());
            logger.info("Company from flightDTO: {}", company);

            Flight flight = new Flight();
            if (route == null)
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);

            if (!flightDTO.getDepartureDate().isBefore(flightDTO.getArrivalDate())) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Collections.singletonMap(MESSAGE, "Departure date should be before the arrival date."));
            }

            int departureHour = flightDTO.getDepartureDate().getHour();
            int departureMinute = flightDTO.getDepartureDate().getMinute();

            String formattedHour = String.format("%02d", departureHour);
            String formattedMinute = String.format("%02d", departureMinute);

            String formattedTime = formattedHour + formattedMinute;

            flight.setId(flightDTO.getId());
            flight.setArrivalDate(flightDTO.getArrivalDate());
            flight.setDepartureDate(flightDTO.getDepartureDate());
            flight.setCompany(company);
            flight.computeDiscount();
            flight.computeDuration();
            flight.setName(company.getCode() + route.getDepartureAirport().getLocation().getCode() + route.getArrivalAirport().getLocation().getCode() + formattedTime);
            flight.setBusinessSeats(flightDTO.getBusinessSeats());
            flight.setEconomySeats(flightDTO.getEconomySeats());
            flight.setFirstClassSeats(flightDTO.getFirstClassSeats());
            flight.setRoute(route);
            flight.setBusinessPrice(flightDTO.getBusinessPrice());
            flight.setEconomyPrice(flightDTO.getEconomyPrice());
            flight.setFirstClassPrice(flightDTO.getFirstClassPrice());
            flight.setRemainingEconomySeats(flightDTO.getRemainingEconomySeats());
            flight.setRemainingBusinessSeats(flightDTO.getRemainingBusinessSeats());
            flight.setRemainingFirstClassSeats(flightDTO.getRemainingFirstClassSeats());

            flightService.updateFlight(flight);

            Optional<Language> languageEntity = languageRepository.findByName(language);
            flightDTO = ControllerUtils.mapFlightToFlightDTO(flight, textTypeEntityRepository, locationTranslationService, languageEntity.orElse(null));

            return ResponseEntity.status(HttpStatus.OK).body(flightDTO);

        }
        catch (FieldNotUniqueOrNullException a) {
            logger.error("Field not unique or null: {}", a.getMessage(), a);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap(MESSAGE, a.getMessage()));
        }
        catch(FieldInvalidException b) {
            logger.error("Field invalid: {}", b.getMessage(), b);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap(MESSAGE, b.getMessage()));
        }
        catch (Exception e) {
            logger.error("Error creating flight: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap(MESSAGE, e.getMessage()));
        }
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @PostMapping("/admin/searchflights")
    public ResponseEntity<Object> searchFlights(@RequestHeader("Language") String language, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "4") int size, @RequestBody FlightDTO flightDTO) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            logger.info("FlightDTO: {}", flightDTO);

            Page<Flight> flightPage = flightService.searchFlights(flightDTO, pageable);
            Optional<Language> languageEntity = languageRepository.findByName(language);
            Page<FlightDTO> flightDTOS = flightPage.map(flight -> ControllerUtils.mapFlightToFlightDTO(flight, textTypeEntityRepository, locationTranslationService, languageEntity.orElse(null)));

            return ResponseEntity.status(HttpStatus.OK).body(flightDTOS);
        }
        catch (Exception e) {
            logger.error("Error searching flights: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @PostMapping("/admin/exportflightspdf")
    public ResponseEntity<String> exportFlightToPdf(@RequestHeader("Langugage") String language) {
        try {
            List<Flight> flights = flightService.getFlights();

            if (flights == null || flights.isEmpty())
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Flights not found");

            List<FlightDTO> flightDTOS = new ArrayList<>();
            FlightDTO flightDTOElement;
            Optional<Language> languageEntity = languageRepository.findByName(language);

            for (Flight flight : flights) {
                flightDTOElement = ControllerUtils.mapFlightToFlightDTO(flight, textTypeEntityRepository, locationTranslationService, languageEntity.orElse(null));

                flightDTOS.add(flightDTOElement);
            }

            documentService.exportFlightsToPdf(flightDTOS);
            return ResponseEntity.status(HttpStatus.OK).body("Flights exported succesfully");
        }
        catch (Exception e) {
            logger.error("Error exporting flights to pdf: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @PostMapping("/admin/exportflightsexcel")
    public ResponseEntity<String> exportFlightToExcel(@RequestHeader("Langugage") String language) {
        try {
            List<Flight> flights = flightService.getFlights();

            if (flights == null || flights.isEmpty())
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Flights not found");

            List<FlightDTO> flightDTOS = new ArrayList<>();
            FlightDTO flightDTOElement;
            Optional<Language> languageEntity = languageRepository.findByName(language);

            for (Flight flight : flights) {
                flightDTOElement = ControllerUtils.mapFlightToFlightDTO(flight, textTypeEntityRepository, locationTranslationService, languageEntity.orElse(null));

                flightDTOS.add(flightDTOElement);
            }

            documentService.exportFlightsToExcel(flights);
            return ResponseEntity.status(HttpStatus.OK).body("Flights exported succesfully");
        }
        catch (Exception e) {
            logger.error("Error exporting flight to excel: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @PostMapping("/admin/importflightsexcel")
    public ResponseEntity<Object> importFlightsFromExcel() {
        try {
            documentService.importFlightsFromExcel("flights.xlsx");
            return ResponseEntity.status(HttpStatus.OK).body(Collections.singletonMap(MESSAGE, "Flights imported succesfully"));
        }
        catch (ValidationException a) {
            logger.error("Validation error: {}", a.getMessage(), a);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap(MESSAGE, a.getMessage()));
        }
        catch (Exception e) {
            logger.error("Error importing flights from excel: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap(MESSAGE, e.getMessage()));
        }
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @PostMapping("/admin/deleteflight")
    public ResponseEntity<Object> deleteFlight(@RequestHeader("Langugage") String language, @RequestBody FlightDTO flightDTO) {
        try {
            flightService.deleteFlightById(flightDTO.getId());
            List<Flight> flights = flightService.getFlights();
            logger.info("Delete flight");
            if (flights == null || flights.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
            }
            List<FlightDTO> flightDTOS = new ArrayList<>();
            FlightDTO flightDTOElement;

            Optional<Language> languageEntity = languageRepository.findByName(language);

            for (Flight flight : flights) {
                flightDTOElement = ControllerUtils.mapFlightToFlightDTO(flight, textTypeEntityRepository, locationTranslationService, languageEntity.orElse(null));

                flightDTOS.add(flightDTOElement);
            }

            return ResponseEntity.status(HttpStatus.OK).body(flightDTOS);
        }
        catch (Exception e) {
            logger.error("Error when deleting flight: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @PostMapping("/cheapestflights")
    public ResponseEntity<Object> getCheapestFlights(@RequestHeader("Language") String language) {
        try {
            List<Flight> flights = flightService.getFlights();

            if (flights == null || flights.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(Collections.singletonMap(MESSAGE, "No flights are available right now"));
            }

            flights.sort(Comparator.comparingDouble(Flight::getEconomyPrice));

            List<Flight> cheapestFlights = new ArrayList<>();
            List<FlightDTO> cheapestFlightDTOS = new ArrayList<>();

            System.out.println(flights);
            if (flights.size() <= 3) {
                for (Flight flight : flights) {
                    cheapestFlights.add(flight);
                }
            } else {
                cheapestFlights.add(flights.get(0));
                cheapestFlights.add(flights.get(1));
                cheapestFlights.add(flights.get(2));
            }

            FlightDTO flightDTOElement;
            Optional<Language> languageEntity = languageRepository.findByName(language);

            for (Flight flight : cheapestFlights) {
                flightDTOElement = ControllerUtils.mapFlightToFlightDTO(flight, textTypeEntityRepository, locationTranslationService, languageEntity.orElse(null));

                cheapestFlightDTOS.add(flightDTOElement);
            }
            logger.info("Cheapest flight list translated: {}", cheapestFlightDTOS);
            return ResponseEntity.ok().body(cheapestFlightDTOS);

        }
        catch (Exception e) {
            logger.error("Error when fetching cheapest flights: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Collections.singletonMap(MESSAGE, e.getMessage()));
        }

    }
}
