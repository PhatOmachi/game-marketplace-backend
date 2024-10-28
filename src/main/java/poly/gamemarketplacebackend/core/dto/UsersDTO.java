package poly.gamemarketplacebackend.core.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UsersDTO {
    private int sysIdUser;
    private String username;
    private String email;
    private String hoVaTen;
    private String balance;
    private LocalDateTime joinTime;
    private String avatar;
    private Float totalSpent;
}
