package poly.gamemarketplacebackend.core.security.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import poly.gamemarketplacebackend.core.constant.ResponseObject;
import poly.gamemarketplacebackend.core.security.data.LoginRequestDTO;
import poly.gamemarketplacebackend.core.security.jwt.JwtUtil;
import poly.gamemarketplacebackend.core.security.service.AuthService;
import poly.gamemarketplacebackend.core.security.service.TokenBlacklistService;
import poly.gamemarketplacebackend.core.service.UsersService;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final JwtUtil jwtUtil;
    private final AuthService authService;
    private final UsersService usersService;
    private final HttpServletRequest httpServletRequest;
    private final TokenBlacklistService tokenBlacklistService;

    @PostMapping("/login")
    public ResponseObject<?> doLogin(@RequestBody @Valid LoginRequestDTO dto) {
        var session = httpServletRequest.getSession();
        return ResponseObject.builder()
                .status(HttpStatus.OK)
                .data(authService.authenticate(dto, session.getId()))
                .message("Login successfully")
                .build();
    }

    @GetMapping("/current-user")
    public ResponseObject<?> getCurrentUser() {
        var username = jwtUtil.extractUsername(httpServletRequest.getHeader("Authorization").substring(7));
        return ResponseObject.builder()
                .status(HttpStatus.OK)
                .data(usersService.findByUsername(username))
                .build();
    }

    @GetMapping("/is-login")
    public ResponseObject<?> isLogin() {
        String token = httpServletRequest.getHeader("Authorization").substring(7);
        return ResponseObject.builder()
                .status(HttpStatus.OK)
                .message("Hello " + jwtUtil.extractUsername(token) + "!")
                .build();
    }

    @PostMapping("/logout")
    public ResponseObject<?> logout() {
        String token = httpServletRequest.getHeader("Authorization").substring(7);
        tokenBlacklistService.blacklistToken(token);
        return ResponseObject.builder()
                .status(HttpStatus.OK)
                .data(tokenBlacklistService.isTokenBlacklisted(token))
                .message("Logout successfully")
                .build();
    }
}

