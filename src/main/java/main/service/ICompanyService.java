package main.service;

import main.dto.CompanyDTO;
import main.model.Company;
import main.model.exceptions.FieldInvalidException;
import main.model.exceptions.FieldNotUniqueOrNullException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.mail.MessagingException;
import java.util.List;

 public interface ICompanyService {
    
     List<Company> getCompanies();

     Page<Company> getCompanies(Pageable pageable);

     void addCompany(Company company) throws FieldNotUniqueOrNullException, FieldInvalidException;

     Company getCompanyById(Long id);

     Company getCompanyByName(String name);

     void deleteCompanyById(Long id) throws MessagingException;

     void updateCompany(Company company) throws FieldNotUniqueOrNullException, FieldInvalidException;

     List<Company> searchCompany(CompanyDTO companyDTO);
}
