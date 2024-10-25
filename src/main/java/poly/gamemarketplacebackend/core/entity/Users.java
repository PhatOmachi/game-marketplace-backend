package poly.gamemarketplacebackend.core.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int sysIdUser ;

    @Column(name = "user_name", nullable = false, unique = true, length = 255)
    private String username;

    @Column(name = "email", nullable = false, unique = true, length = 255)
    private String email;

    @Column(name = "ho_va_ten", length = 255)
    private String hoVaTen;

    @Column(name = "balance", length = 255)
    private String balance;

    @Column(name = "join_time")
    private LocalDateTime joinTime;

    @Column(name = "avatar")
    private String avatar;
}
