package gp.riham_aisha.back_end.service_implementation;

import gp.riham_aisha.back_end.model.product_and_configuration.Product;
import gp.riham_aisha.back_end.model.User;
import gp.riham_aisha.back_end.repository.UserRepository;
import gp.riham_aisha.back_end.service.UserService;
import gp.riham_aisha.back_end.util.AuthUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    @Override
    public User getUser(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User not found"));
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
        log.info("User with id: {} has been deleted by: {}", id, AuthUtil.getCurrentUser());
    }

    @Override
    public void resetPassword(Long id, String newPassword) {
        User user = userRepository.findById(id).orElseThrow();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        log.info("Password for user with id: {} has been reset by: {}", id, AuthUtil.getCurrentUser());
    }

    @Override
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(() -> new EntityNotFoundException("User not found"));
    }

    @Override
    public List<Product> getWishlist(String username) {
        return getUserByUsername(username).getWishlist();
    }

    @Override
    public void addProductToWishlist(String username, Product product) {
        User user = getUserByUsername(username);
        user.addProductToWishlist(product);
        userRepository.save(user);
    }

    @Override
    public void removeProductFromWishlist(String username, Long productId) {
        User user = getUserByUsername(username);
        user.removeProductFromWishlist(productId);
        userRepository.save(user);
    }

    @Override
    public void clearWishlist(String username) {
        User user = getUserByUsername(username);
        user.clearWishlist();
        userRepository.save(user);
    }

    @Override
    public boolean isProductInWishlist(String username, Long productId) {
        return getUserByUsername(username).isProductInWishlist(productId);
    }
}
