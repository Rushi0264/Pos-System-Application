package com.example.pos.system.service.impl;

import com.example.pos.system.domain.UserRole;
import com.example.pos.system.exception.UserException;
import com.example.pos.system.modal.Store;
import com.example.pos.system.modal.User;
import com.example.pos.system.payload.dto.UserDto;
import com.example.pos.system.repository.StoreRepository;
import com.example.pos.system.repository.UserRepository;
import com.example.pos.system.service.SuperAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SuperAdminServiceImpl implements SuperAdminService {

    private final StoreRepository storeRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Store createStore(Store store) {
        return storeRepository.save(store);
    }

    @Override
    public List<Store> getAllStores() {
        return storeRepository.findAll();
    }

    @Override
    public Store updateStore(Long id, Store updatedStore) throws Exception {

        Store store = storeRepository.findById(id)
                .orElseThrow(() -> new Exception("Store not found"));

        store.setBrand(updatedStore.getBrand());
        store.setStatus(updatedStore.getStatus());
        store.setContact(updatedStore.getContact());

        return storeRepository.save(store);
    }

    @Override
    public void deleteStore(Long id) throws Exception {

        Store store = storeRepository.findById(id)
                .orElseThrow(() -> new Exception("Store not found"));

        storeRepository.delete(store);
    }

    @Override
    public User createStoreAdmin(UserDto dto) throws UserException {

        if (userRepository.findByEmail(dto.getEmail()) != null) {
            throw new UserException("Email already exists");
        }

        User user = new User();

        user.setFullName(dto.getFullName());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setPhone(dto.getPhone());

        user.setRole(UserRole.ROLE_STORE_ADMIN);

        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        return userRepository.save(user);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public void deleteUser(Long id) throws Exception {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new Exception("User not found"));

        userRepository.delete(user);
    }
}