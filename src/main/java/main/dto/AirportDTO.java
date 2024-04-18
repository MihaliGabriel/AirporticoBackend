package main.dto;

import javax.validation.constraints.NotBlank;

public class AirportDTO {
    private Long id;
    @NotBlank(message = "Airport name is required")
    private String name;
    @NotBlank(message = "Airport city is required")
    private String city;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    @Override
    public String toString() {
        return "AirportDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", city='" + city + '\'' +
                '}';
    }
}
