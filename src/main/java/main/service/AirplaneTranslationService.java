package main.service;

import main.model.*;
import main.repository.AirplaneTranslationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AirplaneTranslationService implements  IAirplaneTranslationService{

    @Autowired
    private AirplaneTranslationRepository airplaneTranslationRepository;
    @Override
    public AirplaneTranslation findByTextTypeAndLanguageAndAirplane(TextTypeEntity textType, Language language, Airplane airplane) {
        Optional<AirplaneTranslation> airplaneTranslationOptional = airplaneTranslationRepository.findByTextTypeAndLanguageAndAirplane(textType, language, airplane);

        return airplaneTranslationOptional.orElse(null);
    }
}
