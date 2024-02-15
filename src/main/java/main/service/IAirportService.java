package main.service;

import main.dto.AirportDTO;
import main.model.Airport;
import main.model.exceptions.FieldNotUniqueOrNullException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.mail.MessagingException;
import java.util.List;

public interface IAirportService {
    List<Airport> getAirports();

    Page<Airport> getAirports(Pageable pageable);

    void addAirport(Airport airport) throws FieldNotUniqueOrNullException;

    Airport getAirportById(Long id);

    Airport getAirportByName(String name);

    void deleteAirportById(Long id) throws MessagingException;

    void updateAirport(Airport airport) throws FieldNotUniqueOrNullException;


    List<Airport> searchAirport(AirportDTO airportDTO);
}
