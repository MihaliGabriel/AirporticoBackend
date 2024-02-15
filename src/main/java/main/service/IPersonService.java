package main.service;

import main.model.exceptions.FieldInvalidException;
import main.model.Person;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IPersonService {

    List<Person> getPeople();

    Page<Person> getPeople(Pageable pageable);

    void addPerson(Person person) throws FieldInvalidException;

    void updatePerson(Person person) throws FieldInvalidException;

    void deletePerson(Long personId);

    Person findPersonByUserId(Long userId);

    Person getById(Long personId);

    List<Person> findPersonByFlightId(Long flightId);

    List<Person> findPersonByRouteId(Long routeId);
}
