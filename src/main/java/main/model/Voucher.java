package main.model;

import main.utils.Util;

import javax.persistence.*;

@Entity
@Table(name="vouchers")
public class Voucher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="voucher_code", length = 100, unique = true, nullable = false)
    private String code;

    @Column(name="voucher_type", length = 100, unique = false, nullable = false)
    private VoucherType type;

    @Column(name="discount_percentage", unique = false, nullable = false, columnDefinition = "NUMERIC(10,2)")
    private double discountPercentage;

    @OneToOne
    @JoinColumn(name = "ref_person")
    private Person person;

    public Long getId() {
        return id;
    }

    public void setId(Number id) {
        this.id = Util.getLong(id);
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public VoucherType getType() {
        return type;
    }

    public void setType(VoucherType type) {
        this.type = type;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public double getDiscountPercentage() {
        return discountPercentage;
    }

    public void setDiscountPercentage(Number discountPercentage) {
        this.discountPercentage = Util.getDouble(discountPercentage);
    }



    @Override
    public String toString() {
        return "Voucher{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", type=" + type +
                ", discountPercentage=" + discountPercentage +
                ", person=" + person +
                '}';
    }
}
