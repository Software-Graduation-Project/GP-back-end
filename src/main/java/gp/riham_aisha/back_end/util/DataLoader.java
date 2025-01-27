package gp.riham_aisha.back_end.util;

import gp.riham_aisha.back_end.dto.AddStoreDto;
import gp.riham_aisha.back_end.dto.ProductCategoryDTO;
import gp.riham_aisha.back_end.dto.RegistrationRequest;
import gp.riham_aisha.back_end.dto.product.ProductManagementDto;
import gp.riham_aisha.back_end.enums.AttributeType;
import gp.riham_aisha.back_end.enums.Role;
import gp.riham_aisha.back_end.model.product_and_configuration.Choice;
import gp.riham_aisha.back_end.model.product_and_configuration.ConfigurationAttributes;
import gp.riham_aisha.back_end.model.product_and_configuration.Product;
import gp.riham_aisha.back_end.model.product_and_configuration.ProductConfiguration;
import gp.riham_aisha.back_end.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Set;

@Slf4j
@Configuration
public class DataLoader {
    @Bean
    CommandLineRunner initDB(AdminService adminService, CategoryService categoryService, AuthenticationService authenticationService, StoreService storeService, ProductService productService) {
        return args -> {
            log.info("---------- The application has started on port 1218 ----------");
            addUsers(adminService, authenticationService);
            addStoreCategories(categoryService);
            addStores(storeService);
            addProductCategories(categoryService);
            addProducts(productService);
        };
    }

    private void addUsers(AdminService adminService, AuthenticationService authenticationService) {
        String password = "pass123456";
        // add main admin
        adminService.addNewAdmin(new RegistrationRequest("rihamkatout", "Riham", "Katout", "rihamkatm@gmail.com", password, "0599119482"));
        // add users
        authenticationService.register(new RegistrationRequest("siwar_katout", "Siwar", "Katout", "siwar@gp.com", password, "0987654321"), Set.of(Role.CUSTOMER));
        authenticationService.register(new RegistrationRequest("reem_ishtayeh", "Reem", "Ishtayeh", "reem@gp.com", password, "0987764321"), Set.of(Role.CUSTOMER));

    }

    private void addStoreCategories(CategoryService categoryService) {
        categoryService.addNewStoreCategory("General", null);
        categoryService.addNewStoreCategory("Sweets", "https://drive.google.com/thumbnail?id=1eUBDdHDLWdXbU7mTpF0Y7VvBcsnEEDL8");
        categoryService.addNewStoreCategory("Jewelry", "https://drive.google.com/thumbnail?id=1NHs5LnU9AINY08IPaVAPBO2Exb8cHqrc");
        categoryService.addNewStoreCategory("Toys", "https://drive.google.com/thumbnail?id=1e6WlsCPcCctzMkpPqU6lZxL5ZZIirTzx");
        categoryService.addNewStoreCategory("Home Decor", "https://drive.google.com/thumbnail?id=1KA8EF6JMU9ft42r6akR6DTZTgWPjUDnS");
    }

    private void addStores(StoreService storeService) {
        storeService.addNewStore(new AddStoreDto("Siwar Store", "Siwar is the most beautiful girl in the world", null, null, 1L, 4L));
        storeService.addNewStore(new AddStoreDto("Riham Store", "bla bla bla bla", null, null, 1L, 5L));
        storeService.addNewStore(new AddStoreDto("Sweet Touches", "They have the best cookies ever", "https://drive.google.com/thumbnail?id=1dZkmsrJjKo8k5D6pAKtrHmiJE60AGjQo", null, 1L, 2L));
    }

    private void addProductCategories(CategoryService categoryService) {
        categoryService.addNewProductCategory(new ProductCategoryDTO(null, "Cake", 2L, "https://drive.google.com/thumbnail?id=1gMy5DYxIIIB5QSX0y4HTRwb-sL55INpc"));
        categoryService.addNewProductCategory(new ProductCategoryDTO(null, "Cookies", 2L, "https://drive.google.com/thumbnail?id=1UAqkCZeAj7rplnrs3AI3DfmJgm_D6kk7"));
        categoryService.addNewProductCategory(new ProductCategoryDTO(null, "Chocolate", 2L, "https://drive.google.com/thumbnail?id=1m60ogeEJRYfQUf-c5UmVA5iM6_ATMHcH"));
        categoryService.addNewProductCategory(new ProductCategoryDTO(null, "Macarons", 2L, "https://drive.google.com/thumbnail?id=13xEgjolFzPAZ-r34yZsGnHbPi5veN4Gh"));
        categoryService.addNewProductCategory(new ProductCategoryDTO(null, "Ring", 3L, null));
        categoryService.addNewProductCategory(new ProductCategoryDTO(null, "Necklace", 3L, null));
        categoryService.addNewProductCategory(new ProductCategoryDTO(null, "Earring", 3L, null));
        categoryService.addNewProductCategory(new ProductCategoryDTO(null, "Watches", 3L, null));
        categoryService.addNewProductCategory(new ProductCategoryDTO(null, "Dolls", 4L, null));
        categoryService.addNewProductCategory(new ProductCategoryDTO(null, "Plush Toys", 4L, null));
        categoryService.addNewProductCategory(new ProductCategoryDTO(null, "Wall Art", 5L, null));
        categoryService.addNewProductCategory(new ProductCategoryDTO(null, "Vases", 5L, null));
        categoryService.addNewProductCategory(new ProductCategoryDTO(null, "Rugs", 5L, null));
        categoryService.addNewProductCategory(new ProductCategoryDTO(null, "Candles", 5L, null));
        categoryService.addNewProductCategory(new ProductCategoryDTO(null, "Mirrors", 5L, null));
        categoryService.addNewProductCategory(new ProductCategoryDTO(null, "Clocks", 5L, null));
    }

    private void addProducts(ProductService productService) {
        // cake
        Product customizableCake = new Product(null, "3d cake", "Delicious cake, you can design it, choose topping and filling as you want!", "https://drive.google.com/thumbnail?id=1M4iDO8UnNY3dfdD8RvY7256H7V8it5sB", 15.0, 0, 0, false, true, null, true, false, null, 0, null, null, null);

        ConfigurationAttributes toppingAttributes = new ConfigurationAttributes(null, "Color", AttributeType.COLOR, List.of(new Choice("Red", 0.0), new Choice("Blue", 1.0), new Choice("Green", 12.0)), null);
        ConfigurationAttributes toppingFlavor = new ConfigurationAttributes(null, "Flavor", AttributeType.OTHER, List.of(new Choice("Frosting", 2.0), new Choice("Fruit", 1.0), new Choice("Nuts", 12.0), new Choice("Chocolate ", 2.5)), null);
        ConfigurationAttributes cakeAttributes = new ConfigurationAttributes(null, "Cake size", AttributeType.SIZE, List.of(new Choice("S", 0.0), new Choice("M", 4.0), new Choice("L", 7.0)), null);
        ProductConfiguration cakeTopping = new ProductConfiguration(null, "Topping", false, 0.0, null, List.of(toppingAttributes, toppingFlavor));
        ProductConfiguration cakeFilling = new ProductConfiguration(null, "Filling", false, 0.0, null, List.of(toppingAttributes));
        ProductConfiguration cakeSize = new ProductConfiguration(null, "Size", false, 0.0, null, List.of(cakeAttributes));
        productService.addProduct(new ProductManagementDto(customizableCake, 3L, List.of(cakeTopping, cakeFilling, cakeSize), 1L));

        // flower pot
        Product customizableFlowerPot = new Product(null, "Flower Pot", "Beautiful flower pot, you can customize the color, size, and material to suit your needs!", "https://drive.google.com/thumbnail?id=17kU1y14U9miNE2KLq3iA6R4mLthDP_RQ", 20.0, 3, 5, true, true, null, false, false, null, 0, null, null, null);
        // pot
        ConfigurationAttributes potColorAttributes = new ConfigurationAttributes(null, "Color", AttributeType.COLOR, List.of(new Choice("White", 0.0), new Choice("Black", 1.5), new Choice("Green", 2.0), new Choice("Blue", 2.5)), null);
        ConfigurationAttributes potSizeAttributes = new ConfigurationAttributes(null, "Size", AttributeType.SIZE, List.of(new Choice("Small", 0.0), new Choice("Medium", 3.0), new Choice("Large", 5.0)), null);
        ConfigurationAttributes potMaterialAttributes = new ConfigurationAttributes(null, "Material", AttributeType.OTHER, List.of(new Choice("Ceramic", 0.0), new Choice("Plastic", 1.0), new Choice("Clay", 2.0), new Choice("Metal", 3.0)), null);
        ProductConfiguration potConfigurations = new ProductConfiguration(null, "Pot Options", false, 0.0, null, List.of(potColorAttributes, potSizeAttributes, potMaterialAttributes));
        // flower
        ConfigurationAttributes flowerColorAttributes = new ConfigurationAttributes(null, "Color", AttributeType.COLOR, List.of(new Choice("White", 0.0), new Choice("Pink", 1.5), new Choice("Green", 2.0), new Choice("Yellow", 2.5)), null);
        ConfigurationAttributes flowerTypeAttributes = new ConfigurationAttributes(null, "Type", AttributeType.OTHER, List.of(new Choice("Peonies", 0.0), new Choice("Rose", 1.0), new Choice("Alstroemeria", 2.0), new Choice("Chrysanthemum", 3.0)), null);
        ProductConfiguration flowerConfigurations = new ProductConfiguration(null, "Flower Options", true, 2.0, null, List.of(flowerColorAttributes, flowerTypeAttributes));
        productService.addProduct(new ProductManagementDto(customizableFlowerPot, 2L, List.of(potConfigurations, flowerConfigurations), 12L));

//            // cookies
//            productService.addProduct(new ProductDto("Classic cookies", "The best cookies ever", 1.0, 100,
//                    "https://drive.google.com/thumbnail?id=1LtKw1BnmKyf6oG-sabBHydIIewVJmHcM",
//                    true, false, null, 2L, 50, 3L, List.of("red", "brown", "#caa179"), Map.of(Size.S, 1.0, Size.M, 1.5, Size.L, 2.0)));
//            productService.addProduct(new ProductDto("Classic cookies not available", "The best cookies ever", 2.0, 100,
//                    "https://drive.google.com/thumbnail?id=1LtKw1BnmKyf6oG-sabBHydIIewVJmHcM",
//                    false, false, null, 2L, 120, 3L, null, Map.of(Size.S, 1.0, Size.M, 1.5, Size.L, 2.0)));
//            productService.addProduct(new ProductDto("Classic cookies with 3d model customizable", "The best cookies ever", 3.0, 100,
//                    "https://drive.google.com/thumbnail?id=1LtKw1BnmKyf6oG-sabBHydIIewVJmHcM",
//                    true, true, "https://drive.google.com/thumbnail?id=1BK2xFWIPilz8qoY5OXvyiI2j0pYv3d9L", 2L, 120, 3L, null, Map.of(Size.S, 1.0, Size.M, 1.5, Size.L, 2.0)));

        // chocolate
//            productService.addProduct(new ProductDto("Vanilla Chocolate", "Creamy vanilla chocolate", 5.0, 200,
//                    "https://drive.google.com/thumbnail?id=1BK2xFWIPilz8qoY5OXvyiI2j0pYv3d9L",
//                    true, false, null, 3L, 100, 3L, List.of("white"), Map.of(Size.S, 5.0, Size.M, 7.0, Size.L, 10.0)));
//
//            // dolls
//            productService.addProduct(new ProductDto("Doll", "Beautiful doll", 10.0, 150,
//                    "https://drive.google.com/thumbnail?id=1BK2xFWIPilz8qoY5OXvyiI2j0pYv3d9L",
//                    true, true, null, 9L, 100, 1L, List.of("pink", "blue", "yellow", "green"), null));
//
//            productService.addProduct(new ProductDto("Plush Toy", "Beautiful plush toy", 6.0, 50,
//                    "https://drive.google.com/thumbnail?id=1BK2xFWIPilz8qoY5OXvyiI2j0pYv3d9L",
//                    true, false, null, 10L, 60, 1L, List.of("pink"), Map.of(Size.S, 6.0, Size.M, 8.0, Size.L, 10.0)));
//
//            // home decor
//            productService.addProduct(new ProductDto("Wall Art", "Beautiful art", 20.0, 50,
//                    "https://drive.google.com/thumbnail?id=1BK2xFWIPilz8qoY5OXvyiI2j0pYv3d9L",
//                    true, true, null, 11L, 50, 2L, List.of("pink", "blue", "yellow", "green", "brown"), null));
//
//            productService.addProduct(new ProductDto("Vase", "Beautiful pink vase", 20.0, 50,
//                    "https://drive.google.com/thumbnail?id=1BK2xFWIPilz8qoY5OXvyiI2j0pYv3d9L",
//                    true, true, null, 12L, 75, 2L, List.of("pink"), null));
    }
}
