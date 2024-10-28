package poly.gamemarketplacebackend.core.service.impl;

import lombok.RequiredArgsConstructor;
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

import java.util.List;

@Service
@RequiredArgsConstructor
public class VoucherServiceImpl implements VoucherService {

    private final VoucherRepository voucherRepository;
    private final VoucherMapper voucherMapper;
    private final UsersService usersService;

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
        var userId = AuthService.getCurrentAccount().getId();
        var voucher = voucherRepository.findValidVoucher(codeVoucher, userId)
                .orElseThrow(() -> new CustomException("Invalid voucher", HttpStatus.OK));
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
}
