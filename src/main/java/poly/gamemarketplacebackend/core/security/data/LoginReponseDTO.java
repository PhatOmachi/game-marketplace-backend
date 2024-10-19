package poly.gamemarketplacebackend.core.security.data;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class LoginReponseDTO {
    private String token;
}
