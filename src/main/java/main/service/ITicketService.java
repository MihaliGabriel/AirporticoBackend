package main.service;

import main.dto.BuyTicketDTO;
import main.dto.TicketDTO;
import main.model.Ticket;
import main.model.exceptions.FieldInvalidException;
import main.model.exceptions.FieldNotUniqueOrNullException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.persistence.EntityNotFoundException;
import java.util.List;

public interface ITicketService {
    public List<Ticket> getAllTickets();

    public Page<Ticket> getAllTickets(Pageable pageable);

    public Ticket getTicketById(Long id);

    public Ticket getTicketByPrice(Double price);

    public List<Ticket> getTicketsByUser(Long userId);

    public List<Ticket> getTicketsByFlight(Long flightId);

    public Integer getTotalNrOfTickets();

    public Integer getNrOfTicketsForFlight(Long flightId);

    public Ticket createTicket(Ticket ticket);

    public void updateTicket(Ticket ticket);

    public void deleteTicketById(Long id);

    public List<Ticket> getTicketsByRoute(Long routeId);

    public TicketDTO reserveTicket(BuyTicketDTO buyTicketDTO) throws EntityNotFoundException, FieldInvalidException, FieldNotUniqueOrNullException;
}
