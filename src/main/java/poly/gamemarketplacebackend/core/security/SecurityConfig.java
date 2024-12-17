package poly.gamemarketplacebackend.core.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import poly.gamemarketplacebackend.core.constant.Role;
import poly.gamemarketplacebackend.core.security.jwt.JwtRequestFilter;
import poly.gamemarketplacebackend.core.security.service.CustomUserDetailsService;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final CustomUserDetailsService customUserDetailsService;
    private final JwtRequestFilter jwtRequestFilter;
    private final PasswordEncoder passwordEncoder;
    private final String[] allowedOrigins = {
            "http://localhost:5173",
            "http://127.0.0.1:5500"
    };
    public final static String[] nonAuthenticatedUrls = {
            "/api/auth/login",
            "/api/public/**",
            "/api/accounts/**",
            "/api/accounts/*/**",
            "/api/*/p/**",
            "/api/*/p/*/**",
            "/api/*/p/*/*/**",
            "/api/*/p/*/*/*/**",
            "/images/*/**",
            "/VoucherImages/*/**",
            "/CustomerImages/*/**",
            "/api/chat/**",
            "/chatUsertoAdmin"
    };
    private final String[] authenticatedUrls = {
            "/cart", "/user-info", "/order-history", "/transaction", "/security", "/favorite", "/add-funds", "/userinfo"
    };
    private final String[] staffUrls = {
            "/leaders"
    };
    private final String[] adminUrls = {
            "/admin/**"
    };

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors
                        .configurationSource(corsConfigurationSource()))
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(configurer -> configurer
                        .requestMatchers(nonAuthenticatedUrls).permitAll()
                        .requestMatchers(authenticatedUrls).authenticated()
                        .requestMatchers(staffUrls).hasRole(Role.STAFF.name())
                        .requestMatchers(adminUrls).hasRole(Role.ADMIN.name())
                        .anyRequest().authenticated()
                )
        ;

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(allowedOrigins));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        var builder = http.getSharedObject(AuthenticationManagerBuilder.class);
        builder
                .userDetailsService(customUserDetailsService)
                .passwordEncoder(passwordEncoder);
        return builder.build();
    }
}
