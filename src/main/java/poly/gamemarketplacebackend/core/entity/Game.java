package poly.gamemarketplacebackend.core.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "Game")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Game {
    @Id
    @Column(name = "sys_id_game")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer sysIdGame;

    @Column(name = "game_code")
    private String gameCode;

    @Column(name = "game_name")
    private String gameName;

    private Boolean status;

    private Float price;

    private Float discountPercent;

    private String gameImage;

    @Column(name = "slug", unique = true, nullable = false)
    private String slug;

    @Column(name = "game_category")
    private String gameCategory;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "is_active", columnDefinition = "BOOLEAN DEFAULT TRUE")
    private Boolean isActive;

    @Column(name = "sys_id_discount")
    private Integer sysIdDiscount;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "quantity_sold")
    private Integer quantitySold;

    @Column(name = "quantity_count")
    private Integer quantityCount;

    @ManyToOne
    @JoinColumn(name = "sys_id_discount", referencedColumnName = "sys_id_voucher", insertable = false, updatable = false)
    private Voucher voucher;

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CategoryDetail> categoryDetails;

}
