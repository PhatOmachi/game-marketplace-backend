package poly.gamemarketplacebackend.core.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import poly.gamemarketplacebackend.core.constant.ResponseObject;
import poly.gamemarketplacebackend.core.dto.CategoryDTO;
import poly.gamemarketplacebackend.core.service.CategoryService;

@RestController
@Slf4j
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryAPI {
    private final CategoryService categoryService;

    @GetMapping
    public ResponseObject<?> getAllCategory() {
        return ResponseObject.builder()
                .status(HttpStatus.OK)
                .data(categoryService.getAllCategory())
                .build();
    }

    @PostMapping
    public ResponseObject<?> saveCategory(@RequestBody CategoryDTO categoryDTO) {
        return ResponseObject.builder()
                .status(HttpStatus.OK)
                .data(categoryService.saveCategory(categoryDTO))
                .build();
    }

    @DeleteMapping
    public ResponseObject<?> deleteCategory(@RequestBody CategoryDTO categoryDTO) {
        categoryService.deleteCategory(categoryDTO);
        return ResponseObject.builder()
                .status(HttpStatus.OK)
                .message("Delete category successfully")
                .build();
    }


}
