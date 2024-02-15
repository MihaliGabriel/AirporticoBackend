package main.service;

import main.controller.CompanyController;
import main.dto.CompanyDTO;
import main.model.Company;
import main.model.exceptions.FieldInvalidException;
import main.model.exceptions.FieldNotUniqueOrNullException;
import main.model.Flight;
import main.repository.CompanyRepository;
import main.repository.FlightRepository;
import main.utils.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.util.List;

@Service
public class CompanyService implements ICompanyService {

    @Autowired
    CompanyRepository companyRepository;

    @Autowired
    FlightRepository flightRepository;

    @Autowired
    IFlightService flightService;
    private static final Logger logger = LoggerFactory.getLogger(CompanyService.class);
    private static final String INSERT = "INSERT";
    private static final String UPDATE = "UPDATE";
    private boolean checkUniqueEmail(String email, String type) {

        Long count = companyRepository.countByEmail(email);

        return (count == 0 || !type.equals(INSERT)) && (count <= 1 || !type.equals(UPDATE));

    }

    private boolean checkNullEmail(String email) {
        return (email == null || email.isEmpty());
    }

    private boolean checkUniquePhone(String phone, String type) {

        Long count = companyRepository.countByPhone(phone);

        return (count == 0 || !type.equals(INSERT)) && (count <= 1 || !type.equals(UPDATE));
    }

    private boolean checkUniqueCode(String code, String type) {

        Long count = companyRepository.countByCode(code);

        return (count == 0 || !type.equals(INSERT)) && (count <= 1 || !type.equals(UPDATE));
    }
    private boolean checkNullPhone(String phone) {
        return (phone == null || phone.isEmpty());
    }

    private boolean checkUniqueName(String name, String type) {

        Long count = companyRepository.countByName(name);

        return (count == 0 || !type.equals(INSERT)) && (count <= 1 || !type.equals(UPDATE));
    }

    private boolean checkNullName(String name) {
        return (name == null || name.isEmpty());
    }

    @Override
    public List<Company> getCompanies() {
        return companyRepository.findAll();
    }

    @Override
    public Page<Company> getCompanies(Pageable pageable) {
        return companyRepository.findAll(pageable);
    }

    @Override
    public void addCompany(Company company) throws FieldNotUniqueOrNullException, FieldInvalidException {

        logger.info("Company entity in addCompany: {}", company);
        if (checkNullName(company.getName()))
            throw new FieldNotUniqueOrNullException("Name is null");
        if (checkNullPhone(company.getPhone()))
            throw new FieldNotUniqueOrNullException("Phone is null");
        if (checkNullEmail(company.getEmail()))
            throw new FieldNotUniqueOrNullException("Email is null");
        if(Util.checkValidString(company.getCode()))
            throw new FieldInvalidException("Code is not valid");

        if (!checkUniqueName(company.getName(), INSERT))
            throw new FieldNotUniqueOrNullException("Name is not unique");
        if (!checkUniqueEmail(company.getEmail(), INSERT))
            throw new FieldNotUniqueOrNullException("Email is not unique");
        if (!checkUniquePhone(company.getPhone(), INSERT))
            throw new FieldNotUniqueOrNullException("Phone number is not unique");
        if(!checkUniqueCode(company.getCode(), INSERT))
            throw new FieldNotUniqueOrNullException("Code is not unique");

        companyRepository.save(company);
    }

    @Override
    public Company getCompanyById(Long id) {
        return companyRepository.getById(id);
    }

    @Override
    public Company getCompanyByName(String name) {
        return companyRepository.findByName(name);

    }

    /**
     * @Author GXM
     * Stergere cascadata, mai intai se sterg zborurile asociate companiei si dupa compania.
     * @param id
     */
    @Override
    public void deleteCompanyById(Long id) throws NullPointerException, MessagingException {
        List<Flight> flights = flightRepository.findFlightsByCompanyId(id);
        if(flights.isEmpty())
            throw new NullPointerException("Flights is null");
        else {
            for (Flight flight : flights) {
                flightService.deleteFlightById(flight.getId());
            }
        }
        companyRepository.deleteById(id);
    }

    /**
     * @Author GXM
     * Daca vreau sa updatez compania cu alt nume, email, numar de telefon sau cod,
     * verific doar daca mai exista o intrare in db cu aceleasi intrari.
     * Daca numele ramane la fel la update, nu fac nimic, deoarece intrarea este valida in db.
     * @param company
     * @throws FieldNotUniqueOrNullException
     * @throws FieldInvalidException
     */
    @Override
    public void updateCompany(Company company) throws FieldNotUniqueOrNullException, FieldInvalidException {

        Company companyFromDb = companyRepository.getById(company.getId());

        if (checkNullName(company.getName()))
            throw new FieldNotUniqueOrNullException("Name is null");
        if (checkNullPhone(company.getPhone()))
            throw new FieldNotUniqueOrNullException("Phone is null");
        if (checkNullEmail(company.getEmail()))
            throw new FieldNotUniqueOrNullException("Email is null");
        if(Util.checkValidString(company.getCode()))
            throw new FieldInvalidException("Code is not valid");

        if (!companyFromDb.getName().equals(company.getName()) && !checkUniqueName(company.getName(), INSERT)) {
                throw new FieldNotUniqueOrNullException("Name is not unique");
        }

        if (!companyFromDb.getEmail().equals(company.getEmail()) && !checkUniqueEmail(company.getEmail(), INSERT)) {
                throw new FieldNotUniqueOrNullException("Email is not unique");
        }
        if (!companyFromDb.getPhone().equals(company.getPhone()) && !checkUniquePhone(company.getPhone(), INSERT)) {
                throw new FieldNotUniqueOrNullException("Phone number is not unique");
        }
        if(!companyFromDb.getCode().equals(company.getCode()) && !checkUniqueCode(company.getCode(), INSERT)) {
                throw new FieldNotUniqueOrNullException("Code is not unique");
        }
        companyRepository.save(company);
    }

    @Override
    public List<Company> searchCompany(CompanyDTO companyDTO) {
        String companyCode = companyDTO.getCompanyCode() != null ? companyDTO.getCompanyCode().toLowerCase() : null;
        String email = companyDTO.getEmail() != null ? companyDTO.getEmail().toLowerCase() : null;
        String name = companyDTO.getCompanyName() != null ? companyDTO.getCompanyName().toLowerCase() : null;
        String phone = companyDTO.getPhone() != null ? companyDTO.getPhone().toLowerCase() : null;

        return companyRepository.search(companyCode, email, name, phone);
    }
}
