package main.repository;

import main.model.Airport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AirportRepository extends JpaRepository<Airport, Long> {
    public Airport findByName(String name);

    Long countByName(String name);

    @Query("SELECT a from Airport a WHERE " +
            "(:airportName IS NULL OR LOWER(a.name) LIKE LOWER(concat('%', :airportName, '%'))) AND " +
            "(:airportCity IS NULL OR LOWER(a.location.city) LIKE LOWER(concat('%', :airportCity, '%')))")
    List<Airport> search(@Param("airportName") String airportName, @Param("airportCity") String airportCity);

    @Query("SELECT a from Airport a where a.location.id = :id")
    List<Airport> findAirportByLocationId(Long id);
}
