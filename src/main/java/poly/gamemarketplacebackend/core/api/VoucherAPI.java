package poly.gamemarketplacebackend.core.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import poly.gamemarketplacebackend.core.constant.ResponseObject;
import poly.gamemarketplacebackend.core.dto.VoucherDTO;
import poly.gamemarketplacebackend.core.exception.CustomException;
import poly.gamemarketplacebackend.core.mapper.VoucherMapper;
import poly.gamemarketplacebackend.core.service.EmailService;
import poly.gamemarketplacebackend.core.service.VoucherService;

@RestController
@Slf4j
@RequestMapping("/api/vouchers")
@RequiredArgsConstructor
public class VoucherAPI {
    private final VoucherService voucherService;
    private final VoucherMapper voucherMapper;

    @PostMapping("/create")
    public ResponseEntity<ResponseObject> createVoucher(@RequestBody VoucherDTO voucherDTO) {
        try {
            voucherService.save(voucherDTO);
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObject(HttpStatus.OK, "Tạo voucher thành công", null)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ResponseObject(HttpStatus.INTERNAL_SERVER_ERROR, "Tạo voucher thất bại", e.getMessage())
            );
        }
    }

    @GetMapping("/all")
    public ResponseObject<?> getAllVoucher() {
        return ResponseObject.builder()
                .status(HttpStatus.OK)
                .message("Lấy danh sách voucher thành công")
                .data(voucherService.findAll())
                .build();
    }

    @GetMapping("/codevoucher/{codeVoucher}")
    public ResponseObject<?> getVoucherByCode(@PathVariable String codeVoucher) {
        VoucherDTO voucherDTO = voucherService.findByCodeVoucher(codeVoucher);

        if (voucherDTO == null) {
            return ResponseObject.builder()
                    .status(HttpStatus.NOT_FOUND)
                    .message("Không có voucher có id = " + codeVoucher)
                    .build();
        }

        return ResponseObject.builder()
                .status(HttpStatus.OK)
                .message("Lấy voucher thành công")
                .data(voucherService.findByCodeVoucher(codeVoucher))
                .build();
    }

    @GetMapping("/id/{id}")
    public ResponseObject<?> getVoucherById(@PathVariable Integer id) {
        VoucherDTO voucherDTO = voucherService.findBySysIdVoucher(id);

        if (voucherDTO == null) {
            return ResponseObject.builder()
                    .status(HttpStatus.NOT_FOUND)
                    .message("Không có voucher có id = " + id)
                    .build();
        }

        return ResponseObject.builder()
                .status(HttpStatus.OK)
                .message("Lấy voucher thành công")
                .data(voucherService.findBySysIdVoucher(id))
                .build();
    }

//    @PutMapping("/{id}")
//    public ResponseObject<?> updateVoucher(@PathVariable Integer id, @RequestBody VoucherDTO dto) {
//        VoucherDTO voucherDTO = voucherService.findBySysIdVoucher(id);
//
//        if (voucherDTO == null) {
//            return ResponseObject.builder()
//                    .status(HttpStatus.NOT_FOUND)
//                    .message("Không có voucher có id = " + id)
//                    .build();
//        }
//
//        voucherDTO.setCodeVoucher(dto.getCodeVoucher());
//        voucherDTO.setDescription(dto.getDescription());
//        voucherDTO.setDiscountName(dto.getDiscountName());
//        voucherDTO.setEndDate(dto.getEndDate());
//        voucherDTO.setStartDate(dto.getStartDate());
//        voucherDTO.setDiscountPercent(dto.getDiscountPercent());
//
//        return ResponseObject.builder()
//                .status(HttpStatus.OK)
//                .message("Cập nhật voucher thành công")
//                .data(voucherService.save(voucherMapper.toEntity(voucherDTO)))
//                .build();
//    }

    @DeleteMapping("/{id}")
    public ResponseObject<?> deleteVoucher(@PathVariable Integer id) {
        VoucherDTO voucherDTO = voucherService.findBySysIdVoucher(id);

        if (voucherDTO == null) {
            return ResponseObject.builder()
                    .status(HttpStatus.NOT_FOUND)
                    .message("Không có voucher có id = " + id)
                    .build();
        }

        voucherService.deleteBySysIdVoucher(id);
        return ResponseObject.builder()
                .status(HttpStatus.OK)
                .message("Xóa voucher thành công")
                .build();
    }

    @GetMapping("/p/top-limited-discount")
    public ResponseObject<?> getTopVouchersByEndDateNearest(@RequestParam int page, @RequestParam int size) {
        return ResponseObject.builder()
                .status(HttpStatus.OK)
                .data(voucherService.findTopByEndDateNearest(page, size))
                .build();
    }

    @GetMapping("/p/all")
    public ResponseObject<?> getPageVoucher(@RequestParam int page, @RequestParam int size) {
        return ResponseObject.builder()
                .status(HttpStatus.OK)
                .data(voucherService.getPageVoucher(page, size))
                .build();
    }

    @PostMapping("/validate/{codeVoucher}")
    public ResponseObject<?> validateVoucher(@PathVariable String codeVoucher) {
        return ResponseObject.builder()
                .status(HttpStatus.OK)
                .data(voucherService.validVoucherByUser(codeVoucher))
                .build();
    }

    @PostMapping("/send/{codeVoucher}")
    public ResponseObject<?> sendVoucher(@PathVariable String codeVoucher) {
        voucherService.sendVoucherToUser(codeVoucher);
        return ResponseObject.builder()
                .status(HttpStatus.OK)
                .message("Voucher is sent successfully to your email")
                .build();
    }
}
