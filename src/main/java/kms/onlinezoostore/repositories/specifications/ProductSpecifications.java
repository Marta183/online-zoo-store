package kms.onlinezoostore.repositories.specifications;

import kms.onlinezoostore.entities.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.MultiValueMap;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import java.util.List;

public class ProductSpecifications {

    private static Specification<Product> nameLike(String namePart) {
        return (root, criteriaQuery, criteriaBuilder)
                -> criteriaBuilder.like(root.get("name"), "%" + namePart + "%");
    }

    private static Specification<Product> nameStartingWith(String nameStartingWith) {
        return (root, criteriaQuery, criteriaBuilder)
                -> criteriaBuilder.like(root.get("name"), nameStartingWith + "%");
    }

    private static Specification<Product> priceGreaterOrEquals(String minPrice) {
        return (root, criteriaQuery, criteriaBuilder)
                -> criteriaBuilder.greaterThanOrEqualTo(root.get("price"), minPrice);
    }

    private static Specification<Product> priceLessOrEquals(String maxPrice) {
        return (root, criteriaQuery, criteriaBuilder)
                -> criteriaBuilder.lessThanOrEqualTo(root.get("price"), maxPrice);
    }

    private static Specification<Product> isNewArrival(String newArrival) {
        return (root, criteriaQuery, criteriaBuilder)
                -> criteriaBuilder.equal(root.get("newArrival"), newArrival);
    }

    private static Specification<Product> isNotAvailable(String notAvailable) {
        return (root, criteriaQuery, criteriaBuilder)
                -> criteriaBuilder.equal(root.get("notAvailable"), notAvailable);
    }

    private static Specification<Product> hasCategory(String categoryId) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            Join<Product, ProductCategory> categoryJoin = root.join("category", JoinType.INNER);
            return criteriaBuilder.equal(categoryJoin.get("id"), categoryId);
        };
    }

    private static Specification<Product> hasBrand(List<String> brandIds) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            Join<Product, Brand> brandJoin = root.join("brand", JoinType.INNER);
            return brandJoin.get("id").in(brandIds);
        };
    }

    private static Specification<Product> hasMaterial(List<String> materialIds) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            Join<Product, Material> materialJoin = root.join("material", JoinType.INNER);
            return materialJoin.get("id").in(materialIds);
        };
    }

    private static Specification<Product> hasColor(List<String> colorIds) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            Join<Product, Color> colorJoin = root.join("color", JoinType.INNER);
            return colorJoin.get("id").in(colorIds);
        };
    }

    private static Specification<Product> hasWeight(List<String> weightIds) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            Join<Product, Weight> weightJoin = root.join("weight", JoinType.INNER);
            return weightJoin.get("id").in(weightIds);
        };
    }

    private static Specification<Product> hasSize(List<String> sizeIds) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            Join<Product, ProductSize> sizeJoin = root.join("productSize", JoinType.INNER);
            return sizeJoin.get("id").in(sizeIds);
        };
    }

    private static Specification<Product> hasAge(List<String> ageIds) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            Join<Product, Age> ageJoin = root.join("age", JoinType.INNER);
            return ageJoin.get("id").in(ageIds);
        };
    }

    // only valid params
    public static Specification<Product> build(MultiValueMap<String, String> params) {
        Specification<Product> spec = Specification.where(null);

        if (params.containsKey("name_any_part")) {
            spec.and(ProductSpecifications.nameLike(params.getFirst("name_any_part")));
        }

        if (params.containsKey("name_starting_with")) {
            spec.and(ProductSpecifications.nameStartingWith(params.getFirst("name_starting_with")));
        }

        if (params.containsKey("min_price")) {
            spec.and(ProductSpecifications.priceGreaterOrEquals(params.getFirst("min_price")));
        }

        if (params.containsKey("max_price")) {
            spec.and(ProductSpecifications.priceLessOrEquals(params.getFirst("max_price")));
        }

        if (params.containsKey("new_arrival")) {
            spec.and(ProductSpecifications.isNewArrival(params.getFirst("new_arrival")));
        }
        if (params.containsKey("not_available")) {
            spec.and(ProductSpecifications.isNotAvailable(params.getFirst("not_available")));
        }

        if (params.containsKey("category_id")) {
            spec.and(ProductSpecifications.hasCategory(params.getFirst("category_id")));
        }

        if (params.containsKey("brand_ids")) {
            spec.and(ProductSpecifications.hasBrand(params.get("brand_ids")));
        }

        if (params.containsKey("color_ids")) {
            spec.and(ProductSpecifications.hasColor(params.get("color_ids")));
        }

        if (params.containsKey("material_ids")) {
            spec.and(ProductSpecifications.hasMaterial(params.get("material_ids")));
        }

        if (params.containsKey("weight_ids")) {
            spec.and(ProductSpecifications.hasWeight(params.get("weight_ids")));
        }

        if (params.containsKey("size_ids")) {
            spec.and(ProductSpecifications.hasSize(params.get("size_ids")));
        }

        if (params.containsKey("age_ids")) {
            spec.and(ProductSpecifications.hasAge(params.get("age_ids")));
        }

        return spec;
    }
}
