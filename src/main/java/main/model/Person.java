package main.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import main.utils.Util;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "person")
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "person_first_name", length = 100, unique = false, nullable = false)
    private String firstName;

    @Column(name = "person_last_name", length = 100, unique = false, nullable = false)
    private String lastName;

    @Column(name = "person_phone_number", length = 100, unique = true, nullable = false)
    private String phoneNumber;

    @Column(name = "person_email", length = 100, unique = true, nullable = false)
    private String email;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Column(name="person_birth_date", unique=false, nullable=false)
    private Date birthDate;

    @OneToOne
    @JoinColumn(name = "ref_user")
    private User user;

    public Long getId() {
        return id;
    }

    public void setId(Number id) {
        this.id = Util.getLong(id);
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }


    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }


    @Override
    public String toString() {
        return "Person{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", email='" + email + '\'' +
                ", user=" + user +
                '}';
    }
}
