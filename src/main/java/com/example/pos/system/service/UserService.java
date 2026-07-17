package com.example.pos.system.service;

import com.example.pos.system.domain.UserRole;
import com.example.pos.system.exception.UserException;
import com.example.pos.system.modal.User;
import com.example.pos.system.payload.dto.UserDto;

import java.util.List;

public interface UserService {

    User getUserFromJwtToken(String token) throws UserException;

    User getCurrentUser() throws UserException;

    User getUserByEmail(String email) throws UserException;

    //User getUserById(Long id) throws Exception;

    //List<User> getAllUser();

    User createUser(UserDto userDto) throws UserException;

    User updateUser(Long id, UserDto userDto) throws Exception;

    void deleteUser(Long id) throws Exception;

    User findUserById(Long storeAdminId);

    List<User> getUsersByRole(UserRole role);

    void save(User storeAdmin);
    List<UserDto> getEmployeesByStore(Long storeId);

    List<User> getUsersByStore(Long storeId);

    UserDto createEmployee(UserDto dto) throws UserException;

    UserDto updateEmployee(Long id, UserDto dto) throws Exception;

    void deleteEmployee(Long id) throws Exception;
    List<User> getAllUsers(User currentUser) throws Exception;
    User getUserById(Long id, User currentUser) throws Exception;
}