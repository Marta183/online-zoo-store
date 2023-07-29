package kms.onlinezoostore.repositories.specifications;

import kms.onlinezoostore.entities.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.MultiValueMap;

import java.util.List;

public class ProductSpecifications {

    private static Specification<Product> nameLike(String namePart) {
        return (root, criteriaQuery, criteriaBuilder)
                -> criteriaBuilder.like(root.get("name"), "%" + namePart.trim().toLowerCase() + "%");
    }

    private static Specification<Product> prescriptionLike(String prescriptionPart) {
        return (root, criteriaQuery, criteriaBuilder)
                -> criteriaBuilder.like(root.get("prescription"), "%" + prescriptionPart.trim().toLowerCase() + "%");
    }

    private static Specification<Product> nameStartingWith(String nameStartingWith) {
        return (root, criteriaQuery, criteriaBuilder)
                -> criteriaBuilder.like(root.get("name"), nameStartingWith + "%");
    }

    private static Specification<Product> priceBetween(String minPrice, String maxPrice) {
        return (root, criteriaQuery, criteriaBuilder)
                -> criteriaBuilder.between(root.get("price"), minPrice, maxPrice);
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
                -> criteriaBuilder.equal(root.get("newArrival"), Boolean.parseBoolean(newArrival));
    }

    private static Specification<Product> isNotAvailable(String notAvailable) {
        return (root, criteriaQuery, criteriaBuilder)
                -> criteriaBuilder.equal(root.get("notAvailable"), Boolean.parseBoolean(notAvailable));
    }

    private static Specification<Product> hasCategory(String categoryId) {
        return (root, criteriaQuery, criteriaBuilder)
                -> criteriaBuilder.equal(root.get("category").get("id"), categoryId);
    }

    private static Specification<Product> hasBrands(List<String> brandIds) {
        return (root, criteriaQuery, criteriaBuilder)
                -> criteriaBuilder.in(root.get("brand").get("id")).value(brandIds);
    }

    private static Specification<Product> hasMaterials(List<String> materialIds) {
        return (root, criteriaQuery, criteriaBuilder)
                -> criteriaBuilder.in(root.get("material").get("id")).value(materialIds);
    }

    private static Specification<Product> hasColors(List<String> colorIds) {
        return (root, criteriaQuery, criteriaBuilder)
                -> criteriaBuilder.in(root.get("color").get("id")).value(colorIds);
    }

    private static Specification<Product> hasWeights(List<String> weightIds) {
        return (root, criteriaQuery, criteriaBuilder)
                -> criteriaBuilder.in(root.get("weight").get("id")).value(weightIds);
    }

    private static Specification<Product> hasSizes(List<String> sizeIds) {
        return (root, criteriaQuery, criteriaBuilder)
                -> criteriaBuilder.in(root.get("productSize").get("id")).value(sizeIds);
    }

    private static Specification<Product> hasAges(List<String> ageIds) {
        return (root, criteriaQuery, criteriaBuilder)
                -> criteriaBuilder.in(root.get("age").get("id")).value(ageIds);
    }

    private static Specification<Product> init() {
        return (root, criteriaQuery, criteriaBuilder)
                -> criteriaBuilder.isTrue(criteriaBuilder.literal(true));
    }

    public static Specification<Product> build(MultiValueMap<String, String> params) {
        if (params == null || params.size() == 0)
            return null;

        Specification<Product> spec = Specification.where(ProductSpecifications.init());

        if (params.containsKey("name_any_part")) {
            spec = Specification.where(spec).and(ProductSpecifications.nameLike(params.getFirst("name_any_part")));
        }

        if (params.containsKey("prescription_any_part")) {
            spec = Specification.where(spec).and(ProductSpecifications.prescriptionLike(params.getFirst("prescription_any_part")));
        }

        if (params.containsKey("name_starting_with")) {
            spec = Specification.where(spec).and(ProductSpecifications.nameStartingWith(params.getFirst("name_starting_with")));
        }

        if (params.containsKey("min_price") && params.containsKey("max_price")) {
            spec = Specification.where(spec).and(ProductSpecifications.priceBetween(params.getFirst("min_price"), params.getFirst("max_price")));
        } else if (params.containsKey("min_price")) {
            spec = Specification.where(spec).and(ProductSpecifications.priceGreaterOrEquals(params.getFirst("min_price")));
        } else if (params.containsKey("max_price")) {
            spec = Specification.where(spec).and(ProductSpecifications.priceLessOrEquals(params.getFirst("max_price")));
        }

        if (params.containsKey("new_arrival")) {
            spec = Specification.where(spec).and(ProductSpecifications.isNewArrival(params.getFirst("new_arrival")));
        }
        if (params.containsKey("not_available")) {
            spec = Specification.where(spec).and(ProductSpecifications.isNotAvailable(params.getFirst("not_available")));
        }

        if (params.containsKey("category_id")) {
            spec = Specification.where(spec).and(ProductSpecifications.hasCategory(params.getFirst("category_id")));
        }

        if (params.containsKey("brand_ids")) {
            spec = Specification.where(spec).and(ProductSpecifications.hasBrands(params.get("brand_ids")));
        }

        if (params.containsKey("color_ids")) {
            spec = Specification.where(spec).and(ProductSpecifications.hasColors(params.get("color_ids")));
        }

        if (params.containsKey("material_ids")) {
            spec = Specification.where(spec).and(ProductSpecifications.hasMaterials(params.get("material_ids")));
        }

        if (params.containsKey("weight_ids")) {
            spec = Specification.where(spec).and(ProductSpecifications.hasWeights(params.get("weight_ids")));
        }

        if (params.containsKey("size_ids")) {
            spec = Specification.where(spec).and(ProductSpecifications.hasSizes(params.get("size_ids")));
        }

        if (params.containsKey("age_ids")) {
            spec = Specification.where(spec).and(ProductSpecifications.hasAges(params.get("age_ids")));
        }

        return spec;
    }


}
