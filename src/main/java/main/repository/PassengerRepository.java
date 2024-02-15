package main.repository;

import main.model.Passenger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PassengerRepository extends JpaRepository<Passenger, Long> {

    @Query("SELECT " +
            "p " +
            "from Passenger p " +
            "inner join Ticket t " +
            "on p.ticket.id = t.id " +
            "where p.ticket.id = :ticketId")
    List<Passenger> getPassengersByTicketId(Long ticketId);
}
