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
import poly.gamemarketplacebackend.core.dto.VoucherDTO;
import poly.gamemarketplacebackend.core.entity.Voucher;
import poly.gamemarketplacebackend.core.exception.CustomException;
import poly.gamemarketplacebackend.core.mapper.VoucherMapper;
import poly.gamemarketplacebackend.core.repository.VoucherRepository;
import poly.gamemarketplacebackend.core.security.service.AuthService;
import poly.gamemarketplacebackend.core.service.UsersService;
import poly.gamemarketplacebackend.core.service.VoucherService;

import java.text.NumberFormat;
import java.time.LocalDate;
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
        log.info("User id: {}, code voucher: {}", userId, codeVoucher);
        var voucher = voucherRepository.findValidVoucher(codeVoucher, userId)
                .orElseThrow(() -> new CustomException("Invalid voucher or already used", HttpStatus.NOT_ACCEPTABLE));
        return voucherMapper.toDTO(voucher);
    }

    @Override
    public VoucherDTO findBySysIdVoucher(Integer id) {
        return voucherMapper.toDTO(voucherRepository.findBySysIdVoucher(id));
    }

    @Override
    public VoucherDTO save(Voucher voucher) {
        return voucherMapper.toDTO(voucherRepository.save(voucher));
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
