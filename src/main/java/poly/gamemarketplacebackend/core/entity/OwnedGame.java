// OwnedGame.java
package poly.gamemarketplacebackend.core.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Owned_game")
public class OwnedGame {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int sysIdOwnedGame;

    private Timestamp ownedDate;

    @ManyToOne
    @JoinColumn(name = "sys_id_user")
    private Users user;

    @ManyToOne
    @JoinColumn(name = "sys_id_game")
    private Game game;
}