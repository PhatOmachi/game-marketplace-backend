package poly.gamemarketplacebackend.core.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "comment")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sys_id_comment")
    private Integer sysIdComment;

    @Column(name = "context", columnDefinition = "text default 'Game hay'")
    private String context;

    @Column(name = "comment_date")
    private LocalDateTime commentDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sys_id_user", nullable = false)
    private Users user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "sys_id_game", nullable = false)
    private Game game;

    @Column(name = "star", nullable = false, columnDefinition = "integer default 0")
    private Integer star;
}
