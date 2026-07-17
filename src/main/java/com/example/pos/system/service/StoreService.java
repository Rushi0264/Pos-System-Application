package com.example.pos.system.service;

import com.example.pos.system.domain.StoreStatus;
import com.example.pos.system.exception.UserException;
import com.example.pos.system.modal.Store;
import com.example.pos.system.modal.User;
import com.example.pos.system.payload.dto.StoreDTO;
import com.example.pos.system.payload.dto.UserDto;

import java.util.List;

public interface StoreService {
    StoreDTO createStore(
            StoreDTO storeDTO,
            User user
    ) throws Exception;

    //StoreDTO getStoreById(Long id) throws Exception;

    //List<StoreDTO> getAllStores();

    List<StoreDTO> getAllStores(User user) throws Exception;

    StoreDTO getStoreById(Long id, User user) throws Exception;

    Store getStoreByAdmin() throws UserException;

    StoreDTO updateStore(Long id, StoreDTO storeDTO) throws Exception;

    void deleteStore(Long id) throws UserException;

    StoreDTO getStoreByEmployee() throws UserException;

    StoreDTO moderateStore(Long id, StoreStatus status) throws Exception;

    StoreDTO getStoreByUser(User user) throws Exception;
}
