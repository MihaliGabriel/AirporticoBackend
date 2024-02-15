package main.repository;

import main.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {

    @Query(value = "SELECT " +
            "p " +
            "from Person p " +
            "inner join User u " +
            "on p.user.id = u.id " +
            "where p.user.id = :userId")
    public Person findPersonByUser(Long userId);

    @Query(value = "SELECT " +
            "p " +
            "from Person p " +
            "inner join User u " +
            "on p.user.id = u.id " +
            "inner join Ticket t " +
            "on t.user.id = u.id " +
            "where t.flight.route.id = :routeId")
    List<Person> findPeopleByRoute(Long routeId);

    @Query(value = "SELECT " +
            "p " +
            "from Person p " +
            "inner join User u " +
            "on p.user.id = u.id " +
            "inner join Ticket t " +
            "on t.user.id = u.id " +
            "where t.flight.id = :flightId")
    List<Person> findPeopleByFlight(Long flightId);
}
