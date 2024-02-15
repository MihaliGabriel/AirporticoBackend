package main.model;

import main.utils.Util;

import javax.persistence.*;


@Entity
@Table(name="roles",
        indexes = {@Index(name = "role_name_idx", columnList = "role_name", unique = true)})
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="role_name", length=100, nullable=false, unique=true)
    private String name;

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


}
