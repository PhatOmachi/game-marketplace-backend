package poly.gamemarketplacebackend.core.service;

import poly.gamemarketplacebackend.core.dto.CategoryDetailDTO;
import poly.gamemarketplacebackend.core.entity.CategoryDetail;

public interface CategoryDetailService {
    CategoryDetail saveCategoryDetail(CategoryDetailDTO categoryDetailDTO);

    void insertCategoryDetail(Integer sysIdCategory, Integer sysIdGame);

    int deleteCategoryDetailByGameId(Integer sysIdGame);
}
