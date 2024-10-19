package poly.gamemarketplacebackend.core.security.authentication;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import poly.gamemarketplacebackend.core.security.data.CustomUserDetails;
//import poly.java5divineshop.ConfigSecurity.Security.CustomUserDetails;

public class CustomUserDetailsAuthenticationToken extends AbstractAuthenticationToken {
    private final CustomUserDetails userDetails;

    public CustomUserDetailsAuthenticationToken(CustomUserDetails userDetails) {
        super(userDetails.getAuthorities());
        this.userDetails = userDetails;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public CustomUserDetails getPrincipal() {
        return userDetails;
    }
}
