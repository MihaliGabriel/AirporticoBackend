package main.service;

import main.model.*;

public interface IAirportTranslationService {
    AirportTranslation findByTextTypeAndLanguageAndAirport(TextTypeEntity textType, Language language, Airport airport);
}
