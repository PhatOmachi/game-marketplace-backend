package poly.gamemarketplacebackend.core.security.service;

import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import poly.gamemarketplacebackend.core.entity.Account;
import poly.gamemarketplacebackend.core.entity.Roles;
import poly.gamemarketplacebackend.core.security.data.CustomUserDetails;
import poly.gamemarketplacebackend.core.service.AccountService;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final AccountService accountService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = accountService.getAccountByUsername(username);
        if (account == null) {
            return null;
        }
        Collection<GrantedAuthority> getAuthoritySet = new HashSet<>();
        List<Roles> roleS = account.getRoles();
        for (Roles role : roleS) {
            getAuthoritySet.add(new SimpleGrantedAuthority(role.getRole()));
        }
        return new CustomUserDetails(account, getAuthoritySet);
    }

    public CustomUserDetails jwtToCustomUserDetails(DecodedJWT decodedJWT) {
        return CustomUserDetails.builder()
                .account(accountService.getAccountByUsername(decodedJWT.getSubject()))
                .build();
    }
}
