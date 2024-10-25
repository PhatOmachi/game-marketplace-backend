package poly.gamemarketplacebackend.core.security.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import poly.gamemarketplacebackend.core.entity.Account;
//import poly.java5divineshop.Divineshop.Data.Entity.AccountE;

import java.util.Collection;
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class CustomUserDetails implements UserDetails {

    private Account account;
    private Collection<? extends GrantedAuthority> getAuthorities;
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return getAuthorities;
    }
    @Override
    public String getPassword() {
        return account.getHashPassword();
    }
    @Override
    public String getUsername() {
        return account.getUsername();
    }
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    @Override
    public boolean isEnabled() {
        return account.isEnabled();
    }
}
