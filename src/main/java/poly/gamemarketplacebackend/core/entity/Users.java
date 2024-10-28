package poly.gamemarketplacebackend.core.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@Table(name = "Users")
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sys_id_user")
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

    @OneToMany(mappedBy = "users")
    @JsonManagedReference
    private List<Orders> orders;
}
