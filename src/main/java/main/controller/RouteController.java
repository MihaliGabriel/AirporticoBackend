package main.controller;

import main.dto.RouteDTO;
import main.model.*;
import main.service.*;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/api")
public class RouteController {

    private static final Logger logger = LoggerFactory.getLogger(RouteController.class);
    private static final String MESSAGE = "message";
    
    
    private IRouteService routeService;
    private IAirportService airportService;
    private IPersonService personService;
    private EmailService emailService;

    @Autowired
    public RouteController(IRouteService routeService, IAirportService airportService, IPersonService personService, EmailService emailService) {
        this.routeService = routeService;
        this.airportService = airportService;
        this.personService = personService;
        this.emailService = emailService;
    }

    /**
     * @Author GXM
     * Tipul metodei este ResponseEntity<Object> deoarece metoda returneaza obiecte de tip DTO, dar si map-uri de erori pentru exceptii,
     * care sunt prelucrate ca si erori de front-end.
     * @return
     */
    
    @PostMapping("/admin/routes")
    public ResponseEntity<Object> getAllRoutes(@RequestHeader("Language") String language) {
        try {
            List<Route> routes = routeService.getRoutes();
            List<RouteDTO> routeDTOS = new ArrayList<>();
            if (routes == null || routes.isEmpty())
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);

            for (Route route : routes) {
                Airport arrivalAirport = route.getArrivalAirport();
                Airport departureAirport = route.getDepartureAirport();

                RouteDTO routeDTO = new RouteDTO();

                routeDTO.setId(route.getId());
                routeDTO.setDepartureAirport(departureAirport.getName());
                routeDTO.setArrivalAirport(arrivalAirport.getName());
                routeDTO.setRouteName(arrivalAirport.getName(), departureAirport.getName());
                routeDTOS.add(routeDTO);
            }

            return ResponseEntity.status(HttpStatus.OK).body(routeDTOS);

        }
        catch (Exception e) {
            logger.error("Error fetching all routes: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap(MESSAGE, e.getMessage()));
        }
    }

    /**
     * @Author GXM
     * @param page pagina curenta care trebuie afisata
     * @param size numarul de elemente afisate pe o pagina
     * @return
     */
    
    @GetMapping("/admin/routes")
    public ResponseEntity<Object> getAllRoutes(@RequestHeader("Language") String language, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "4") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Route> routesPage = routeService.getRoutes(pageable);

            Page<RouteDTO> routeDTOS = routesPage.map(route -> {
                Airport arrivalAirport = route.getArrivalAirport();
                Airport departureAirport = route.getDepartureAirport();

                RouteDTO routeDTO = new RouteDTO();

                routeDTO.setId(route.getId());
                routeDTO.setDepartureAirport(departureAirport.getName());
                routeDTO.setArrivalAirport(arrivalAirport.getName());
                routeDTO.setRouteName(arrivalAirport.getName(), departureAirport.getName());
                return routeDTO;
            });

            return ResponseEntity.status(HttpStatus.OK).body(routeDTOS);
        }
        catch (Exception e) {
            logger.error("Error fetching all paginated routes: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap(MESSAGE, e.getMessage()));
        }
    }

    @GetMapping("/admin/routes/id/{id}")
    public ResponseEntity<Object> getRouteById(@PathVariable("id") Long id) {
        try {
            Route route = routeService.getRouteById(id);

            if (route == null)
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);

            Airport arrivalAirport = route.getArrivalAirport();
            Airport departureAirport = route.getDepartureAirport();

            RouteDTO routeDTO = new RouteDTO();

            routeDTO.setId(route.getId());
            routeDTO.setDepartureAirport(departureAirport.getName());
            routeDTO.setArrivalAirport(arrivalAirport.getName());

            return ResponseEntity.status(HttpStatus.OK).body(routeDTO);
        }
        catch (Exception e) {
            logger.error("Error fetching route by id: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap(MESSAGE, e.getMessage()));
        }
    }

    
    @PostMapping("/admin/createroute")
    public ResponseEntity<Object> createRoute(@RequestBody RouteDTO routeDTO) {
        try {
            Airport arrivalAirport = airportService.getAirportByName(routeDTO.getArrivalAirport());
            Airport departureAirport = airportService.getAirportByName(routeDTO.getDepartureAirport());

            if (arrivalAirport == null || departureAirport == null)
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);

            Route route = new Route();

            route.setArrivalAirport(arrivalAirport);
            route.setDepartureAirport(departureAirport);
            routeService.addRoute(route);

            List<Route> routes = routeService.getRoutes();
            if (routes == null || routes.isEmpty())
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);

            routeDTO.setId(route.getId());
            routeDTO.setArrivalAirport(arrivalAirport.getName());
            routeDTO.setDepartureAirport(departureAirport.getName());
            routeDTO.setRouteName(departureAirport.getName(), arrivalAirport.getName());

            return ResponseEntity.status(HttpStatus.OK).body(routeDTO);

        }
        catch (Exception e) {
            logger.error("Error creating route: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap(MESSAGE, e.getMessage()));
        }
    }
    
    @PostMapping("/admin/updateroute")
    public ResponseEntity<Object> updateRoute(@RequestBody RouteDTO routeDTO) {
        try {
            Route route = new Route();
            route.setId(routeDTO.getId());
            Airport arrivalairport = airportService.getAirportByName(routeDTO.getArrivalAirport());
            Airport departureairport = airportService.getAirportByName(routeDTO.getDepartureAirport());

            if (arrivalairport == null || departureairport == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }

            route.setDepartureAirport(departureairport);
            route.setArrivalAirport(arrivalairport);

            routeService.updateRoute(route);
            List<Person> peopleOnRoute = personService.findPersonByRouteId(route.getId());

            for (Person person : peopleOnRoute) {
                String email = person.getEmail();
                emailService.sendRouteChangeToPersonEmail(email);
            }

            routeDTO.setRouteName(route.getDepartureAirport().getName(), route.getArrivalAirport().getName());
            return ResponseEntity.status(HttpStatus.OK).body(routeDTO);

        }
        catch (Exception e) {
            logger.error("Error updating route: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap(MESSAGE, e.getMessage()));
        }
    }
}
