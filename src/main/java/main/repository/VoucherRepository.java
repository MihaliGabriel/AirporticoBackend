package main.repository;

import main.model.Voucher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VoucherRepository extends JpaRepository<Voucher, Long> {

    Voucher findByCode(String code);

    @Query("SELECT v from Voucher v where v.person.id = :personId")
    List<Voucher> findVoucherByPersonId(Long personId);
}
