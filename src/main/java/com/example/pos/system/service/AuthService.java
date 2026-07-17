package com.example.pos.system.service;

import com.example.pos.system.exception.UserException;
import com.example.pos.system.payload.dto.UserDto;
import com.example.pos.system.payload.response.AuthResponse;

public interface AuthService {

    AuthResponse signUp(UserDto userDto) throws UserException;
    AuthResponse login(UserDto userDto) throws UserException;

    AuthResponse createUserByAdmin(UserDto userDto) throws UserException;
}
