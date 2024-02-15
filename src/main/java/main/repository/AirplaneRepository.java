package main.repository;

import main.model.Airplane;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AirplaneRepository extends JpaRepository<Airplane, Long> {
    Page<Airplane> findAll(Pageable pageable);

    @Query("SELECT " +
            "f.airplane from Flight f " +
            "where f.name = :flightName")
    Airplane findByFlightName(@Param("flightName") String flightName);

    Airplane findByName(String airplaneName);
}
