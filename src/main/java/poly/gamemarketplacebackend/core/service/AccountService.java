package poly.gamemarketplacebackend.core.service;

import poly.gamemarketplacebackend.core.dto.AccountDTO;
import poly.gamemarketplacebackend.core.entity.Account;

import java.sql.SQLException;
import java.util.List;


public interface AccountService {
    List<AccountDTO> getAllAccount();

    AccountDTO findByUsername(String username);

    Account findByUsernameSecurity(String username);

    AccountDTO findByEmail(String email) throws SQLException;

    Account saveAccount(AccountDTO accountDTO) throws SQLException;

    void sendMailForUser(String email, String otp, String subject);

    int updatePassAccountByEmail(AccountDTO accountDTO) throws SQLException;

    Account getAccountByUsername(String username);

    boolean isUniqueCredentials(String username, String email);

    void requestRegistration(AccountDTO accountDTO);

    void verifyOTP(String otp, String email);

    void resendOTP(String email);

    void requestPasswordReset(String email);

    void verifyForgotPasswordOTP(String email, String otp);

    void updatePassword(String email, String newPass);

    void resendOTPForForgotPassword(String email);
}
