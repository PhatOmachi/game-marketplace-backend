package poly.gamemarketplacebackend.core.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import poly.gamemarketplacebackend.core.constant.ResponseObject;
import poly.gamemarketplacebackend.core.dto.AccountDTO;
import poly.gamemarketplacebackend.core.service.AccountService;

import java.io.IOException;
import java.sql.SQLException;

@RestController
@Slf4j
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountAPI {
    private final AccountService accountService;

    @PostMapping("/register")
    public ResponseObject<?> register(@RequestBody AccountDTO accountDTO) {
        accountService.requestRegistration(accountDTO);
        return ResponseObject.builder()
                .status(HttpStatus.OK)
                .message("Please check email for registration verification")
                .build();
    }

    @PostMapping("/verify-registration-otp")
    public ResponseObject<?> verify(@RequestParam String otp, @RequestParam String email) {
        accountService.verifyOTP(otp, email);
        return ResponseObject.builder()
                .status(HttpStatus.OK)
                .message("Account verified successfully")
                .build();
    }

    @PostMapping("/resend-registration-otp")
    public ResponseObject<?> resend(@RequestParam String email) {
        accountService.resendOTP(email);
        return ResponseObject.builder()
                .status(HttpStatus.OK)
                .message("Please check email for registration verification")
                .build();
    }

    @PostMapping("/forgot-password")
    public ResponseObject<?> forgotPassword(@RequestParam String email) {
        accountService.requestPasswordReset(email);
        return ResponseObject.builder()
                .status(HttpStatus.OK)
                .message("Please check your email to verify OTP")
                .build();
    }

    @PostMapping("/forgot-password/verify-otp")
    public ResponseObject<?> verifyForgotPasswordOTP(@RequestParam String email, @RequestParam String otp) {
        accountService.verifyForgotPasswordOTP(email, otp);
        return ResponseObject.builder()
                .status(HttpStatus.OK)
                .message("OTP is correct")
                .build();
    }

    @PutMapping("/forgot-password/new-password")
    public ResponseObject<?> updatePassword(@RequestParam String email, @RequestParam String newPass) {
        accountService.updatePassword(email, newPass);
        return ResponseObject.builder()
                .status(HttpStatus.OK)
                .message("Password updated")
                .build();
    }

    @PostMapping("/forgot-password/resend-otp")
    public ResponseObject<?> resendOTPForForgotPassword(@RequestParam String email) {
        accountService.resendOTPForForgotPassword(email);
        return ResponseObject.builder()
                .status(HttpStatus.OK)
                .message("Please check your email for OTP")
                .build();
    }

    @PostMapping("/change-password")
    public ResponseObject<?> changePassword(@RequestBody AccountDTO accountDTO) {
        boolean result = accountService.changePassword(
                accountDTO.getUsername(),
                accountDTO.getOldPassword(),
                accountDTO.getNewPassword()
        );
        return ResponseObject.builder()
                .status(HttpStatus.OK)
                .message("Update password Sucess")
                .build();

    }

    @PostMapping("/insert-account-user-role")
    public ResponseObject<?> insertAccountUserAndRole(@RequestBody AccountDTO accountDTO) throws SQLException, IOException {
        accountService.insertAccountUserAndRole(accountDTO);
        return ResponseObject.builder()
                .status(HttpStatus.OK)
                .message("Insert account user and role success")
                .build();
    }

    @PostMapping("/verify-pass")
    public ResponseObject<?> verifyPass(@RequestParam("username") String userName, @RequestParam("pass") String pass) throws SQLException, IOException {
        Boolean check =  accountService.verifyPw(userName,pass);
        return ResponseObject.builder()
                .status(HttpStatus.OK)
                .data(check)
                .message("Insert account user and role success")
                .build();
    }

    @PostMapping("/update-email")
    public ResponseObject<?> updateEmail(@RequestParam("username") String userName, @RequestParam("email") String email) throws SQLException, IOException {
        accountService.updateEmail(userName,email);
        return ResponseObject.builder()
                .status(HttpStatus.OK)
                .message("update success")
                .build();
    }


}