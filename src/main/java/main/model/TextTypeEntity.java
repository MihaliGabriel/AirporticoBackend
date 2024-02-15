package main.model;

import main.utils.Util;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="texttype")
public class TextTypeEntity {

    @Id
    private Long id;

    @Column(name="name", length=100, unique=true, nullable = false)
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

    @Override
    public String toString() {
        return "TextTypeEntity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
