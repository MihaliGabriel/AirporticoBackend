package main.service;

import main.model.Flight;
import main.model.Route;
import main.repository.FlightRepository;
import main.repository.RouteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.util.List;

@Service
public class RouteService implements IRouteService {

    @Autowired
    private RouteRepository routeRepository;

    @Autowired
    private IFlightService flightService;

    @Autowired
    private FlightRepository flightRepository;

    @Override
    public List<Route> getRoutes() {
        return routeRepository.findAll();
    }

    @Override
    public Page<Route> getRoutes(Pageable pageable) {
        return routeRepository.findAll(pageable);
    }

    @Override
    public void addRoute(Route route) {
        routeRepository.save(route);
    }

    @Override
    public Route getRouteById(Long id) {
        return routeRepository.getById(id);
    }

    @Override
    public void updateRoute(Route route) {
        routeRepository.save(route);
    }

    /**
     * @Author GXM
     * Se sterg mai intai zborurile asociate rutei, si dupa ruta.
     * @param id
     */
    public void deleteRouteById(Long id) throws MessagingException {
        List<Flight> flights = flightRepository.findFlightsByRouteId(id);
        if (!flights.isEmpty()) {
            for (Flight flight : flights) {
                flightService.deleteFlightById(flight.getId());
            }
        }
        routeRepository.deleteById(id);
    }
}
