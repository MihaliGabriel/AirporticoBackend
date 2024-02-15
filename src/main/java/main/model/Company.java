package main.model;

import main.utils.Util;

import javax.persistence.*;

@Entity
@Table(name="companies",
        indexes = {@Index(name="company_name_idx", columnList = "company_name", unique = true),
                    @Index(name="company_phone_idx", columnList = "company_phone", unique = true),
                    @Index(name="company_email_idx", columnList = "company_email", unique = true)})
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="company_name", unique = true, nullable = false)
    private String name;

    @Column(name="company_code", unique = true, nullable = false)
    private String code;

    @Column(name="company_phone", unique = true, nullable = false)
    private String phone;

    @Column(name="company_email", unique = true, nullable = false)
    private String email;

    public Long getId() {
        return id;
    }

    public void setId(Number id) {
        this.id = Util.getLong(id);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return "Company{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", code='" + code + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
