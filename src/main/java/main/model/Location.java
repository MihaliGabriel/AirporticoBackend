package main.model;

import main.utils.Util;

import javax.persistence.*;

@Entity
@Table(name="locations",
        indexes = {@Index(name = "location_city_idx", columnList = "location_city", unique = true),
                    @Index(name = "location_country_idx", columnList = "location_country", unique = false)})
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="location_code", length=100, unique = true, nullable = false)
    private String code;

    @Column(name="location_city", length=100, unique = true, nullable = false)
    private String city;

    @Column(name="location_country", length=100, unique = false, nullable = false)
    private String country;


    public Long getId() {
        return id;
    }

    public void setId(Number id) {
        this.id = Util.getLong(id);
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return "Location{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", city='" + city + '\'' +
                ", country='" + country + '\'' +
                '}';
    }
}
