package main.repository;

import main.model.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {
    Location findByCity(String city);

    Long countByCity(String city);

    Long countByCode(String code);

    @Query("SELECT l from Location l WHERE " +
            "(:code IS NULL OR LOWER(l.code) LIKE LOWER(concat('%', :code, '%'))) AND " +
            "(:city IS NULL OR LOWER(l.city) LIKE LOWER(concat('%', :city, '%'))) AND " +
            "(:country IS NULL OR LOWER(l.country) LIKE LOWER(concat('%', :country, '%')))")
    List<Location> search(@Param("code") String code, @Param("city") String city, @Param("country") String country);
}
