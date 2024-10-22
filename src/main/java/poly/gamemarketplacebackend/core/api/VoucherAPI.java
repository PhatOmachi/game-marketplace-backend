package poly.gamemarketplacebackend.core.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import poly.gamemarketplacebackend.core.constant.ResponseObject;
import poly.gamemarketplacebackend.core.dto.VoucherDTO;
import poly.gamemarketplacebackend.core.mapper.VoucherMapper;
import poly.gamemarketplacebackend.core.service.VoucherService;

@RestController
@Slf4j
@RequestMapping("/api/vouchers")
@RequiredArgsConstructor
public class VoucherAPI {
    private final VoucherService voucherService;
    private final VoucherMapper voucherMapper;

    @PostMapping("/create")
    public ResponseObject<?> createVoucher(@RequestBody VoucherDTO voucherDTO) {
        voucherService.save(voucherMapper.toEntity(voucherDTO));
        return ResponseObject.builder()
                .status(HttpStatus.OK)
                .message("Tạo voucher thành công")
                .build();
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

    @PutMapping("/{id}")
    public ResponseObject<?> updateVoucher(@PathVariable Integer id, @RequestBody VoucherDTO dto) {
        VoucherDTO voucherDTO = voucherService.findBySysIdVoucher(id);

        if (voucherDTO == null) {
            return ResponseObject.builder()
                    .status(HttpStatus.NOT_FOUND)
                    .message("Không có voucher có id = " + id)
                    .build();
        }

        voucherDTO.setCodeVoucher(dto.getCodeVoucher());
        voucherDTO.setDescription(dto.getDescription());
        voucherDTO.setDiscountName(dto.getDiscountName());
        voucherDTO.setEndDate(dto.getEndDate());
        voucherDTO.setStartDate(dto.getStartDate());
        voucherDTO.setDiscountPercent(dto.getDiscountPercent());

        return ResponseObject.builder()
                .status(HttpStatus.OK)
                .message("Cập nhật voucher thành công")
                .data(voucherService.save(voucherMapper.toEntity(voucherDTO)))
                .build();
    }

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
}
