package main.repository;

import main.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AirportTranslationRepository extends JpaRepository<AirportTranslation, Long> {
    Optional<AirportTranslation> findByTextTypeAndLanguageAndAirport(TextTypeEntity textType, Language language, Airport airport);
}
