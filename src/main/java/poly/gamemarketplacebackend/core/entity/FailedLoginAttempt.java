package poly.gamemarketplacebackend.core.entity;

import lombok.*;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "failed_login_attempt")
public class FailedLoginAttempt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private int failedAttempts;

    @Column(nullable = false)
    private LocalDateTime lastFailedTime;
}