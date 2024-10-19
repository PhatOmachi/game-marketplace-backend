package poly.gamemarketplacebackend.core.dto;

import lombok.Data;

@Data
public class AccountDTO {
    private int id;
    private String username;
    private String email;
    private String hashPassword;
    private boolean isEnabled;
}
