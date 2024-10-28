package poly.gamemarketplacebackend.core.util;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;
import poly.gamemarketplacebackend.core.entity.CategoryDetail;
import poly.gamemarketplacebackend.core.entity.Game;

public class GameSpecification {
    public static Specification<Game> hasName(String name) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(criteriaBuilder.lower(root.get("gameName")), "%" + name.toLowerCase() + "%");
    }

    public static Specification<Game> hasPriceBetween(Double minPrice, Double maxPrice) {
        return (root, query, criteriaBuilder) -> {
            // Nếu minPrice hoặc maxPrice là null thì trả về điều kiện đúng
            if (minPrice == null && maxPrice == null) {
                return criteriaBuilder.conjunction(); // Không có điều kiện nào được áp dụng
            }

            // Nếu chỉ có minPrice thì lọc theo minPrice
            if (minPrice != null && maxPrice == null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("price"), minPrice);
            }

            // Nếu chỉ có maxPrice thì lọc theo maxPrice
            if (maxPrice != null && minPrice == null) {
                return criteriaBuilder.lessThanOrEqualTo(root.get("price"), maxPrice);
            }

            // Nếu có cả minPrice và maxPrice thì sử dụng between
            return criteriaBuilder.between(root.get("price"), minPrice, maxPrice);
        };
    }


    public static Specification<Game> hasCategory(String categoryName) {
        return (root, query, criteriaBuilder) -> {
            root.fetch("categoryDetails", JoinType.INNER); // Tải trước categoryDetails
            Join<Game, CategoryDetail> categories = root.join("categoryDetails", JoinType.INNER);
            return criteriaBuilder.equal(categories.get("category").get("categoryName"), categoryName);
        };
    }

    public static Specification<Game> hasRatingBetween(String minRatingStr, String maxRatingStr) {
        return (root, query, criteriaBuilder) -> {
            Float minRating = null;
            Float maxRating = null;

            // Chuyển đổi giá trị từ String sang Double
            if (minRatingStr != null && !minRatingStr.isEmpty()) {
                try {
                    minRating = Float.parseFloat(minRatingStr);
                } catch (NumberFormatException e) {
                    System.out.println("Invalid minRating: " + minRatingStr);
                }
            }

            if (maxRatingStr != null && !maxRatingStr.isEmpty()) {
                try {
                    maxRating = Float.parseFloat(maxRatingStr);
                } catch (NumberFormatException e) {
                    System.out.println("Invalid maxRating: " + maxRatingStr);
                }
            }

            // Nếu minRating hoặc maxRating là null thì trả về điều kiện đúng
            if (minRating == null && maxRating == null) {
                return criteriaBuilder.conjunction();
            }

            // Nếu chỉ có minRating thì lọc theo minRating
            if (minRating != null && maxRating == null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("rating"), minRating);
            }

            // Nếu chỉ có maxRating thì lọc theo maxRating
            if (maxRating != null && minRating == null) {
                return criteriaBuilder.lessThanOrEqualTo(root.get("rating"), maxRating);
            }

            // Nếu có cả minRating và maxRating thì sử dụng between
            return criteriaBuilder.between(root.get("rating"), minRating, maxRating);
        };
    }

}
