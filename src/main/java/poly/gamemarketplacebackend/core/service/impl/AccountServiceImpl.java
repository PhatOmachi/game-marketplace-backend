package poly.gamemarketplacebackend.core.service.impl;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {
    private static final Logger log = LoggerFactory.getLogger(AccountServiceImpl.class);
    private final AccountRepository accountRepository;
    private final JavaMailSender emailSender;
    private final AccountMapper accountMapper;
    private final PasswordEncoder passwordEncoder;
    private final DataStore dataStore;

    @Override
    public AccountDTO findByUsername(String username) {
        Account account = accountRepository.findByUsername(username);
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
    public Account getAccountByUsername(String username) {
        return accountRepository.findByUsernameOrEmailAndIsEnabledTrue(username, username);
    }

    @Override
    public boolean isUniqueCredentials(String username, String email) {
        if (accountRepository.findByUsername(username) != null) {
            throw new CustomException("User is already existed", HttpStatus.BAD_REQUEST);
        } else if (accountRepository.findByEmail(email) != null) {
            throw new CustomException("Email is already existed", HttpStatus.BAD_REQUEST);
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
            sendMailForUser(accountDTO.getEmail(), otp, "OTP for registration");
        }
    }

    @Override
    public void verifyOTP(String otp, String email) {
        String otpSession = (String) dataStore.get(email + "otp");
        Long otpTime = (Long) dataStore.get(email + "otpTime");
        if (otpTime == null || (System.currentTimeMillis() - otpTime) > 2 * 60 * 1000) {
            throw new CustomException("OTP is expired", HttpStatus.BAD_REQUEST);
        } else if (otp.equals(otpSession)) {
            AccountDTO accountDTO = (AccountDTO) dataStore.get(email + "account");
            accountDTO.setEnabled(true);
            accountDTO.setHashPassword(passwordEncoder.encode(accountDTO.getHashPassword()));
            saveAccount(accountDTO);
            accountRepository.insertUserAndRole(accountDTO.getUsername(), accountDTO.getEmail(), Role.CUSTOMER.name());
            dataStore.bulkRemoveStartsWith(email);
        } else {
            throw new CustomException("OTP is incorrect", HttpStatus.BAD_REQUEST);
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
            throw new CustomException("Cannot found any account", HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public void requestPasswordReset(String email) {
        Account account = accountRepository.findByEmail(email);
        if (account == null || !account.isEnabled()) {
            throw new CustomException("Email is not existed or has not been activated", HttpStatus.BAD_REQUEST);
        }
        String otp = OTPUtil.generateOTP();
        dataStore.put(email + "otp", otp);
        dataStore.put(email + "otp_exp", System.currentTimeMillis() + 2 * 60 * 1000);
        sendMailForUser(email, otp, "OTP for reset password");
    }

    @Override
    public void verifyForgotPasswordOTP(String email, String otp) {
        String storedOtp = (String) dataStore.get(email + "otp");
        Long otpExp = (Long) dataStore.get(email + "otp_exp");
        log.info("Stored OTP: {}, Expire on {}", storedOtp, otpExp);
        if (storedOtp == null || otpExp == null || System.currentTimeMillis() > otpExp || !storedOtp.equals(otp)) {
            throw new CustomException("OTP is invalid or expired", HttpStatus.BAD_REQUEST);
        }
        dataStore.remove(email + "otp");
        dataStore.remove(email + "otp_exp");
        dataStore.put(email + "approveForgotPassword", true);
    }

    @Override
    public void updatePassword(String email, String newPass) {
        Boolean isApproved = (Boolean) dataStore.get(email + "approveForgotPassword");
        if (isApproved == null || !isApproved) {
            throw new CustomException("Reset password request is not valid", HttpStatus.BAD_REQUEST);
        }
        Account account = accountRepository.findByEmail(email);
        if (account == null) {
            throw new CustomException("Email is not existed", HttpStatus.BAD_REQUEST);
        }
        account.setHashPassword(passwordEncoder.encode(newPass));
        accountRepository.save(account);
        dataStore.remove(email + "approveForgotPassword");
    }

    @Override
    public void resendOTPForForgotPassword(String email) {
        var otp = dataStore.get(email + "otp");
        var otpExp = dataStore.get(email + "otp_exp");
        if (otp == null || otpExp == null) {
            throw new CustomException("OTP has been not requested before", HttpStatus.BAD_REQUEST);
        }
        String newOtp = OTPUtil.generateOTP();
        dataStore.put(email + "otp", otp);
        dataStore.put(email + "otp_exp", System.currentTimeMillis() + 2 * 60 * 1000);
        sendMailForUser(email, newOtp, "OTP for reset password");
    }

}