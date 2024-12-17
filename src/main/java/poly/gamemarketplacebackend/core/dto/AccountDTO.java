package poly.gamemarketplacebackend.core.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class AccountDTO {
    private int id;
    private String username;
    private String email;
    private String hashPassword;
    private boolean isEnabled;
    private String oldPassword;
    private String newPassword;

    // tạo biến cho api insert-account-user-role
    private String hoVaTen;
    private String phoneNumber;
    private List<String> files;
    private String fileName;
    // end tạo biến cho api insert-account-user-role

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
