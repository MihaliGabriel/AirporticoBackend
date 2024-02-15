package main.service;

import main.model.*;

public interface ILocationTranslationService {
    LocationTranslation findByTextTypeAndLanguageAndLocation(TextTypeEntity textType, Language language, Location location);
}
