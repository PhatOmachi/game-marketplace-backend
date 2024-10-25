package poly.gamemarketplacebackend.core.service.impl;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import poly.gamemarketplacebackend.core.constant.Role;
import poly.gamemarketplacebackend.core.dto.AccountDTO;
import poly.gamemarketplacebackend.core.entity.Account;
import poly.gamemarketplacebackend.core.exception.CustomException;
import poly.gamemarketplacebackend.core.mapper.AccountMapper;
import poly.gamemarketplacebackend.core.repository.AccountRepository;
import poly.gamemarketplacebackend.core.service.AccountService;
import poly.gamemarketplacebackend.core.util.DataStore;
import poly.gamemarketplacebackend.core.util.OTPUtil;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;
    private final JavaMailSender emailSender;
    private final AccountMapper accountMapper;
    private final PasswordEncoder passwordEncoder;
    //    private final HttpSession session;
    private final DataStore dataStore;

    @Override
    public List<AccountDTO> getAllAccount() {
        List<Account> accounts = accountRepository.findAll();
        return accountMapper.toDTOs(accounts);
    }

    @Override
    public AccountDTO findByUsername(String username) {
        Account account = accountRepository.findByUsername(username);
        return account == null ? null : accountMapper.toDTO(account);
    }

    @Override
    public Account findByUsernameSecurity(String username) {
        return accountRepository.findByUsernameQuerySecurity(username);
    }

    @Override
    public AccountDTO findByEmail(String email) {
        Account account = accountRepository.findByEmail(email);
        return account == null ? null : accountMapper.toDTO(account);
    }

    @Override
    public Account saveAccount(AccountDTO accountDTO) {
        return accountRepository.save(accountMapper.toEntity(accountDTO));
    }

    @Override
    @SneakyThrows
    public void sendMailForUser(String email, String otp, String subject) {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(email);
        helper.setSubject(subject);
        helper.setText("Mã OTP của bạn là: " + otp);
        emailSender.send(message);
    }

    @Override
    public int updatePassAccountByEmail(AccountDTO accountDTO) {
        return accountRepository.updatePassAccountByEmail(passwordEncoder.encode(accountDTO.getHashPassword()), accountDTO.getEmail());
    }

    @Override
    public Account getAccountByUsername(String username) {
        return accountRepository.findByUsernameOrEmailAndIsEnabledTrue(username, username);
    }

    @Override
    public boolean isUniqueCredentials(String username, String email) {
        if (accountRepository.findByUsername(username) != null) {
            throw new CustomException("Username đã tồn tại", HttpStatus.BAD_REQUEST);
        } else if (accountRepository.findByEmail(email) != null) {
            throw new CustomException("Email đã tồn tại", HttpStatus.BAD_REQUEST);
        }
        return true;
    }

    @Override
    public void requestRegistration(AccountDTO accountDTO) {
        if (isUniqueCredentials(accountDTO.getUsername(), accountDTO.getEmail())) {
            String otp = OTPUtil.generateOTP();
            dataStore.put(accountDTO.getEmail() + "otp", otp);
            dataStore.put(accountDTO.getEmail() + "otpTime", System.currentTimeMillis());
            dataStore.put(accountDTO.getEmail() + "account", accountDTO);
            sendMailForUser(accountDTO.getEmail(), otp, "Mã OTP cho đăng ký tài khoản");
        }
    }

    @Override
    public void verifyOTP(String otp, String email) {
        String otpSession = (String) dataStore.get(email + "otp");
        Long otpTime = (Long) dataStore.get(email + "otpTime");
//        System.out.println(otpSession + " | " + otpTime);
        if (otpTime == null || (System.currentTimeMillis() - otpTime) > 2 * 60 * 1000) {
            throw new CustomException("Mã OTP đã hết hạn", HttpStatus.BAD_REQUEST);
        } else if (otp.equals(otpSession)) {
            AccountDTO accountDTO = (AccountDTO) dataStore.get(email + "account");
//            System.out.println(accountDTO.toString());
            accountDTO.setEnabled(true);
            accountDTO.setHashPassword(passwordEncoder.encode(accountDTO.getHashPassword()));
            saveAccount(accountDTO);
            accountRepository.insertUserAndRole(accountDTO.getUsername(), accountDTO.getEmail(), Role.CUSTOMER.name());
            dataStore.bulkRemoveStartsWith(email);
        } else {
            throw new CustomException("Mã OTP không đúng", HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public void resendOTP(String email) {
        AccountDTO accountDTO = (AccountDTO) dataStore.get(email + "account");
        if (accountDTO != null) {
            String otp = OTPUtil.generateOTP();
            dataStore.put(email + "otp", otp);
            dataStore.put(email + "otpTime", System.currentTimeMillis());
            sendMailForUser(accountDTO.getEmail(), otp, "Mã OTP cho đăng ký tài khoản");
        } else {
            throw new CustomException("Không tìm thấy thông tin tài khoản", HttpStatus.BAD_REQUEST);
        }
    }
}