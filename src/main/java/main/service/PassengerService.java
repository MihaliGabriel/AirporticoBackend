package main.service;

import main.model.exceptions.FieldInvalidException;
import main.model.Passenger;
import main.model.Ticket;
import main.repository.PassengerRepository;
import main.repository.TicketRepository;
import main.utils.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PassengerService implements IPassengerService {

    @Autowired
    private PassengerRepository passengerRepository;

    @Autowired
    private TicketRepository ticketRepository;

    @Override
    public void addPassenger(Passenger passenger) throws FieldInvalidException {

        if(!Util.checkValidString(passenger.getFirstName()))
            throw new FieldInvalidException("First name is not valid");
        if(!Util.checkValidString(passenger.getLastName()))
            throw new FieldInvalidException("Last name is not valid");
        if(!Util.checkValidString(passenger.getEmail()))
            throw new FieldInvalidException("Email is not valid");
        if(!Util.checkValidString(passenger.getPhoneNumber()))
            throw new FieldInvalidException("Phone number is not valid");

        passengerRepository.save(passenger);
    }

    @Override
    public void deletePassenger(Long passengerId) {
        passengerRepository.deleteById(passengerId);
    }

    @Override
    public void updatePassenger(Passenger passenger) throws FieldInvalidException{

        if(!Util.checkValidString(passenger.getFirstName()))
            throw new FieldInvalidException("First name is not valid");
        if(!Util.checkValidString(passenger.getLastName()))
            throw new FieldInvalidException("Last name is not valid");
        if(!Util.checkValidString(passenger.getEmail()))
            throw new FieldInvalidException("Email is not valid");
        if(!Util.checkValidString(passenger.getPhoneNumber()))
            throw new FieldInvalidException("Phone number is not valid");

        passengerRepository.save(passenger);
    }

    @Override
    public List<Passenger> getPassengers() {
        return passengerRepository.findAll();
    }

    @Override
    public Page<Passenger> getPassengers(Pageable pageable) {
        return passengerRepository.findAll(pageable);
    }

    @Override
    public List<Passenger> getPassengerByTicket(Ticket ticket) {
        return passengerRepository.getPassengersByTicketId(ticket.getId());
    }

    @Override
    public Passenger getPassengerById(Long id) {
        return passengerRepository.getById(id);
    }

    @Override
    public List<Passenger> getPassengersByFlight(Long flightId) {
        List<Ticket> ticketsForFlight = ticketRepository.findTicketsByFlight(flightId);
        List<Passenger> passengersForFlight = new ArrayList<>();
        for (Ticket ticket : ticketsForFlight) {
            List<Passenger> passengersPerFlight = passengerRepository.getPassengersByTicketId(ticket.getId());
            passengersForFlight.addAll(passengersPerFlight);
        }
        return passengersForFlight;
    }
}
