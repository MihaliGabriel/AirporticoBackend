package main.repository;

import main.model.Route;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RouteRepository extends JpaRepository<Route, Long> {

    @Query("SELECT r from Route r WHERE " +
            "(:arrivalAirport IS NULL OR LOWER(r.arrivalAirport.name) LIKE LOWER(concat('%', :arrivalAirport, '%'))) OR " +
            "(:departureAirport IS NULL OR LOWER(r.departureAirport.name) LIKE LOWER(concat('%', :departureAirport, '%')))")
    public List<Route> findRoutesByAirportId(@Param("arrivalAirport") String arrivalAirport, @Param("departureAirport") String departureAirport);
}
