package poly.gamemarketplacebackend.core.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import poly.gamemarketplacebackend.core.dto.VoucherDTO;
import poly.gamemarketplacebackend.core.entity.Voucher;
import poly.gamemarketplacebackend.core.exception.CustomException;
import poly.gamemarketplacebackend.core.mapper.VoucherMapper;
import poly.gamemarketplacebackend.core.repository.VoucherRepository;
import poly.gamemarketplacebackend.core.security.service.AuthService;
import poly.gamemarketplacebackend.core.service.UsersService;
import poly.gamemarketplacebackend.core.service.VoucherService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Base64;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class VoucherServiceImpl implements VoucherService {

    private static final Logger log = LoggerFactory.getLogger(VoucherServiceImpl.class);
    private final VoucherRepository voucherRepository;
    private final VoucherMapper voucherMapper;
    private final UsersService usersService;
    private final EmailServiceImpl emailService;

    @Override
    public List<VoucherDTO> findAll() {
        return voucherMapper.toDTOList(voucherRepository.findAll());
    }

    @Override
    public VoucherDTO findByCodeVoucher(String codeVoucher) {
        return voucherMapper.toDTO(voucherRepository.findByCodeVoucher(codeVoucher));
    }

    @Override
    public VoucherDTO validVoucherByUser(String codeVoucher) {
        var userId = usersService.getCurrentUser().getSysIdUser();
        var voucher = voucherRepository.findValidVoucher(codeVoucher, userId)
                .orElseThrow(() -> new CustomException("Invalid voucher or already used", HttpStatus.NOT_ACCEPTABLE));
        return voucherMapper.toDTO(voucher);
    }

    @Override
    public VoucherDTO findBySysIdVoucher(Integer id) {
        return voucherMapper.toDTO(voucherRepository.findBySysIdVoucher(id));
    }

    @Override
    public void save(VoucherDTO voucherDTO) throws IOException {
        // Save images and get the path
        String voucherBanner = null;
        if (voucherDTO.getFiles() != null && !voucherDTO.getFiles().isEmpty()) {
            voucherBanner = saveImage(voucherDTO.getFiles().get(0), voucherDTO.getCodeVoucher());
        }

        // Insert voucher
        voucherRepository.insertVoucher(
                voucherDTO.getCodeVoucher(),
                voucherDTO.getDiscountName(),
                voucherDTO.getDiscountPercent(),
                voucherDTO.getStartDate(),
                voucherDTO.getEndDate(),
                voucherDTO.getDescription(),
                voucherBanner,
                voucherDTO.getQuantity(),
                voucherDTO.isActive(),
                voucherDTO.getMaxDiscount()
        );
    }

    private String saveImage(String base64Image, String codeVoucher) throws IOException {
        // Create directory for voucher images
        File voucherDir = new File("src/main/resources/static/VoucherImages/" + codeVoucher);
        if (!voucherDir.exists()) {
            voucherDir.mkdirs();
        }

        // Decode base64 image
        byte[] decodedBytes = Base64.getDecoder().decode(base64Image.split(",")[1]);
        String filePath = "src/main/resources/static/VoucherImages/" + codeVoucher + "/" + codeVoucher + ".jpg"; // Adjust the path and file name as needed

        // Save image to file
        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            fos.write(decodedBytes);
        }

        // Build full URL for the image
        String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
        return baseUrl + "/VoucherImages/" + codeVoucher + "/" + codeVoucher + ".jpg";
    }

    @Override
    @Transactional
    public void deleteBySysIdVoucher(Integer id) {
        voucherRepository.deleteBySysIdVoucher(id);
    }

    @Override
    public List<VoucherDTO> findTopByEndDateNearest(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return voucherMapper.toDTOList(voucherRepository.findTopByEndDateNearest(pageable).getContent());
    }

    @Override
    public List<VoucherDTO> getPageVoucher(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return voucherMapper.toDTOList(voucherRepository.findAllByPage(pageable).getContent());
    }

    @Override
    public void sendVoucherToUser(String codeVoucher) {
        var currentUser = usersService.getCurrentUser();
        VoucherDTO voucher = validVoucherByUser(codeVoucher);

        NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.GERMANY);
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");

        String formattedEndDate = LocalDate.parse(voucher.getEndDate().toString(), inputFormatter).format(outputFormatter);

        String emailContent = String.format(
                "Voucher's name: %s<br>Code: <b style='color: red;'>%s</b>, %d%% off, capped at %s VND.<br>" +
                        "You may have to be fast to use this voucher since only %d left, and expired on %s.<br>" +
                        "At check out screen, there's an input that you can enter this voucher's code to receive a huge discount.",
                voucher.getDiscountName(), voucher.getCodeVoucher(), voucher.getDiscountPercent(),
                numberFormat.format(voucher.getMaxDiscount()), voucher.getQuantity(), formattedEndDate
        );
        emailService.sendEmail(currentUser.getEmail(), "Your Voucher Details", emailContent);
    }

}
