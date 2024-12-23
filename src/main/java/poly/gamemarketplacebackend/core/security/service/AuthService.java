package poly.gamemarketplacebackend.core.security.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import poly.gamemarketplacebackend.core.entity.Account;
import poly.gamemarketplacebackend.core.exception.CustomException;
import poly.gamemarketplacebackend.core.security.data.CustomUserDetails;
import poly.gamemarketplacebackend.core.security.data.LoginReponseDTO;
import poly.gamemarketplacebackend.core.security.data.LoginRequestDTO;
import poly.gamemarketplacebackend.core.security.jwt.JwtUtil;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final CustomLoginAttemptService loginAttemptService;
    private final JwtUtil jwtUtil;

    public LoginReponseDTO authenticate(LoginRequestDTO dto, String sessionId) {
        if (loginAttemptService.isBlocked(dto.getUsername())) {
            throw new CustomException("Your account is locked due to multiple failed login attempts. Please try again in 30 minutes.", HttpStatus.LOCKED);
        }
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword()));
            var userDetails = (CustomUserDetails) authentication.getPrincipal();
            loginAttemptService.resetFailedAttempts(dto.getUsername());
            return LoginReponseDTO.builder()
                    .token(jwtUtil.generateToken(userDetails, sessionId))
                    .build();
        } catch (BadCredentialsException e) {
            loginAttemptService.loginFailed(dto.getUsername());
            throw new CustomException("Incorrect login information", HttpStatus.UNAUTHORIZED);
        }
    }

    public static Account getCurrentAccount() {
        return ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getAccount();
    }

}