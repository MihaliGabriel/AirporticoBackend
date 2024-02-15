package main.service;

import main.dto.BuyTicketDTO;
import main.dto.FlightDTO;;
import main.dto.ReserveSeatDTO;
import main.model.AirplaneSeatType;
import main.model.exceptions.FieldInvalidException;
import main.model.exceptions.FieldNotUniqueOrNullException;
import main.model.Flight;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.mail.MessagingException;
import java.util.List;

 public interface IFlightService {

     Page<Flight> getFlights(Pageable pageable);

     List<Flight> getFlights();

     void addFlight(Flight flight) throws FieldNotUniqueOrNullException, FieldInvalidException;

     Flight getFlightById(Long id);

     Flight getFlightByName(String name);

     Page<Flight> searchFlights(FlightDTO flightDTO, Pageable pageable);

     void updateFlight(Flight flight) throws FieldNotUniqueOrNullException, FieldInvalidException;

     void deleteFlightById(Long id) throws MessagingException;
     AirplaneSeatType[][] generateSeatMap(Flight flight);
     BuyTicketDTO reserveSeats(BuyTicketDTO buyTicketDTO);
}
