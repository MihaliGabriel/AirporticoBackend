package main.repository;

import main.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    public Ticket findByPrice(Double price);

    @Query(value = "SELECT " +
            "t " +
            "from Ticket t " +
            "inner join Flight f " +
            "on t.flight.id = f.id " +
            "where f.id = :flightId")
    public List<Ticket> findTicketsByFlight(Long flightId);

    @Query(value = "SELECT " +
            "t " +
            "from Ticket t " +
            "inner join User u " +
            "on t.user.id = u.id " +
            "where t.user.id = :userId")
    public List<Ticket> findTicketsByUser(Long userId);

    @Query(value = "SELECT " +
            "count(t.id) " +
            "from tickets t " +
            "inner join flights f " +
            "on t.ref_flight = f.id " +
            "group by f.id " +
            "where f.id is null or f.id = ?1", nativeQuery = true)
    public Integer getNumberOfTicketsBoughtFlight(Long flightId);

    @Query(value = "SELECT " +
            "count(t.id) " +
            "from tickets t " +
            "inner join flights f " +
            "on t.ref_flight = f.id ", nativeQuery = true)
    public Integer getNumberOfTicketsBought();

    @Query(value = "SELECT " +
            "t " +
            "from Ticket t " +
            "where t.flight.route.id = :routeId")
    public List<Ticket> findTicketsByRoute(Long routeId);
}
