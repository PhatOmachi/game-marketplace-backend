package poly.gamemarketplacebackend.core.security.data;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequestDTO {
    @NotBlank(message = "Username must not be empty!")
    private String username;
    @NotBlank(message = "Password must not be empty!")
    private String password;
}
