package poly.gamemarketplacebackend.core.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Game")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sys_id_game")
    private Integer sysIdGame;

    @Column(name = "game_code")
    private String gameCode;

    @Column(name = "game_name")
    private String gameName;

    @Column(name = "status")
    private Boolean status = true;

    @Column(name = "price")
    private Float price = 0.0f;

    @Column(name = "discount_percent")
    private Float discountPercent;

    @Column(name = "game_image")
    private String gameImage;

    @Column(name = "slug", unique = true, nullable = false)
    private String slug;

    @Column(name = "game_category")
    private String gameCategory;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "quantity_sold")
    private Integer quantitySold;

    @Column(name = "quantity_count")
    private Integer quantityCount;

    @ManyToOne
    @JoinColumn(name = "sys_id_voucher", referencedColumnName = "sys_id_voucher")
    @JsonIgnore
    private Voucher voucher;
}
