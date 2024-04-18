package main.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

public class CompanyDTO {
    private Long id;
    @NotBlank(message = "Company code is required")
    private String companyCode;
    @NotBlank(message = "Company name is required")
    private String companyName;
    @NotBlank(message = "Company phone is required")
    private String phone;
    @NotBlank(message = "Company email is required")
    @Email(message = "Invalid email format")
    private String email;

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyName() {
        return companyName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCompanyCode() {
        return companyCode;
    }

    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
    }

    @Override
    public String toString() {
        return "CompanyDTO{" +
                "id=" + id +
                ", companyCode='" + companyCode + '\'' +
                ", companyName='" + companyName + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
