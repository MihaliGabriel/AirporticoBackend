package main.repository;

import main.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AirplaneTranslationRepository extends JpaRepository<AirplaneTranslation, Long> {
    Optional<AirplaneTranslation> findByTextTypeAndLanguageAndAirplane(TextTypeEntity textType, Language language, Airplane airplane);
}
