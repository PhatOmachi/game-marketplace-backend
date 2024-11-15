package poly.gamemarketplacebackend.core.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "Category")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Category {
    @Id
    @Column(name = "sys_id_category")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer sysIdCategory;

    @Column(name = "category_name")
    private String categoryName;

    private String description;
}
