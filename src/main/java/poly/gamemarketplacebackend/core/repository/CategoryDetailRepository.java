package poly.gamemarketplacebackend.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import poly.gamemarketplacebackend.core.entity.Category;
import poly.gamemarketplacebackend.core.entity.CategoryDetail;

public interface CategoryDetailRepository extends JpaRepository<CategoryDetail, Integer> {
    CategoryDetail save(CategoryDetail categoryDetail);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO Category_detail (sys_id_category, sys_id_game) VALUES (:sysIdCategory, :sysIdGame)", nativeQuery = true)
    void insertCategoryDetail(@Param("sysIdCategory") Integer sysIdCategory, @Param("sysIdGame") Integer sysIdGame);

    @Modifying
    @Transactional
    @Query("DELETE FROM CategoryDetail c WHERE c.game.sysIdGame = :sysIdGame")
    int deleteCategoryDetailByGameId(@Param("sysIdGame") Integer sysIdGame);
}
