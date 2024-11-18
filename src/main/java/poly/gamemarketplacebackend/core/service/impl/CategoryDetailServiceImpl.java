package poly.gamemarketplacebackend.core.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import poly.gamemarketplacebackend.core.dto.CategoryDetailDTO;
import poly.gamemarketplacebackend.core.entity.CategoryDetail;
import poly.gamemarketplacebackend.core.mapper.CategoryDetailMapper;
import poly.gamemarketplacebackend.core.mapper.CategoryMapper;
import poly.gamemarketplacebackend.core.repository.CategoryDetailRepository;
import poly.gamemarketplacebackend.core.repository.CategoryRepository;
import poly.gamemarketplacebackend.core.service.CategoryDetailService;

@Service
@RequiredArgsConstructor
public class CategoryDetailServiceImpl implements CategoryDetailService {
    private final CategoryDetailRepository categoryDetailRepository;
    private final CategoryDetailMapper categoryDetailMapper;

    @Override
    public CategoryDetail saveCategoryDetail(CategoryDetailDTO categoryDetailDTO) {
        return categoryDetailRepository.save(categoryDetailMapper.toEntity(categoryDetailDTO));
    }

    @Override
    public void insertCategoryDetail(Integer sysIdCategory, Integer sysIdGame) {
        categoryDetailRepository.insertCategoryDetail(sysIdCategory, sysIdGame);
    }

    @Override
    public int deleteCategoryDetailsByGameId(Integer sysIdGame) {
        return categoryDetailRepository.deleteCategoryDetailsByGameId(sysIdGame);
    }

}
