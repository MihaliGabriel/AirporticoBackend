package main.service;

import main.model.*;

public interface IFlightTranslationService {
    FlightTranslation findByTextTypeAndLanguageAndFlight(TextTypeEntity textType, Language language, Flight flight);
}
