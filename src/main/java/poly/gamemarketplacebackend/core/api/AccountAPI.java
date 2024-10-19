package poly.gamemarketplacebackend.core.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import poly.gamemarketplacebackend.core.constant.ResponseObject;
import poly.gamemarketplacebackend.core.dto.AccountDTO;
import poly.gamemarketplacebackend.core.service.AccountService;

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
                .message("Vui lòng kiểm tra email để xác nhận tài khoản")
                .build();
    }

    @PostMapping("/verify-registration-otp")
    public ResponseObject<?> verify(@RequestParam String otp) {
        accountService.verifyOTP(otp);
        return ResponseObject.builder()
                .status(HttpStatus.OK)
                .message("Xác nhận tài khoản thành công")
                .build();
    }

    @PostMapping("/resend-registration-otp")
    public ResponseObject<?> resend() {
        accountService.resendOTP();
        return ResponseObject.builder()
                .status(HttpStatus.OK)
                .message("Vui lòng kiểm tra email để xác nhận tài khoản")
                .build();
    }
}