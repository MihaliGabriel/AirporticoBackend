package main.service;

import main.model.*;
import main.repository.FlightTranslationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class FlightTranslationService implements IFlightTranslationService{

    @Autowired
    private FlightTranslationRepository flightTranslationRepository;


    @Override
    public FlightTranslation findByTextTypeAndLanguageAndFlight(TextTypeEntity textType, Language language, Flight flight) {
        Optional<FlightTranslation> flightTranslationOptional = flightTranslationRepository.findByTextTypeAndLanguageAndFlight(textType, language, flight);

        return flightTranslationOptional.orElse(null);
    }
}
