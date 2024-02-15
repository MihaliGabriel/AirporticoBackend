package main.service;

import main.model.Language;
import main.model.Location;
import main.model.LocationTranslation;
import main.model.TextTypeEntity;
import main.repository.LocationTranslationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LocationTranslationService implements  ILocationTranslationService{

    @Autowired
    private LocationTranslationRepository locationTranslationRepository;


    @Override
    public LocationTranslation findByTextTypeAndLanguageAndLocation(TextTypeEntity textType, Language language, Location location) {
        Optional<LocationTranslation> locationTranslationOptional = locationTranslationRepository.findByTextTypeAndLanguageAndLocation(textType, language, location);

        return locationTranslationOptional.orElse(null);
    }
}
