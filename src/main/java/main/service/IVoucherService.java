package main.service;

import main.model.Voucher;

import java.util.List;

public interface IVoucherService {
    List<Voucher> getVouchers();

    List<Voucher> getVouchersByPerson();

    List<Voucher> updateVoucher();

    Voucher getVoucherByCode(String code);

    void deleteVoucherById(Long voucherId);
}
