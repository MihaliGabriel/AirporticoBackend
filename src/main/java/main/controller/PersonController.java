package main.controller;

import main.controller.utils.ControllerUtils;
import main.dto.PersonDTO;
import main.model.*;
import main.model.exceptions.FieldInvalidException;
import main.service.IPersonService;
import main.service.IUsersService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api")
public class PersonController {

    private static final String MESSAGE = "message";

    private static final Logger logger = LoggerFactory.getLogger(PersonController.class);

    @Autowired
    IPersonService personService;

    @Autowired
    IUsersService usersService;


    /**
     * @Author GXM
     * Tipul metodei este ResponseEntity<Object> deoarece metoda returneaza obiecte de tip DTO, dar si map-uri de erori pentru exceptii,
     * care sunt prelucrate ca si erori de front-end.
     * @return
     */
    @CrossOrigin(origins = "http://localhost:4200")
    @PostMapping("admin/people")
    public ResponseEntity<Object> getAllPeople() {
        try {
            List<Person> people = personService.getPeople();

            if (people == null || people.isEmpty())
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(Collections.singletonMap(MESSAGE, "No people"));

            List<PersonDTO> personDTOS = ControllerUtils.mapPersonListToPersonDtoList(people);

            return ResponseEntity.status(HttpStatus.OK).body(personDTOS);
        }
        catch (Exception e) {
            logger.error("Error fetching all people: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap(MESSAGE, e.getMessage()));
        }
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @PostMapping("/createperson")
    public ResponseEntity<Object> createPerson(@RequestBody PersonDTO personDTO) {
        try {
            User user = usersService.getUserById(personDTO.getUserId());

            if (user == null) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(Collections.singletonMap(MESSAGE, "No content"));
            }
            Person person = new Person();

            person.setFirstName(personDTO.getFirstName());
            person.setLastName(personDTO.getLastName());
            person.setEmail(personDTO.getEmail());
            person.setPhoneNumber(personDTO.getPhoneNumber());
            person.setBirthDate(personDTO.getBirthDate());
            person.setUser(user);

            personService.addPerson(person);

            List<Person> people = personService.getPeople();
            if (people == null || people.isEmpty())
                return ResponseEntity.noContent().build();

            personDTO.setId(person.getId());

            return ResponseEntity.status(HttpStatus.OK).body(personDTO);
        }
        catch(FieldInvalidException a) {
            logger.error("Field invalid error: {}", a.getMessage(), a);
            return ResponseEntity.internalServerError().body(Collections.singletonMap(MESSAGE, a.getMessage()));
        }
        catch (Exception e) {
            logger.error("Error creating person: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Collections.singletonMap(MESSAGE, e.getMessage()));
        }
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @PostMapping("/updateperson")
    public ResponseEntity<Object> updatePerson(@RequestBody PersonDTO personDTO) {
        try {

            Person person = personService.findPersonByUserId(personDTO.getUserId());

            person.setFirstName(personDTO.getFirstName());
            person.setLastName(personDTO.getLastName());
            person.setEmail(personDTO.getEmail());
            person.setPhoneNumber(personDTO.getPhoneNumber());
            person.setBirthDate(personDTO.getBirthDate());

            personService.updatePerson(person);
            return ResponseEntity.status(HttpStatus.OK).body(personDTO);
        }
        catch(FieldInvalidException a) {
            logger.error("Field invalid error: {}", a.getMessage(), a);
            return ResponseEntity.internalServerError().body(Collections.singletonMap(MESSAGE, a.getMessage()));
        }
        catch (Exception e) {
            logger.error("Error updating person: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Collections.singletonMap(MESSAGE, e.getMessage()));
        }
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @PostMapping("/deleteperson")
    public ResponseEntity<Object> deletePerson(@RequestBody PersonDTO personDTO) {
        try {
            personService.deletePerson(personDTO.getId());
            List<Person> people = personService.getPeople();
            logger.info("Deleting person...");
            if (people == null || people.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(Collections.singletonMap(MESSAGE, "No people"));
            }
            List<PersonDTO> personDTOS = ControllerUtils.mapPersonListToPersonDtoList(people);
            return ResponseEntity.status(HttpStatus.OK).body(personDTOS);
        }
        catch (Exception e) {
            logger.error("Error deleting person: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.OK).body(Collections.singletonMap(MESSAGE, e.getMessage()));
        }
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @PostMapping("/personbyuser")
    public ResponseEntity<Object> getPersonByUser(@RequestBody PersonDTO personDTO) {
        try {
            logger.info("PersonDTO: {}", personDTO);
            Person person = personService.findPersonByUserId(personDTO.getUserId());

            personDTO = ControllerUtils.mapPersonToPersonDto(person);

            logger.info("PersoanaDTO pt user: {}", personDTO);

            return ResponseEntity.status(HttpStatus.OK).body(personDTO);
        }
        catch (Exception e) {
            logger.error("Error fetching person by user: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.OK).body(Collections.singletonMap(MESSAGE, e.getMessage()));
        }
    }
}
