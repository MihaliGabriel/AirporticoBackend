package main.service;

import main.model.*;

public interface IAirplaneTranslationService {
    AirplaneTranslation findByTextTypeAndLanguageAndAirplane(TextTypeEntity textType, Language language, Airplane airplane);
}
