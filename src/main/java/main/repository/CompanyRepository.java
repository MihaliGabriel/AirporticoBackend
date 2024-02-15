package main.repository;

import main.model.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {
    Company findByName(String name);

    Long countByPhone(String phone);

    Long countByEmail(String email);

    Long countByName(String name);

    Long countByCode(String code);

    @Query("SELECT c from Company c WHERE " +
            "(:companyCode IS NULL OR LOWER(c.code) LIKE LOWER (concat('%', :companyCode, '%'))) AND " +
            "(:email IS NULL OR LOWER(c.email) LIKE LOWER(concat('%', :email, '%'))) AND " +
            "(:companyName IS NULL OR LOWER(c.name) LIKE LOWER(concat('%', :companyName, '%'))) AND" +
            "(:companyPhone IS NULL OR LOWER(c.phone) LIKE LOWER(concat('%', :companyPhone, '%')))")
    List<Company> search(@Param("companyCode") String companyCode, @Param("email") String email, @Param("companyName") String companyName, @Param("companyPhone") String companyPhone);
}
