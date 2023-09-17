package kms.onlinezoostore.repositories.specifications;

import kms.onlinezoostore.entities.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.MultiValueMap;

import java.util.List;

public class ProductSpecifications {

    private static Specification<Product> nameLike(String namePart) {
        return (root, criteriaQuery, criteriaBuilder)
                -> criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + namePart.trim().toLowerCase() + "%");
    }

    private static Specification<Product> nameStartingWith(String nameStartingWith) {
        return (root, criteriaQuery, criteriaBuilder)
                -> criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), nameStartingWith.toLowerCase() + "%");
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

    private static Specification<Product> priceWithDiscount() {
        return (root, criteriaQuery, criteriaBuilder)
                -> criteriaBuilder.greaterThan(root.get("priceWithDiscount"), 0);
    }

    private static Specification<Product> isNewArrival(String newArrival) {
        return (root, criteriaQuery, criteriaBuilder)
                -> criteriaBuilder.equal(root.get("newArrival"), Boolean.parseBoolean(newArrival));
    }

    private static Specification<Product> isNotAvailable(String notAvailable) {
        return (root, criteriaQuery, criteriaBuilder)
                -> criteriaBuilder.equal(root.get("notAvailable"), Boolean.parseBoolean(notAvailable));
    }

    private static Specification<Product> hasCategory(List<String> categoryIds) {
        return (root, criteriaQuery, criteriaBuilder)
                -> criteriaBuilder.in(root.get("category").get("id")).value(categoryIds);
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

    private static Specification<Product> hasPrescriptions(List<String> prescriptionIds) {
        return (root, criteriaQuery, criteriaBuilder)
                -> criteriaBuilder.in(root.get("prescription").get("id")).value(prescriptionIds);
    }

    private static Specification<Product> init() {
        return (root, criteriaQuery, criteriaBuilder)
                -> criteriaBuilder.isTrue(criteriaBuilder.literal(true));
    }

    public static Specification<Product> build(MultiValueMap<String, String> params) {
        if (params == null || params.size() == 0)
            return null;

        Specification<Product> spec = Specification.where(ProductSpecifications.init());

        if (params.containsKey("nameLike")) {
            spec = Specification.where(spec).and(ProductSpecifications.nameLike(params.getFirst("nameLike")));
        }

        if (params.containsKey("nameStartsWith")) {
            spec = Specification.where(spec).and(ProductSpecifications.nameStartingWith(params.getFirst("nameStartsWith")));
        }

        if (params.containsKey("minPrice") && params.containsKey("maxPrice")) {
            spec = Specification.where(spec).and(ProductSpecifications.priceBetween(params.getFirst("minPrice"), params.getFirst("maxPrice")));
        } else if (params.containsKey("minPrice")) {
            spec = Specification.where(spec).and(ProductSpecifications.priceGreaterOrEquals(params.getFirst("minPrice")));
        } else if (params.containsKey("maxPrice")) {
            spec = Specification.where(spec).and(ProductSpecifications.priceLessOrEquals(params.getFirst("maxPrice")));
        }

        if (params.containsKey("onSale") && params.getFirst("onSale").equalsIgnoreCase("true")) {
            spec = Specification.where(spec).and(ProductSpecifications.priceWithDiscount());
        }

        if (params.containsKey("newArrival")) {
            spec = Specification.where(spec).and(ProductSpecifications.isNewArrival(params.getFirst("newArrival")));
        }
        if (params.containsKey("notAvailable")) {
            spec = Specification.where(spec).and(ProductSpecifications.isNotAvailable(params.getFirst("notAvailable")));
        }

        if (params.containsKey("categoryId")) {
            spec = Specification.where(spec).and(ProductSpecifications.hasCategory(params.get("categoryId")));
        }

        if (params.containsKey("brandId")) {
            spec = Specification.where(spec).and(ProductSpecifications.hasBrands(params.get("brandId")));
        }

        if (params.containsKey("colorId")) {
            spec = Specification.where(spec).and(ProductSpecifications.hasColors(params.get("colorId")));
        }

        if (params.containsKey("materialId")) {
            spec = Specification.where(spec).and(ProductSpecifications.hasMaterials(params.get("materialId")));
        }

        if (params.containsKey("weightId")) {
            spec = Specification.where(spec).and(ProductSpecifications.hasWeights(params.get("weightId")));
        }

        if (params.containsKey("sizeId")) {
            spec = Specification.where(spec).and(ProductSpecifications.hasSizes(params.get("sizeId")));
        }

        if (params.containsKey("ageId")) {
            spec = Specification.where(spec).and(ProductSpecifications.hasAges(params.get("ageId")));
        }

        if (params.containsKey("prescriptionId")) {
            spec = Specification.where(spec).and(ProductSpecifications.hasPrescriptions(params.get("prescriptionId")));
        }

        return spec;
    }


}
