package main.service;

import main.dto.LocationDTO;
import main.model.exceptions.FieldNotUniqueOrNullException;
import main.model.Location;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.mail.MessagingException;
import java.util.List;

public interface ILocationService {

    List<Location> getLocations();

    Page<Location> getLocations(Pageable pageable);

    void addLocation(Location location) throws FieldNotUniqueOrNullException;

    Location getLocationById(Long id);

    void deleteLocationById(Long id) throws MessagingException;

    void updateLocation(Location location) throws FieldNotUniqueOrNullException;

    Location getLocationByCity(String city);

    List<Location> searchLocation(LocationDTO locationDTO);
}
