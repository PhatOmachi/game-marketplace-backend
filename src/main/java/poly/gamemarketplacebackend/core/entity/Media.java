package poly.gamemarketplacebackend.core.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Media")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Media {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer sysIdMedia;

    @Column(name = "media_name")
    private String mediaName;

    @Column(name = "media_url", columnDefinition = "TEXT")
    private String mediaUrl;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sys_id_game", referencedColumnName = "sys_id_game")
    private Game game;
}