package poly.gamemarketplacebackend.core.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "Category")
@Data
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

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CategoryDetail> categoryDetails;
}
