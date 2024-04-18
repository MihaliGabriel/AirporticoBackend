package main.dto;

import javax.validation.constraints.NotBlank;

public class LocationDTO {
    private Long id;

    @NotBlank
    private String city;

    @NotBlank
    private String country;

    @NotBlank
    private String locationCode;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getLocationCode() {
        return locationCode;
    }

    public void setLocationCode(String locationCode) {
        this.locationCode = locationCode;
    }

    @Override
    public String toString() {
        return "LocationDTO{" +
                "id=" + id +
                ", city='" + city + '\'' +
                ", country='" + country + '\'' +
                ", locationCode='" + locationCode + '\'' +
                '}';
    }
}
