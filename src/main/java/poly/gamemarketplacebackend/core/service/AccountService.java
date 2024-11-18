package poly.gamemarketplacebackend.core.service;

import poly.gamemarketplacebackend.core.dto.AccountDTO;
import poly.gamemarketplacebackend.core.entity.Account;

import java.sql.SQLException;


public interface AccountService {

    AccountDTO findByUsername(String username);

    Account saveAccount(AccountDTO accountDTO) throws SQLException;

    void sendMailForUser(String email, String otp, String subject);

    Account getAccountByUsername(String username);

    boolean isUniqueCredentials(String username, String email);

    void requestRegistration(AccountDTO accountDTO);

    void verifyOTP(String otp, String email);

    void resendOTP(String email);

    void requestPasswordReset(String email);

    void verifyForgotPasswordOTP(String email, String otp);

    void updatePassword(String email, String newPass);

    void resendOTPForForgotPassword(String email);

    boolean changePassword(String username, String passwordOld, String passwordNew);
}
