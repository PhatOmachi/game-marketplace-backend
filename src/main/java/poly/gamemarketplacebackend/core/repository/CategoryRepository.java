package poly.gamemarketplacebackend.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import poly.gamemarketplacebackend.core.entity.Category;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {

    @Query("SELECT c FROM Category c order by c.sysIdCategory asc ")
    List<Category> findAll();

    Category save(Category category);

    void delete(Category category);

    Category findById(int id);
}
