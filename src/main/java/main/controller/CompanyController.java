package main.controller;

import main.controller.utils.ControllerUtils;
import main.dto.CompanyDTO;
import main.model.*;
import main.model.exceptions.FieldInvalidException;
import main.model.exceptions.FieldNotUniqueOrNullException;
import main.repository.LanguageRepository;
import main.repository.TextTypeEntityRepository;
import main.service.ICompanyService;
import main.service.ICompanyTranslationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


import javax.validation.Valid;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/api")
public class CompanyController {


    ICompanyService companyService;
    ICompanyTranslationService companyTranslationService;
    TextTypeEntityRepository textTypeEntityRepository;
    LanguageRepository languageRepository;

    private static final Logger logger = LoggerFactory.getLogger(CompanyController.class);
    private static final String MESSAGE = "message";

    @Autowired
    public CompanyController(ICompanyService companyService, ICompanyTranslationService companyTranslationService, TextTypeEntityRepository textTypeEntityRepository, LanguageRepository languageRepository) {
        this.companyService = companyService;
        this.companyTranslationService = companyTranslationService;
        this.textTypeEntityRepository = textTypeEntityRepository;
        this.languageRepository = languageRepository;
    }

    /**
     * @Author GXM
     * Tipul metodei este ResponseEntity<Object> deoarece metoda returneaza obiecte de tip DTO, dar si map-uri de erori pentru exceptii,
     * care sunt prelucrate ca si erori de front-end.
     * @return
     */
    @CrossOrigin(origins = {"http://localhost:4200", "https://thankful-coast-03f536003.4.azurestaticapps.net"})
    @PostMapping("/companies")
    public ResponseEntity<Object> getCompanies(@RequestHeader("Language") String language) {
        try {
            List<Company> companies = companyService.getCompanies();
            if (companies == null || companies.isEmpty())
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);

            Optional<Language> languageEntity = languageRepository.findByName(language);

            List<CompanyDTO> companyDTOS = ControllerUtils.mapCompanyListToCompanyDtoList(companies, textTypeEntityRepository, companyTranslationService, languageEntity.orElse(null));

            return ResponseEntity.status(HttpStatus.OK).body(companyDTOS);
        }
        catch (Exception e) {
            logger.error("Error fetching all companies: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap(MESSAGE, e.getMessage()));
        }
    }

    /**
     * @Author GXM
     * @param page pagina curenta care trebuie afisata
     * @param size numarul de elemente afisate pe o pagina
     * @return
     */
    @CrossOrigin(origins = {"http://localhost:4200", "https://thankful-coast-03f536003.4.azurestaticapps.net"})
    @GetMapping("/companies")
    public ResponseEntity<Object> getCompanies(@RequestHeader("Language") String language, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "4") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Company> companyPage = companyService.getCompanies(pageable);

            Optional<Language> languageEntity = languageRepository.findByName(language);

            Page<CompanyDTO> companyDTOS = ControllerUtils.mapCompanyListToCompanyDtoList(companyPage, textTypeEntityRepository, companyTranslationService, languageEntity.orElse(null));

            return ResponseEntity.status(HttpStatus.OK).body(companyDTOS);
        }
        catch (Exception e) {
            logger.error("Error fetching all paginated companies: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap(MESSAGE, e.getMessage()));
        }
    }

    @CrossOrigin(origins = {"http://localhost:4200", "https://thankful-coast-03f536003.4.azurestaticapps.net"})
    @PostMapping("/admin/searchcompanies")
    public ResponseEntity<Object> searchCompany(@RequestHeader("Language") String language, @RequestBody CompanyDTO companyDTO) {
        try {
            logger.info("CompanyDTO: {}",companyDTO);
            List<Company> companies = companyService.searchCompany(companyDTO);

            if (companies == null || companies.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            Optional<Language> languageEntity = languageRepository.findByName(language);

            List<CompanyDTO> companyDTOS = ControllerUtils.mapCompanyListToCompanyDtoList(companies, textTypeEntityRepository, companyTranslationService, languageEntity.orElse(null));
            logger.info("CompanyDTOS : {}", companyDTOS);

            return new ResponseEntity<>(companyDTOS, HttpStatus.OK);
        }
        catch (Exception e) {
            logger.error("Error searching for companies: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap(MESSAGE, e.getMessage()));
        }
    }

    @CrossOrigin(origins = {"http://localhost:4200", "https://thankful-coast-03f536003.4.azurestaticapps.net"})
    @PostMapping("/admin/createcompany")
    public ResponseEntity<Object> createCompany(@Valid @RequestBody CompanyDTO companyDTO) {
        try {
            Company company = new Company();

            company.setName(companyDTO.getCompanyName());
            company.setEmail(companyDTO.getEmail());
            company.setPhone(companyDTO.getPhone());
            company.setCode(companyDTO.getCompanyCode());
            companyService.addCompany(company);

            companyDTO.setId(company.getId());
            companyDTO.setCompanyName(company.getName());
            companyDTO.setPhone(company.getPhone());
            companyDTO.setEmail(company.getEmail());
            companyDTO.setCompanyCode(company.getCode());

            return ResponseEntity.status(HttpStatus.OK).body(companyDTO);
        }
        catch (FieldNotUniqueOrNullException a) {
            logger.error("Fields are not unique or null: {}", a.getMessage(), a);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap(MESSAGE, a.getMessage()));
        }
        catch (FieldInvalidException b) {
            logger.error("Fields invalid: {}", b.getMessage(), b);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap(MESSAGE, b.getMessage()));
        }
        catch (Exception e) {
            logger.error("Error creating company: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap(MESSAGE, e.getMessage()));
        }
    }

    @CrossOrigin(origins = {"http://localhost:4200", "https://thankful-coast-03f536003.4.azurestaticapps.net"})
    @PostMapping("/admin/updatecompany")
    public ResponseEntity<Object> updateCompany(@Valid @RequestBody CompanyDTO companyDTO) {
        try {
            logger.info("Update company");
            Company company = new Company();
            logger.info("CompanyDTO: {}", companyDTO);
            company.setId(companyDTO.getId());
            company.setName(companyDTO.getCompanyName());
            company.setEmail(companyDTO.getEmail());
            company.setPhone(companyDTO.getPhone());
            company.setCode(companyDTO.getCompanyCode());

            companyService.updateCompany(company);

            companyDTO.setId(company.getId());
            companyDTO.setCompanyName(company.getName());
            companyDTO.setEmail(company.getEmail());
            companyDTO.setPhone(company.getPhone());
            companyDTO.setCompanyCode(company.getCode());
            return ResponseEntity.status(HttpStatus.OK).body(companyDTO);
        }
        catch (FieldNotUniqueOrNullException a) {
            logger.error("Fields not unique or null: {}", a.getMessage(), a);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap(MESSAGE, a.getMessage()));
        }
        catch (FieldInvalidException b) {
            logger.error("Field/fields invalid: {}", b.getMessage(), b);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap(MESSAGE, b.getMessage()));
        }
        catch (Exception e) {
            logger.error("Error updating company: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap(MESSAGE, e.getMessage()));
        }
    }

    @CrossOrigin(origins = {"http://localhost:4200", "https://thankful-coast-03f536003.4.azurestaticapps.net"})
    @PostMapping("/admin/deletecompany")
    public ResponseEntity<Object> deleteCompany(@RequestHeader("Language") String language, @RequestBody CompanyDTO companyDTO) {
        try {
            companyService.deleteCompanyById(companyDTO.getId());
            List<Company> companies = companyService.getCompanies();

            logger.info("Delete company");

            if (companies == null || companies.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            Optional<Language> languageEntity = languageRepository.findByName(language);
            List<CompanyDTO> companyDTOS = ControllerUtils.mapCompanyListToCompanyDtoList(companies, textTypeEntityRepository, companyTranslationService, languageEntity.orElse(null));

            return ResponseEntity.status(HttpStatus.OK).body(companyDTOS);
        }
        catch (Exception e) {
            logger.error("Error deleting company: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap(MESSAGE, e.getMessage()));
        }
    }
}
