package poly.gamemarketplacebackend.core.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int sysIdCategoryDetail;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "sys_id_category", nullable = false)
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "sys_id_game", nullable = false)
    private Game game;
}
