package main.dto;

import main.model.Person;

public class VoucherDTO {
    private long id;
    private String code;
    private double discountPercentage;
    private Person voucherUser;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public double getDiscountPercentage() {
        return discountPercentage;
    }

    public void setDiscountPercentage(double discountPercentage) {
        this.discountPercentage = discountPercentage;
    }

    public Person getVoucherUser() {
        return voucherUser;
    }

    public void setVoucherUser(Person voucherUser) {
        this.voucherUser = voucherUser;
    }

    @Override
    public String toString() {
        return "VoucherDTO{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", discountPercentage=" + discountPercentage +
                ", voucherUser=" + voucherUser +
                '}';
    }
}
