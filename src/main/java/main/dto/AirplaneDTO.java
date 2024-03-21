package main.dto;

public class AirplaneDTO {
    private Long id;
    private String name;
    private Integer columns;
    private Integer rows;

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

    public Integer getColumns() {
        return columns;
    }

    public void setColumns(Integer columns) {
        this.columns = columns;
    }

    public Integer getRows() {
        return rows;
    }

    public void setRows(Integer rows) {
        this.rows = rows;
    }

    @Override
    public String toString() {
        return "AirplaneDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", columns=" + columns +
                ", rows=" + rows +
                '}';
    }
}
