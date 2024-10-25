package poly.gamemarketplacebackend.core.dto;

import lombok.Data;

@Data
public class AccountDTO {
    private int id;
    private String username;
    private String email;
    private String hashPassword;
    private boolean isEnabled;

    @Override
    public String toString() {
        return "AccountDTO{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", hashPassword='" + hashPassword + '\'' +
                ", isEnabled=" + isEnabled +
                '}';
    }
}
