package main.service;

import main.model.exceptions.FieldInvalidException;
import main.model.Passenger;
import main.model.Ticket;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IPassengerService {
    List<Passenger> getPassengers();

    Page<Passenger> getPassengers(Pageable pageable);

    List<Passenger> getPassengerByTicket(Ticket ticket);

    List<Passenger> getPassengersByFlight(Long flightId);

    Passenger getPassengerById(Long id);

    void addPassenger(Passenger passenger) throws FieldInvalidException;

    void deletePassenger(Long passengerId);

    void updatePassenger(Passenger passenger) throws FieldInvalidException;
}
