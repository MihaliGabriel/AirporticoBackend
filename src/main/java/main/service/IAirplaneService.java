package main.service;

import main.model.Airplane;
import main.model.exceptions.FieldInvalidException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IAirplaneService {

    List<Airplane> getAllAirplanes();

    Page<Airplane> getAllAirplanes(Pageable pageable);

    Airplane getAirplaneByFlightName(String flightName);

    void addAirplane(Airplane airplane) throws FieldInvalidException;

    void updateAirplane(Airplane airplane) throws FieldInvalidException;

    void deleteAirplaneById(Long airplaneId);

    Airplane getAirplaneByName(String airplaneName);

}
