package poly.gamemarketplacebackend.core.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Category_detail")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDetail {
    @Id
    @Column(name = "sys_id_category")
    private Integer sysIdCategory;

    @Id
    @Column(name = "sys_id_game")
    private Integer sysIdGame;

    @ManyToOne
    @JoinColumn(name = "sys_id_category", referencedColumnName = "sys_id_category", insertable = false, updatable = false)
    private Category category;

    @ManyToOne
    @JoinColumn(name = "sys_id_game", referencedColumnName = "sys_id_game", insertable = false, updatable = false)
    private Game game;
}
