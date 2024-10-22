package poly.gamemarketplacebackend.core.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Category")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Category {
    @Id
    @Column(name = "sys_id_category")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int sysIdCategory;
    @Column(name = "category_name")
    private String categoryName;
    private String description;
}
