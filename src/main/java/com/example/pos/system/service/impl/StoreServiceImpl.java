package com.example.pos.system.service.impl;

import com.example.pos.system.domain.StoreStatus;
import com.example.pos.system.domain.UserRole;
import com.example.pos.system.exception.UserException;
import com.example.pos.system.mapper.StoreMapper;
import com.example.pos.system.mapper.UserMapper;
import com.example.pos.system.modal.Store;
import com.example.pos.system.modal.User;
import com.example.pos.system.payload.dto.StoreDTO;
import com.example.pos.system.payload.dto.UserDto;
import com.example.pos.system.repository.StoreRepository;
import com.example.pos.system.service.EmailService;
import com.example.pos.system.service.StoreService;
import com.example.pos.system.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StoreServiceImpl implements StoreService {

    private final StoreRepository storeRepository;
    private final UserService userService;
    private final EmailService emailService;

    @Override
    public StoreDTO createStore(StoreDTO dto, User currentUser) throws Exception {

        if (currentUser == null) {
            throw new UserException("User not authenticated");
        }

        if (!currentUser.getRole().name().equals("ROLE_SUPER_ADMIN")) {
            throw new UserException("Only Super Admin can create store");
        }

        Store store = new Store();

        store.setBrand(dto.getBrand());
        store.setDescription(dto.getDescription());
        store.setStoreType(dto.getStoreType());
        store.setContact(dto.getContact());

        Store savedStore = storeRepository.save(store);

        return StoreMapper.toDTO(savedStore);
    }

    @Override
    public StoreDTO getStoreById(Long id, User user) throws Exception {

        Store store = storeRepository.findById(id)
                .orElseThrow(() ->
                        new Exception("Store not found"));

        if (user.getRole() != UserRole.ROLE_SUPER_ADMIN) {

            if (user.getStore() == null ||
                    !user.getStore().getId().equals(id)) {

                throw new UserException("Access Denied");
            }
        }

        return StoreMapper.toDTO(store);
    }

    @Override
    public List<StoreDTO> getAllStores(User user) throws Exception {

        if (user.getRole() == UserRole.ROLE_SUPER_ADMIN
                || user.getRole() == UserRole.ROLE_INVENTORY_MANAGER
                || user.getRole() == UserRole.ROLE_ACCOUNTANT) {

            return storeRepository.findAll()
                    .stream()
                    .map(StoreMapper::toDTO)
                    .toList();
        }

        if (user.getStore() == null) {
            throw new UserException("Store not assigned");
        }

        return List.of(
                StoreMapper.toDTO(user.getStore())
        );
    }

    @Override
    public Store getStoreByAdmin() throws UserException {

        User currentUser = userService.getCurrentUser();

        if (currentUser.getStore() == null) {
            throw new UserException("No store assigned");
        }

        return currentUser.getStore();
    }

    @Override
    public StoreDTO updateStore(Long id, StoreDTO dto) throws Exception {

        User currentUser = userService.getCurrentUser();

        Store store = storeRepository.findById(id)
                .orElseThrow(() -> new Exception("Store not found"));

        if (!currentUser.getRole().name().equals("ROLE_SUPER_ADMIN")) {

            if (currentUser.getStore() == null ||
                    !currentUser.getStore().getId().equals(store.getId())) {

                throw new UserException("You cannot update this store");
            }
        }

        store.setBrand(dto.getBrand());
        store.setDescription(dto.getDescription());
        store.setStoreType(dto.getStoreType());
        store.setContact(dto.getContact());

        if (dto.getStatus() != null) {
            store.setStatus(dto.getStatus());
        }

        Store updated = storeRepository.save(store);

        return StoreMapper.toDTO(updated);
    }

    @Override
    public void deleteStore(Long id) throws UserException {

        User currentUser = userService.getCurrentUser();

        if (!currentUser.getRole().name().equals("ROLE_SUPER_ADMIN")) {
            throw new UserException("Only Super Admin can delete store");
        }

        Store store = storeRepository.findById(id)
                .orElseThrow(() -> new UserException("Store not found"));

        if (!store.getUsers().isEmpty()) {
            throw new UserException(
                    "Cannot delete store because users are assigned to it."
            );
        }

        storeRepository.delete(store);
    }

    @Override
    public StoreDTO getStoreByEmployee() throws UserException {

        User currentUser = userService.getCurrentUser();

        if (currentUser.getStore() == null) {
            throw new UserException("No store assigned");
        }

        return StoreMapper.toDTO(currentUser.getStore());
    }

    @Override
    public StoreDTO moderateStore(Long id, StoreStatus status) throws Exception {

        Store store = storeRepository.findById(id)
                .orElseThrow(() -> new Exception("Store not found"));

        StoreStatus previousStatus = store.getStatus();

        store.setStatus(status);

        Store updatedStore = storeRepository.save(store);

        if (previousStatus != status) {
            notifyStoreUsers(updatedStore, status);
        }

        return StoreMapper.toDTO(updatedStore);
    }

    private void notifyStoreUsers(Store store, StoreStatus newStatus) {

        List<User> storeUsers = store.getUsers();

        List<User> recipients = storeUsers == null ? new java.util.ArrayList<>() :
                storeUsers.stream()
                        .filter(u -> u.getRole() == UserRole.ROLE_STORE_ADMIN)
                        .collect(Collectors.toList());

        if (recipients.isEmpty()) {
            return;
        }

        String subject;
        String bodyTemplate;

        switch (newStatus) {
            case ACTIVE -> {
                subject = "Your Store Has Been Approved";
                bodyTemplate = "Hello %s,\n\n"
                        + "Good news! Your store \"" + store.getBrand() + "\" has been approved by the Super Admin.\n"
                        + "You can now log in and start using NexoraPOS.\n\n"
                        + "Thank you.";
            }
            case BLOCKED -> {
                subject = "Your Store Access Has Been Blocked";
                bodyTemplate = "Hello %s,\n\n"
                        + "Your store \"" + store.getBrand() + "\" access has been blocked by the Super Admin.\n"
                        + "Please contact support if you have any questions.\n\n"
                        + "Thank you.";
            }
            case PENDING -> {
                subject = "Your Store Status Is Pending Review";
                bodyTemplate = "Hello %s,\n\n"
                        + "Your store \"" + store.getBrand() + "\" is currently under review by the Super Admin.\n\n"
                        + "Thank you.";
            }
            default -> {
                return;
            }
        }

        for (User user : recipients) {
            String body = String.format(bodyTemplate, user.getFullName());
            emailService.sendEmail(user.getEmail(), subject, body);
        }
    }

    @Override
    public StoreDTO getStoreByUser(User user) throws Exception {

        if (user.getStore() == null) {
            throw new Exception("Store not assigned");
        }

        return StoreMapper.toDTO(user.getStore());
    }

    @Override
    public List<UserDto> getStoreAdminsContact(Long storeId, User user) throws Exception {

        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new Exception("Store not found"));


        if (user.getRole() != UserRole.ROLE_SUPER_ADMIN) {

            if (user.getStore() == null ||
                    !user.getStore().getId().equals(storeId)) {

                throw new UserException("Access Denied");
            }
        }

        return store.getUsers().stream()
                .filter(u -> u.getRole() == UserRole.ROLE_STORE_ADMIN)
                .map(UserMapper::toDTO)
                .collect(Collectors.toList());
    }
}