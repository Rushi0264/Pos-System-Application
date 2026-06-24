package com.example.pos.system.service.impl;

import com.example.pos.system.domain.StoreStatus;
import com.example.pos.system.exception.UserException;
import com.example.pos.system.mapper.StoreMapper;
import com.example.pos.system.modal.Store;
import com.example.pos.system.modal.StoreContact;
import com.example.pos.system.modal.User;
import com.example.pos.system.payload.dto.StoreDTO;
import com.example.pos.system.repository.StoreRepository;
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

    @Override
    public StoreDTO createStore(StoreDTO storeDTO, User user) {
        Store store = StoreMapper.toEntity(storeDTO, user);

        return StoreMapper.toDTO(storeRepository.save(store));
    }

    @Override
    public StoreDTO getStoreById(Long id) throws Exception {

        Store store = storeRepository.findById(id).orElseThrow(
                () -> new Exception("store not found..")
        );
        return StoreMapper.toDTO(store);
    }

    @Override
    public List<StoreDTO> getAllStores() {
        List<Store> dtos = storeRepository.findAll();
        return dtos.stream().map(StoreMapper::toDTO).collect(Collectors.toList());
    }

    @Override
    public Store getStoreByAdmin() throws UserException {
        User admin = userService.getCurrentUser();
        return storeRepository.findByStoreAdminId(admin.getId());
    }

    @Override
    public StoreDTO updateStore(Long id, StoreDTO storeDTO) throws Exception {

        Store existing = storeRepository.findById(id)
                .orElseThrow(() -> new Exception("store not found"));

        existing.setBrand(storeDTO.getBrand());
        existing.setDescription(storeDTO.getDescription());

        if (storeDTO.getStoreType() != null) {
            existing.setStoreType(storeDTO.getStoreType());
        }

        if (storeDTO.getContact() != null) {
            StoreContact contact = StoreContact.builder()
                    .address(storeDTO.getContact().getAddress())
                    .phone(storeDTO.getContact().getPhone())
                    .email(storeDTO.getContact().getEmail())
                    .build();

            existing.setContact(contact);
        }

        return StoreMapper.toDTO(storeRepository.save(existing));
    }

    @Override
    public void deleteStore(Long id) throws UserException {
        Store store = getStoreByAdmin();
        storeRepository.delete(store);
    }

    @Override
    public StoreDTO getStoreByEmployee() throws UserException {
        User currentUser = userService.getCurrentUser();

        if (currentUser == null) {
            throw new UserException("you dont have permission to access this store..");
        }

        return StoreMapper.toDTO(currentUser.getStore());
    }

    @Override
    public StoreDTO moderateStore(Long id, StoreStatus status) throws Exception {
        Store store = storeRepository.findById(id).orElseThrow(
                ()->new Exception("store not found..")
        );

        store.setStatus(status);
        Store updateStore = storeRepository.save(store);
        return StoreMapper.toDTO(updateStore);
    }
}
