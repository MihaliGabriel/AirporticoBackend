package main.service;

import main.model.Airport;
import main.model.AirportTranslation;
import main.model.Language;
import main.model.TextTypeEntity;
import main.repository.AirportTranslationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AirportTranslationService implements  IAirportTranslationService{

    @Autowired
    private AirportTranslationRepository airportTranslationRepository;

    @Override
    public AirportTranslation findByTextTypeAndLanguageAndAirport(TextTypeEntity textType, Language language, Airport airport) {
        Optional<AirportTranslation> airportTranslationOptional = airportTranslationRepository.findByTextTypeAndLanguageAndAirport(textType, language, airport);

        return airportTranslationOptional.orElse(null);

    }
}
