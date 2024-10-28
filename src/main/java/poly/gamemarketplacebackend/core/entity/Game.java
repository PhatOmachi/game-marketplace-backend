package poly.gamemarketplacebackend.core.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
@Table(name = "Game")
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sys_id_game")
    private int sysIdGame;

    @Column(name = "game_code", length = 255)
    private String gameCode;

    @Column(name = "game_name", length = 255)
    private String gameName;

    @Column(name = "status")
    private boolean status = true;

    @Column(name = "category_name", length = 255)
    private String categoryName;

    @Column(name = "price")
    private float price = 0.0f;

    @Column(name = "discount_pricent")
    private float discountPricent;

    @Column(name = "game_image", length = 255)
    private String gameImage;

    @Column(name = "slug", unique = true, nullable = false, length = 255)
    private String slug;

    @Column(name = "game_category", length = 255)
    private String gameCategory;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "is_active")
    private boolean isActive = true;

    @Column(name = "sys_id_discount")
    private Integer sysIdDiscount;

    @Column(name = "quantity")
    private int quantity;

    @Column(name = "quantity_sold")
    private int quantitySold;

    @Column(name = "quantity_count")
    private int quantityCount;

//    @OneToMany(mappedBy = "game")
//    private List<Orders> orders;
}

