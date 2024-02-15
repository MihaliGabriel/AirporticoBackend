package main.service;

import main.model.Company;
import main.model.CompanyTranslation;
import main.model.Language;
import main.model.TextTypeEntity;
import main.repository.CompanyTranslationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CompanyTranslationService implements ICompanyTranslationService{

    @Autowired
    private CompanyTranslationRepository companyTranslationRepository;

    @Override
    public CompanyTranslation findByTextTypeAndLanguageAndCompany(TextTypeEntity textType, Language language, Company company) {
        Optional<CompanyTranslation> companyTranslationOptional = companyTranslationRepository.findByTextTypeAndLanguageAndCompany(textType, language, company);

        return companyTranslationOptional.orElse(null);
    }
}
