package gp.riham_aisha.back_end.service_implementation;

import gp.riham_aisha.back_end.dto.StoreDto;
import gp.riham_aisha.back_end.enums.Role;
import gp.riham_aisha.back_end.enums.StoreStatus;
import gp.riham_aisha.back_end.model.Store;
import gp.riham_aisha.back_end.model.StoreCategory;
import gp.riham_aisha.back_end.model.User;
import gp.riham_aisha.back_end.repository.StoreRepository;
import gp.riham_aisha.back_end.service.CategoryService;
import gp.riham_aisha.back_end.service.StoreService;
import gp.riham_aisha.back_end.service.UserService;
import gp.riham_aisha.back_end.util.AuthUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class StoreServiceImpl implements StoreService {
    private final StoreRepository storeRepository;
    private final CategoryService storeCategoryService;
    private final UserService userService;

    private static final String IS_NOT_FOUND = " is not found";
    private static final String NOT_FOUND = "Store with id: %d" + IS_NOT_FOUND;

    @Transactional
    @Override
    public Store addNewStore(StoreDto storeDto) {
        Long managerId = storeDto.managerId();
        User manager = userService.getUser(managerId).orElseThrow(
                () -> new EntityNotFoundException("User with id: " + managerId + IS_NOT_FOUND));
        manager.addRole(Role.STORE_MANAGER);
        Long categoryId = storeDto.categoryId();
        StoreCategory category = storeCategoryService.getStoreCategory(categoryId).orElseThrow(
                () -> new EntityNotFoundException("Store category with id: " + categoryId + IS_NOT_FOUND));

        Store store = new Store(storeDto, category, manager);
        storeRepository.save(store);
        manager.addStore();
        log.info("Store with id: {} is added successfully by: {}", store.getId(), AuthUtil.getCurrentUser());
        return store;
    }

    @Override
    public Store updateStore(Long id, StoreDto storeDto) {
        Store store = getStore(id);
        validateOwner(store);
        store.setName(storeDto.name());
        store.setDescription(storeDto.description());
        store.setLogoURL(storeDto.logoURL());
        store.setCoverURL(storeDto.coverURL());
        store.setStoreCategory(storeCategoryService.getStoreCategory(storeDto.categoryId()).orElseThrow(
                () -> new EntityNotFoundException("Store category with id: " + storeDto.categoryId() + IS_NOT_FOUND)));
        storeRepository.save(store);
        log.info("Store with id: {} is updated successfully by: {}", store.getId(), AuthUtil.getCurrentUser());
        return store;
    }

    @Override
    public void banStore(Long id) {
        Store store = getStore(id);
        store.setStatus(StoreStatus.BANNED);
        storeRepository.save(store);
        log.info("Store with id: {} is banned successfully by: {}", store.getId(), AuthUtil.getCurrentUser());
    }

    @Override
    public void unbanStore(Long id) {
        Store store = getStore(id);
        if(store.getStatus() != StoreStatus.BANNED) {
            throw new IllegalStateException("Store is not banned");
        }
        store.setStatus(StoreStatus.ACTIVE);
        storeRepository.save(store);
        log.info("Store with id: {} is unbanned successfully by: {}", store.getId(), AuthUtil.getCurrentUser());
    }

    @Override
    public void activateStore(Long id) {
        Store store = getStore(id);
        if(store.getStatus() != StoreStatus.INACTIVE) {
            throw new IllegalStateException("Store is not inactive");
        }
        validateOwner(store);
        store.setStatus(StoreStatus.ACTIVE);
        storeRepository.save(store);
        log.info("Store with id: {} is activated successfully by: {}", store.getId(), AuthUtil.getCurrentUser());
    }

    @Override
    public void deactivateStore(Long id) {
        Store store = getStore(id);
        if(store.getStatus() != StoreStatus.ACTIVE) {
            throw new IllegalStateException("Store is not active");
        }
        validateOwner(store);
        store.setStatus(StoreStatus.INACTIVE);
        storeRepository.save(store);
        log.info("Store with id: {} is deactivated successfully by: {}", store.getId(), AuthUtil.getCurrentUser());
    }

    @Override
    public void deleteStore(Long id) {
        User manager = validateOwner(getStore(id));
        manager.removeStore();
        storeRepository.deleteById(id);
        log.info("Store with id: {} is deleted successfully by: {}", id, AuthUtil.getCurrentUser());
    }

    @Override
    public Store getStore(Long id) {
        return storeRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.format(NOT_FOUND, id))
        );
    }

    private User validateOwner(Store store) {
        User manager = store.getManager();
        // check if the current user is the manager of the store

        if (AuthUtil.isCurrentUser(manager.getUsername())) {
            throw new SecurityException("You are not authorized to update this store information");
        }
        return manager;
    }
}
