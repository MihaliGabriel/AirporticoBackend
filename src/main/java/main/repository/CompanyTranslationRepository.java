package main.repository;

import main.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CompanyTranslationRepository extends JpaRepository<CompanyTranslation, Long> {
    Optional<CompanyTranslation> findByTextTypeAndLanguageAndCompany(TextTypeEntity textType, Language language, Company company);
}
