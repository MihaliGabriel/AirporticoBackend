package main.model;

import main.utils.Util;

import javax.persistence.*;

@Entity
@Table(name="airplanes")
public class Airplane {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name="airplane_name", length = 100, unique=false, nullable = false)
    private String name;
    @Column(name="airplane_rows", length = 100, unique=false, nullable = false)
    private Integer rows;
    @Column(name="airplane_columns", length = 100, unique=false, nullable = false)
    private Integer columns;

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

    public Integer getRows() {
        return rows;
    }

    public void setRows(Number rows) {
        this.rows = Util.getInteger(rows);
    }

    public Integer getColumns() {
        return columns;
    }

    public void setColumns(Integer columns) {
        this.columns = columns;
    }


    @Override
    public String toString() {
        return "Airplane{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", rows=" + rows +
                ", columns=" + columns +
                '}';
    }
}
