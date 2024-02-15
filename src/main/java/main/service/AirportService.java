package main.service;

import main.dto.AirportDTO;
import main.model.*;
import main.model.exceptions.FieldNotUniqueOrNullException;
import main.repository.AirportRepository;
import main.repository.RouteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.util.List;

@Service
public class AirportService implements IAirportService {

    @Autowired
    AirportRepository airportRepository;

    @Autowired
    RouteRepository routeRepository;

    @Autowired
    IRouteService routeService;

    private static final String INSERT = "INSERT";
    private static final String UPDATE = "UPDATE";
    private static final Logger logger = LoggerFactory.getLogger(AirportService.class);
    private boolean checkUniqueName(String name, String type) {

        Long count = airportRepository.countByName(name);
        if (count != 0 && type.equals(INSERT))
            return false;
        if (count > 1 && type.equals(UPDATE)) {
            return false;
        }

        return true;
    }

    private boolean checkNullName(String name) {
        return name == null;
    }

    private boolean checkNullLocation(Location location) {
        return location == null;
    }

    @Override
    public List<Airport> getAirports() {
        return airportRepository.findAll();
    }

    @Override
    public Page<Airport> getAirports(Pageable pageable) {
        return airportRepository.findAll(pageable);
    }

    @Override
    public void addAirport(Airport airport) throws FieldNotUniqueOrNullException {

        if (checkNullName(airport.getName())) {
            throw new FieldNotUniqueOrNullException("Name is null");
        }
        if (!checkUniqueName(airport.getName(), INSERT)) {
            throw new FieldNotUniqueOrNullException("Name is not unique");
        }
        if(checkNullLocation(airport.getLocation())) {
            throw new FieldNotUniqueOrNullException("Location is null");
        }

        airportRepository.save(airport);
    }

    @Override
    public Airport getAirportById(Long id) {
        return airportRepository.getById(id);
    }

    @Override
    public Airport getAirportByName(String name) {
        return airportRepository.findByName(name);
    }

    /**
     * @Author
     * Stergere cascadata, mai intai se sterg rutele asociate aeroportului si dupa aeroportul.
     * @param id
     */
    @Override
    public void deleteAirportById(Long id) throws MessagingException {
        Airport airport = airportRepository.getById(id);
        List<Route> routes = routeRepository.findRoutesByAirportId(airport.getName(), airport.getName());
        if (!routes.isEmpty()) {
            for (Route route : routes) {
                routeService.deleteRouteById(route.getId());
            }
        }
        airportRepository.deleteById(id);
    }

    /**
     * @Author GXM
     * Daca vreau sa updatez aeroportul cu alt nume, verific doar daca mai exista o intrare in db cu acelasi nume.
     * Daca numele ramane la fel la update, nu fac nimic, deoarece intrarea este valida in db.
     * @param airport
     * @throws FieldNotUniqueOrNullException
     */
    @Override
    public void updateAirport(Airport airport) throws FieldNotUniqueOrNullException {

        Airport airportFromDb = airportRepository.getById(airport.getId());
        if (checkNullName(airport.getName())) {
            throw new FieldNotUniqueOrNullException("Name is null");
        }
        if (!airportFromDb.getName().equals(airport.getName()) && !checkUniqueName(airport.getName(), INSERT)) {
            throw new FieldNotUniqueOrNullException("Name is not unique");
        }
        airportRepository.save(airport);

    }


    @Override
    public List<Airport> searchAirport(AirportDTO airportDTO) {

        String airportName = airportDTO.getName() != null ? airportDTO.getName().toLowerCase() : null;
        String airportCity = airportDTO.getCity() != null ? airportDTO.getCity().toLowerCase() : null;

        return airportRepository.search(airportName, airportCity);
    }

}
