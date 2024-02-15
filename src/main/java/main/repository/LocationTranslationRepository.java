package main.repository;

import main.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LocationTranslationRepository extends JpaRepository<LocationTranslation, Long> {
    Optional<LocationTranslation> findByTextTypeAndLanguageAndLocation(TextTypeEntity textType, Language language, Location location);
}
