package poly.gamemarketplacebackend.core.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@Table(name = "Users")
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sys_id_user")
    private int sysIdUser;

    @Column(name = "user_name", nullable = false, unique = true)
    private String username;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "ho_va_ten")
    private String hoVaTen;

    @Column(name = "balance")
    private String balance;

    @Column(name = "join_time")
    private LocalDateTime joinTime;

    @Column(name = "avatar")
    private String avatar;

    @Column(name = "gender", nullable = false)
    private Boolean gender;

    @Column(name = "DOB")
    private LocalDate DOB;

    @Column(name = "phone_number", length = 11)
    private String phoneNumber;

    @OneToMany(mappedBy = "users")
    @JsonManagedReference
    private List<Orders> orders;

    @OneToMany(mappedBy = "user")
    private List<OwnedGame> ownedGames;
}
