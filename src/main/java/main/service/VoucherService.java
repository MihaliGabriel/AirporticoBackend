package main.service;

import main.model.Person;
import main.model.User;
import main.model.Voucher;
import main.model.VoucherType;
import main.repository.PersonRepository;
import main.repository.VoucherRepository;
import main.utils.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static main.model.VoucherType.BIRTHDAY_VOUCHER;

@Service
public class VoucherService implements IVoucherService {

    @Autowired
    private VoucherRepository voucherRepository;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private EmailService emailService;

    public List<Voucher> getVouchers() {
        return voucherRepository.findAll();
    }

    public Voucher getVoucherByCode(String code) {
        return voucherRepository.findByCode(code);
    }

    public boolean checkSameVoucher(String code) {
        Voucher voucher = voucherRepository.findByCode(code);

        return voucher == null;
    }

    /**
     * @Author GXM
     * Prin adnotarea de @Scheduled aceasta metoda se apeleaza in fiecare zi la 6 dimineata si verifica daca persoanele din DB
     * sunt eligible pentru un voucher, de zi de nastere sau de vechime.
     */
    @Scheduled(cron = "0 0 6 * * ?")
    @Transactional
    public void checkForVouchers() {
        List<Person> people = personRepository.findAll();

        for (Person person : people) {
            User user = person.getUser();
            Date birthDate = person.getBirthDate();
            Date today = new Date();

            if (Util.isBirthday(today, birthDate.getDay(), birthDate.getMonth()))
                generateVoucher(BIRTHDAY_VOUCHER, person);
            if (Util.is5YearAnniversary(today, user.getCreatedAt()))
                generateVoucher(VoucherType.FIVE_YEAR_VOUCHER, person);
        }
    }

    /**
     * @Author GXM
     * Se creeaza voucher-ul, generand un UUID, de repetate ori daca voucher-ul mai exista in baza de date, pana cand se gaseste un cod
     * unic, si dupa este trimis pe email la persoana asociata voucher-ului.
     * @param voucherType
     * Exista 2 tipuri de vouchere, BIRTHDAY_VOUCHER si FIVE_YEAR_VOUCHER, pentru ziua de nastere si pentru vechimea de 5 ani in aplicatie.
     * @param person
     */
    @Transactional
    public void generateVoucher(VoucherType voucherType, Person person) {
        try {
            String code;
            do {
                code = UUID.randomUUID().toString();
            } while (!checkSameVoucher(code));

            Voucher voucher = new Voucher();
            voucher.setCode(code);
            voucher.setPerson(person);
            voucher.setType(voucherType);

            switch (voucherType) {
                case BIRTHDAY_VOUCHER:
                    voucher.setDiscountPercentage(15);
                    break;
                case FIVE_YEAR_VOUCHER:
                    voucher.setDiscountPercentage(25);
                    break;
            }

            voucherRepository.save(voucher);
            emailService.sendVoucherEmail(voucher, person.getEmail());
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public List<Voucher> getVouchersByPerson() {
        return Collections.emptyList();
    }

    public void deleteVoucherById(Long id) {
        voucherRepository.deleteById(id);
    }

    @Override
    public List<Voucher> updateVoucher() {
        return Collections.emptyList();
    }
}
