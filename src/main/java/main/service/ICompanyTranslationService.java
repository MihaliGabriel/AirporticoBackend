package main.service;

import main.model.*;

public interface ICompanyTranslationService {
    CompanyTranslation findByTextTypeAndLanguageAndCompany(TextTypeEntity textType, Language language, Company company);
}
