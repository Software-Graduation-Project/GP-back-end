package gp.riham_aisha.back_end.service_implementation;

import gp.riham_aisha.back_end.dto.CloudinaryResponse;
import gp.riham_aisha.back_end.dto.product.ProductDetailsDto;
import gp.riham_aisha.back_end.dto.product.ProductManagementDto;
import gp.riham_aisha.back_end.dto.product.ProductWithStoreDto;
import gp.riham_aisha.back_end.dto.product.SearchProductParameters;
import gp.riham_aisha.back_end.model.Offer;
import gp.riham_aisha.back_end.model.ProductCategory;
import gp.riham_aisha.back_end.model.Review;
import gp.riham_aisha.back_end.model.Store;
import gp.riham_aisha.back_end.model.product_and_configuration.ConfigurationAttributes;
import gp.riham_aisha.back_end.model.product_and_configuration.Product;
import gp.riham_aisha.back_end.model.product_and_configuration.ProductConfiguration;
import gp.riham_aisha.back_end.repository.product_and_configuration.ConfigurationAttributesRepository;
import gp.riham_aisha.back_end.repository.product_and_configuration.ConfigurationRepository;
import gp.riham_aisha.back_end.repository.product_and_configuration.ProductRepository;
import gp.riham_aisha.back_end.service.*;
import gp.riham_aisha.back_end.service.specification.ProductSpecification;
import gp.riham_aisha.back_end.util.AuthUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    @PersistenceContext
    private EntityManager entityManager;

    private final ProductRepository productRepository;
    private final ConfigurationRepository configurationRepository;
    private final ConfigurationAttributesRepository configurationAttributesRepository;
    private final CloudinaryService cloudinaryService;
    private final StoreService storeService;
    private final CategoryService categoryService;
    private final UserService userService;

    private boolean isProductInWishlist(Long productId) {
        if (AuthUtil.getCurrentUser().equals("System")) return false;
        return userService.isProductInWishlist(AuthUtil.getCurrentUser(), productId);
    }

    @Override
    public Product getProductById(Long id) {
        return productRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Product with id: " + id + " not found")
        );
    }

    @Override
    public ProductDetailsDto getProductDetailsById(Long id) {
        return ProductDetailsDto.fromProductInfo(getProductById(id), isProductInWishlist(id));
    }


    @Override
    @Transactional
    public Long addProduct(ProductManagementDto productDto, MultipartFile... image) {
        // 1- validate store owner
        Store store = storeService.getStore(productDto.storeId());
        AuthUtil.validateStoreOwner(store);

        // 2- validate category
        ProductCategory category = categoryService.getProductCategory(productDto.categoryId());

        // 3- create product
        Product product = new Product(productDto, store, category);

        // 4- add image
        if (image != null && image.length > 0) {
            CloudinaryResponse response = cloudinaryService.uploadFile(image[0], String.valueOf(product.getId()));
            if (response != null) {
                product.setMainImageURL(response.getUrl());
            }
        }
        productRepository.save(product);

        // Add configurations
        productDto.configurations().forEach(configDto -> {
            ProductConfiguration configuration = new ProductConfiguration();
            configuration.setName(configDto.getName());
            configuration.setAllowsMultipleUnits(configDto.getAllowsMultipleUnits());
            configuration.setUnitPriceImpact(configDto.getUnitPriceImpact());
            configuration.setProduct(product);
            configurationRepository.save(configuration);

            // Add configuration attributes
            configDto.getConfigurationAttributes().forEach(attrDto -> {
                ConfigurationAttributes attribute = new ConfigurationAttributes();
                attribute.setName(attrDto.getName());
                attribute.setType(attrDto.getType());
                attribute.setChoices(attrDto.getChoices());
                attribute.setConfiguration(configuration);
                configurationAttributesRepository.save(attribute);
            });
        });
        entityManager.flush();
        entityManager.refresh(product);
        log.info("Product with id: {} was added by: {}", product.getId(), AuthUtil.getCurrentUser());
        return product.getId();
    }

    @Override
    @Transactional
    public ProductManagementDto updateProduct(Long id, ProductManagementDto productDto) {
        // 1- Get the existed product by id
        Product existedProduct = getProductById(id);
        if (productDto == null) {
            return ProductManagementDto.fromProduct(existedProduct);
        }
        // 2- Validate store owner
        Store store = storeService.getStore(productDto.storeId());
        AuthUtil.validateStoreOwner(store);

        // 3- Validate category
        ProductCategory category = categoryService.getProductCategory(productDto.categoryId());

        // 4- Update product with new data
        existedProduct.update(productDto, category);
        productRepository.save(existedProduct);

        // 5- Update configurations
        productDto.configurations().forEach(configDto -> {
            // Find existing configuration for this product
            ProductConfiguration existingConfig = existedProduct.getConfigurations().stream()
                    .filter(config -> config.getName().equals(configDto.getName()))
                    .findFirst()
                    .orElse(null);

            if (existingConfig != null) {
                // Update the existing configuration with new data
                existingConfig.setAllowsMultipleUnits(configDto.getAllowsMultipleUnits());
                existingConfig.setUnitPriceImpact(configDto.getUnitPriceImpact());
                configurationRepository.save(existingConfig);

                // Update configuration attributes
                configDto.getConfigurationAttributes().forEach(attrDto -> {
                    // Find existing configuration attribute or create new
                    ConfigurationAttributes existingAttr = existingConfig.getConfigurationAttributes().stream()
                            .filter(attr -> attr.getName().equals(attrDto.getName()))
                            .findFirst()
                            .orElse(null);

                    if (existingAttr != null) {
                        // Update existing attribute
                        existingAttr.setType(attrDto.getType());
                        existingAttr.setChoices(attrDto.getChoices());
                        configurationAttributesRepository.save(existingAttr);
                    } else {
                        // Create new attribute if it doesn't exist
                        ConfigurationAttributes attribute = new ConfigurationAttributes();
                        attribute.setName(attrDto.getName());
                        attribute.setType(attrDto.getType());
                        attribute.setChoices(attrDto.getChoices());
                        attribute.setConfiguration(existingConfig);
                        configurationAttributesRepository.save(attribute);
                    }
                });
            }
        });
        entityManager.flush();
        entityManager.refresh(existedProduct);
        log.info("Product with id: {} was updated by: {}", existedProduct.getId(), AuthUtil.getCurrentUser());
        return ProductManagementDto.fromProduct(existedProduct);
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        // 1- Get the product by ID
        Product product = getProductById(id);

        // 2- Validate store ownership
        AuthUtil.validateStoreOwner(product.getStore());

        // 3- Delete configurations and their attributes
        product.getConfigurations().forEach(config -> {
            // Delete configuration attributes first
            config.getConfigurationAttributes().forEach(configurationAttributesRepository::delete);

            // Delete the configuration itself
            configurationRepository.delete(config);
        });

        // 4- Delete the product
        productRepository.delete(product);

        // 5- Log the deletion action
        log.info("Product with id: {} was deleted by: {}", product.getId(), AuthUtil.getCurrentUser());
    }


    @Override
    public Page<ProductWithStoreDto> searchProducts(SearchProductParameters parameters, Pageable pageable) {
        Specification<Product> specification = Specification.where(ProductSpecification.hasKeyWord(parameters.keyWord()))
                .and(ProductSpecification.hasCategory(parameters.categoryId()))
                .and(ProductSpecification.hasStore(parameters.storeId()))
                .and(ProductSpecification.hasStoreCategory(parameters.storeCategoryId()))
                .and(ProductSpecification.isAvailable(parameters.isAvailable()))
                .and(ProductSpecification.has3DModel(parameters.threeDModel()))
                .and(ProductSpecification.is3dCustomizable(parameters.customizable()))
                .and(ProductSpecification.hasMinPrice(parameters.minPrice()))
                .and(ProductSpecification.hasMaxPrice(parameters.maxPrice()))
                .and(ProductSpecification.hasMinRating(parameters.minRating()))
                .and(ProductSpecification.hasId(parameters.id()))
                .and(ProductSpecification.isCustomizable(parameters.customizable()));

        Page<Product> productPage = productRepository.findAll(specification, pageable);
        return productPage.map(product ->
                ProductWithStoreDto.fromProductAndWishlist(product, isProductInWishlist(product.getId())));
    }


    @Override
    public List<Product> lowStockProducts(Long storeId) {
        return productRepository.findLowStockProductsByStoreNative(storeId);
    }

    @Override
    public void addProductToOffer(Long productId, Offer offer) {
        Product product = getProductById(productId);
        AuthUtil.validateStoreOwner(product.getStore());
        if (product.getOffer() != null) {
            throw new IllegalArgumentException("Product with id " + productId + " already has an offer");
        }
        product.setOffer(offer);
        productRepository.save(product);
    }

    @Override
    public void removeProductFromOffer(Long productId, Offer offer) {
        Product product = getProductById(productId);
        AuthUtil.validateStoreOwner(product.getStore());
        if (product.getOffer() == null || !product.getOffer().getId().equals(offer.getId())) {
            throw new IllegalArgumentException("Product with id " + productId + " doesn't have an offer");
        }
        product.setOffer(null);
        productRepository.save(product);
    }

}
