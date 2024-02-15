package main.service;

import main.dto.LocationDTO;
import main.model.Airport;
import main.model.exceptions.FieldNotUniqueOrNullException;
import main.model.Location;
import main.repository.AirportRepository;
import main.repository.LocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.util.List;

@Service
public class LocationService implements ILocationService {

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private AirportRepository airportRepository;

    @Autowired
    private IAirportService airportService;

    private static final String INSERT = "INSERT";
    private boolean checkUniqueCity(String city, String type) {

        Long count = locationRepository.countByCity(city);

        if (count != 0 && type.equals(INSERT))
            return false;
        if (count > 1 && type.equals("UPDATE")) {
            return false;
        }

        return true;

    }

    private boolean checkUniqueCode(String code, String type) {

        Long count = locationRepository.countByCode(code);

        return (count == 0 || !type.equals(INSERT)) && (count <= 1 || !type.equals("UPDATE"));

    }

    private boolean checkNullCode(String code) {
        return code == null || code.isEmpty();
    }

    private boolean checkNullCity(String name) {
        return name == null || name.isEmpty();
    }

    @Override
    public List<Location> getLocations() {
        return locationRepository.findAll();
    }

    @Override
    public Page<Location> getLocations(Pageable pageable) {
        return locationRepository.findAll(pageable);
    }

    @Override
    public void addLocation(Location location) throws FieldNotUniqueOrNullException {

        if (checkNullCity(location.getCity()))
            throw new FieldNotUniqueOrNullException("City is null");
        if (!checkUniqueCity(location.getCity(), INSERT))
            throw new FieldNotUniqueOrNullException("City is not unique");
        if (checkNullCode(location.getCode()))
            throw new FieldNotUniqueOrNullException("Code is null");
        if (!checkUniqueCode(location.getCode(), INSERT))
            throw new FieldNotUniqueOrNullException("Code is not unique");

        locationRepository.save(location);
    }

    @Override
    public Location getLocationById(Long id) {
        return locationRepository.getById(id);
    }

    /**
     * @Author GXM
     * Stergere cascadata, se sterg mai intai aeroporturile asociate acelei locatii si dupa locatia.
     * @param id
     */
    @Override
    public void deleteLocationById(Long id) throws NullPointerException, MessagingException {

        List<Airport> airports = airportRepository.findAirportByLocationId(id);
        if(airports.isEmpty())
            throw new NullPointerException("Airports is null");
        else {
            for (Airport airport : airports) {
                airportService.deleteAirportById(airport.getId());
            }
            locationRepository.deleteById(id);
        }
    }

    /**
     * @Author GXM
     * Daca vreau sa updatez locatia cu alt oras sau cod, verific doar daca mai exista o intrare in db cu acelasi cod.
     * Daca codul ramane la fel la update, nu fac nimic, deoarece intrarea este valida in db.
     * @param location
     * @throws FieldNotUniqueOrNullException
     */
    @Override
    public void updateLocation(Location location) throws FieldNotUniqueOrNullException {
        Location locationInDB = locationRepository.getById(location.getId());
        if (checkNullCity(location.getCity()))
            throw new FieldNotUniqueOrNullException("City is null");
        if (!locationInDB.getCity().equals(location.getCity()) && !checkUniqueCity(location.getCity(), INSERT))
            throw new FieldNotUniqueOrNullException("City is not unique");
        if (!locationInDB.getCode().equals(location.getCode()) && !checkUniqueCode(location.getCode(), INSERT))
            throw new FieldNotUniqueOrNullException("Code is not unique");

        locationRepository.save(location);
    }

    @Override
    public Location getLocationByCity(String city) {
        return locationRepository.findByCity(city);
    }

    @Override
    public List<Location> searchLocation(LocationDTO locationDTO) {
        String code = locationDTO.getLocationCode() != null ? locationDTO.getLocationCode().toLowerCase() : null;
        String city = locationDTO.getCity() != null ? locationDTO.getCity().toLowerCase() : null;
        String country = locationDTO.getCountry() != null ? locationDTO.getCountry().toLowerCase() : null;

        return locationRepository.search(code, city, country);
    }
}
