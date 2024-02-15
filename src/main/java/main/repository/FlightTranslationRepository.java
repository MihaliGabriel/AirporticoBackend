package main.repository;

import main.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FlightTranslationRepository extends JpaRepository<FlightTranslation, Long> {
    Optional<FlightTranslation> findByTextTypeAndLanguageAndFlight(TextTypeEntity textType, Language language, Flight flight);
}
