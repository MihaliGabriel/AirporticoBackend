package main.repository;

import main.model.Flight;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface FlightRepository extends JpaRepository<Flight, Long> {
    Flight findByName(String name);

    Flight findByArrivalDate(LocalDateTime arrivalDate);

    Flight findByDepartureDate(LocalDateTime departureDate);

    Long countByName(String name);

    //    @Query(value = "SELECT f " +
//            "from flights f inner join routes r " +
//            "on f.ref_route = r.id " +
//            "inner join airports a " +
//            "on r.ref_airport_arrival = a.id " +
//            "inner join airports a2 " +
//            "on r.ref_airport_departure = a2.id " +
//            "where a.name like %?1 or a2.name like %?2",
//            nativeQuery = true)
//    public List<Flight> findByDepartureOrArrivalAirport(String departureAirport, String arrivalAirport);
    @Query("SELECT f from Flight f " +
            "where f.route.departureAirport.location.city = :departureCity")
    public List<Flight> findByDepartureCity(String departureCity);

    @Query("SELECT f from Flight f " +
            "where f.route.arrivalAirport.location.city = :arrivalCity")
    public List<Flight> findByArrivalCity(String arrivalCity);

    @Query("SELECT f from Flight f WHERE " +
            "(:departureDate IS NULL OR f.departureDate = :departureDate) AND " +
            "(:arrivalDate IS NULL OR f.arrivalDate = :arrivalDate) AND " +
            "(:departureCity IS NULL OR LOWER(f.route.departureAirport.location.city) LIKE LOWER(concat('%', :departureCity, '%'))) AND " +
            "(:arrivalCity IS NULL OR LOWER(f.route.arrivalAirport.location.city) LIKE LOWER(concat('%', :arrivalCity, '%'))) AND " +
            "(:company IS NULL OR f.company.name = :company) AND " +
            "(:economyPriceMin IS NULL OR f.economyPrice >= :economyPriceMin) AND (:economyPriceMax IS NULL OR f.economyPrice <= :economyPriceMax) AND " +
            "(:businessPriceMin IS NULL OR f.businessPrice >= :businessPriceMin) AND (:businessPriceMax IS NULL OR f.businessPrice <= :businessPriceMax) AND " +
            "(:firstClassPriceMin IS NULL OR f.firstClassPrice >= :firstClassPriceMin) AND (:firstClassPriceMax IS NULL OR f.firstClassPrice <= :firstClassPriceMax)")
    Page<Flight> searchFlights(@Param("departureDate") LocalDateTime departureDate,
                               @Param("arrivalDate") LocalDateTime arrivalDate,
                               @Param("departureCity") String departureCity,
                               @Param("arrivalCity") String arrivalCity,
                               @Param("company") String company,
                               @Param("businessPriceMin") Double businessPriceMin,
                               @Param("businessPriceMax") Double businessPriceMax,
                               @Param("economyPriceMin") Double economyPriceMin,
                               @Param("economyPriceMax") Double economyPriceMax,
                               @Param("firstClassPriceMin") Double firstClassPriceMin,
                               @Param("firstClassPriceMax") Double firstClassPriceMax,
                               Pageable pageable);

    @Query("SELECT f from Flight f where f.route.id = :routeId")
    List<Flight> findFlightsByRouteId(Long routeId);

    //    @Query("SELECT f " +
//            "from Flight f " +
//            "where f.departureDate = ?1 or f.arrivalDate = ?2")
//    public List<Flight> findByDepartureOrArrivalDate(String departureDate, String arrivalDate);
    @Query("SELECT f from Flight f where f.company.id = :companyId")
    List<Flight> findFlightsByCompanyId(Long companyId);
}
