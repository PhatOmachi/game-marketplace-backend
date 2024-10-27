package poly.gamemarketplacebackend.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import poly.gamemarketplacebackend.core.entity.Category;
import poly.gamemarketplacebackend.core.entity.CategoryDetail;

public interface CategoryDetailRepository extends JpaRepository<CategoryDetail, Integer> {
    CategoryDetail save(CategoryDetail categoryDetail);
}
