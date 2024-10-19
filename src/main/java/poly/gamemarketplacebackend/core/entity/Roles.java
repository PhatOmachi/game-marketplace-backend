package poly.gamemarketplacebackend.core.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.Data;

import jakarta.persistence.*;

@Entity
@Data
public class Roles {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int sysIdRole;

    @Column(nullable = false, insertable = false, updatable = false)
    private String username;

    @Column(nullable = false)
    private String usernameUser;

    @Column(nullable = false)
    private String role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "username", referencedColumnName = "username")
    @JsonIgnore
    private Account account;
}