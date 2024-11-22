package poly.gamemarketplacebackend.core.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "Game")
@Getter
@Setter
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

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "is_active", columnDefinition = "BOOLEAN DEFAULT TRUE")
    private Boolean isActive;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "quantity_sold")
    private Integer quantitySold;

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CategoryDetail> categoryDetails;

    // additional fields
    private Float rating;
    private Integer ratingCount;
    private String features;
    private LocalDate releaseDate;
    private String developer;
    private String platform;
    private String language;
    private String about;

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Media> media;

//    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    @JsonIgnore
//    private List<Orders> orders;

}