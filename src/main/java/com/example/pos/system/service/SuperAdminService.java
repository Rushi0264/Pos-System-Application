package com.example.pos.system.service;

import com.example.pos.system.exception.UserException;
import com.example.pos.system.modal.Store;
import com.example.pos.system.modal.User;
import com.example.pos.system.payload.dto.UserDto;

import java.util.List;

public interface SuperAdminService {

    Store createStore(Store store) throws Exception;

    List<Store> getAllStores();

    Store updateStore(Long id, Store store) throws Exception;

    void deleteStore(Long id) throws Exception;

    User createStoreAdmin(UserDto userDto) throws UserException;

    List<User> getAllUsers();

    void deleteUser(Long id) throws Exception;

}