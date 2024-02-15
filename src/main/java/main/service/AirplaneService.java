package main.service;

import main.model.Airplane;
import main.model.exceptions.FieldInvalidException;
import main.repository.AirplaneRepository;
import main.utils.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Service
public class AirplaneService implements IAirplaneService {

    @Autowired
    private AirplaneRepository airplaneRepository;

    @Override
    public List<Airplane> getAllAirplanes() {
        return airplaneRepository.findAll();
    }

    @Override
    public Page<Airplane> getAllAirplanes(Pageable pageable) {
        return airplaneRepository.findAll(pageable);
    }



    public Airplane getAirplaneByFlightName(String flightName) {
        Airplane airplane = airplaneRepository.findByFlightName(flightName);

        if(airplane == null)
            throw new EntityNotFoundException("Airplane with name" + flightName + "not found");

        return airplaneRepository.findByFlightName(flightName);
    }

    @Override
    public void addAirplane(Airplane airplane) throws FieldInvalidException {
        if(!Util.checkValidInteger(airplane.getColumns()) || Util.checkValidInteger(airplane.getRows()))
            throw new FieldInvalidException("Columns/rows is null");

        airplaneRepository.save(airplane);
    }

    @Override
    public void updateAirplane(Airplane airplane) throws FieldInvalidException {
        if(!Util.checkValidInteger(airplane.getColumns()) || Util.checkValidInteger(airplane.getRows()))
            throw new FieldInvalidException("Columns/rows is null");

        airplaneRepository.save(airplane);
    }

    @Override
    public void deleteAirplaneById(Long airplaneId) {
        airplaneRepository.deleteById(airplaneId);
    }

    @Override
    public Airplane getAirplaneByName(String airplaneName) {
        return airplaneRepository.findByName(airplaneName);
    }
}
