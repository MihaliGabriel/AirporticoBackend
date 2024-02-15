package main.controller;

import main.controller.utils.ControllerUtils;
import main.dto.VoucherDTO;
import main.model.Voucher;
import main.service.IVoucherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Collections;

@Controller
@RequestMapping("/api")
public class VoucherController {


    private IVoucherService voucherService;

    @Autowired
    public VoucherController(IVoucherService voucherService) {
        this.voucherService = voucherService;
    }

    /**
     * @Author GXM
     * Tipul metodei este ResponseEntity<Object> deoarece metoda returneaza obiecte de tip DTO, dar si map-uri de erori pentru exceptii,
     * care sunt prelucrate ca si erori de front-end.
     * @return
     */
    @CrossOrigin(origins = "http://localhost:4200")
    @PostMapping("/getvoucherbycode")
    public ResponseEntity<Object> getVoucher(@RequestBody VoucherDTO voucherDTO) {
        try {
            Voucher voucher = voucherService.getVoucherByCode(voucherDTO.getCode());

            if (voucher == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("message", "Voucher was not found!"));
            }

            voucherDTO = ControllerUtils.mapVoucherToVoucherDTO(voucher);
            return ResponseEntity.status(HttpStatus.OK).body(voucherDTO);
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap("message", "Server error"));
        }
    }
}
