package gp.riham_aisha.back_end.service.specification;

import gp.riham_aisha.back_end.model.product_and_configuration.Product;
import org.springframework.data.jpa.domain.Specification;

public class ProductSpecification {
    private ProductSpecification() {
    }

    public static Specification<Product> hasCategory(Long categoryId) {
        return (root, query, criteriaBuilder) -> categoryId == null ? null :
                criteriaBuilder.equal(root.get("productCategory").get("id"), categoryId);
    }

    public static Specification<Product> hasStore(Long storeId) {
        return (root, query, criteriaBuilder) -> storeId == null ? null :
                criteriaBuilder.equal(root.get("store").get("id"), storeId);
    }

    public static Specification<Product> hasStoreCategory(Long storeCategoryId) {
        return (root, query, criteriaBuilder) -> storeCategoryId == null ? null :
                criteriaBuilder.equal(root.get("store").get("storeCategory").get("id"), storeCategoryId);
    }

    public static Specification<Product> isAvailable(Boolean isAvailable) {
        return (root, query, criteriaBuilder) -> isAvailable == null ? null :
                criteriaBuilder.equal(root.get("isAvailable"), isAvailable);
    }

    public static Specification<Product> has3DModel(Boolean has3DModel) {
        return (root, query, criteriaBuilder) -> {
            if (has3DModel == null) {
                return null;
            }
            return has3DModel ? criteriaBuilder.isNotNull(root.get("model3dURL")) : criteriaBuilder.isNull(root.get("model3dURL"));
        };
    }

    public static Specification<Product> is3dCustomizable(Boolean is3dCustomizable) {
        return (root, query, criteriaBuilder) -> is3dCustomizable == null ? null :
                criteriaBuilder.equal(root.get("is3dCustomizable"), is3dCustomizable);
    }

    public static Specification<Product> hasMinPrice(Double price) {
        return (root, query, criteriaBuilder) -> price == null ? null :
                criteriaBuilder.greaterThanOrEqualTo(root.get("basePrice"), price);
    }

    public static Specification<Product> hasMaxPrice(Double price) {
        return (root, query, criteriaBuilder) -> price == null ? null :
                criteriaBuilder.lessThanOrEqualTo(root.get("basePrice"), price);
    }

    public static Specification<Product> hasMinRating(Double rating) {
        return (root, query, criteriaBuilder) -> rating == null ? null :
                criteriaBuilder.greaterThanOrEqualTo(root.get("rating"), rating);
    }

    public static Specification<Product> hasId(Long id) {
        return (root, query, criteriaBuilder) -> id == null ? null :
                criteriaBuilder.equal(root.get("id"), id);
    }

    public static Specification<Product> isCustomizable(Boolean customizable) {
        return (root, query, criteriaBuilder) -> customizable == null ? null :
                criteriaBuilder.notEqual(root.get("defaultFeatures"), customizable);
    }

    public static Specification<Product> hasKeyWord(String keyWord) {
        return (root, query, criteriaBuilder) -> keyWord == null ? null :
                criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + keyWord.toLowerCase() + "%"),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), "%" + keyWord.toLowerCase() + "%")
                );
    }

    public static Specification<Product> hasOffer(Long offerId){
        return (root, query, criteriaBuilder) -> offerId == null ? null :
                criteriaBuilder.equal(root.get("offer").get("id"), offerId);
    }
}
