package main.service;

import main.model.exceptions.FieldInvalidException;
import main.model.Person;
import main.model.Voucher;
import main.repository.PersonRepository;
import main.repository.VoucherRepository;
import main.utils.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PersonService implements IPersonService {

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private VoucherRepository voucherRepository;

    @Autowired
    private IVoucherService voucherService;

    public List<Person> getPeople() {
        return personRepository.findAll();
    }

    public Page<Person> getPeople(Pageable pageable) {
        return personRepository.findAll(pageable);
    }

    public void addPerson(Person person) throws FieldInvalidException {

        if(!Util.checkValidString(person.getFirstName()))
            throw new FieldInvalidException("First name is not valid");
        if(!Util.checkValidString(person.getLastName()))
            throw new FieldInvalidException("Last name is not valid");
        if(!Util.checkValidString(person.getPhoneNumber()))
            throw new FieldInvalidException("Phone number is not valid");
        if(!Util.checkValidString(person.getEmail()))
            throw new FieldInvalidException("Email is not valid");

        personRepository.save(person);
    }

    public void updatePerson(Person person) throws FieldInvalidException {

        if(!Util.checkValidString(person.getFirstName()))
            throw new FieldInvalidException("First name is not valid");
        if(!Util.checkValidString(person.getLastName()))
            throw new FieldInvalidException("Last name is not valid");
        if(!Util.checkValidString(person.getPhoneNumber()))
            throw new FieldInvalidException("Phone number is not valid");
        if(!Util.checkValidString(person.getEmail()))
            throw new FieldInvalidException("Email is not valid");

        personRepository.save(person);
    }

    /**
     * @Author GXM
     *Persoanele se sterg cascadat, voucherele asociate persoanei se sterg prima oara, si dupa persoana.
     * @param personId
     */
    public void deletePerson(Long personId) {
        List<Voucher> vouchers = voucherRepository.findVoucherByPersonId(personId);

        for (Voucher voucher : vouchers) {
            voucherService.deleteVoucherById(voucher.getId());
        }
        personRepository.deleteById(personId);
    }

    public Person findPersonByUserId(Long userId) {
        Person person = personRepository.findPersonByUser(userId);
        if (person == null)
            return null;
        return person;
    }

    @Override
    public Person getById(Long personId) {
        return personRepository.getById(personId);
    }

    @Override
    public List<Person> findPersonByFlightId(Long flightId) {
        return personRepository.findPeopleByFlight(flightId);
    }

    @Override
    public List<Person> findPersonByRouteId(Long routeId) {
        return personRepository.findPeopleByRoute(routeId);
    }

}
