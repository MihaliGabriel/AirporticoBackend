package main.model;

import main.utils.Util;

import javax.persistence.*;

@Entity
@Table(name="airports", indexes = {@Index(name="airport_name_idx", columnList = "airport_name", unique = true)})
public class Airport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="airport_name", length = 100, unique=true, nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name="ref_location", nullable = false)
    private Location location;


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

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }


    @Override
    public String toString() {
        return "Airport{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", location=" + location +
                '}';
    }
}
