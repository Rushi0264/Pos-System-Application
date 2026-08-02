package com.example.pos.system.controller;

import com.example.pos.system.domain.UserRole;
import com.example.pos.system.exception.UserException;
import com.example.pos.system.mapper.UserMapper;
import com.example.pos.system.modal.User;

import com.example.pos.system.payload.dto.UserDto;
import com.example.pos.system.payload.response.ApiResponse;
import com.example.pos.system.payload.response.AuthResponse;
import com.example.pos.system.service.AuthService;
import com.example.pos.system.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final AuthService authService;

    // =========================
    // USER CRUD
    // =========================

    @PostMapping
    public ResponseEntity<UserDto> createUser(
            @RequestBody UserDto userDto
    ) throws Exception {


        AuthResponse response =
                authService.createUserByAdmin(userDto);


        return ResponseEntity.ok(
                response.getUser()
        );
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers(
            @RequestHeader("Authorization") String jwt
    ) throws Exception {

        User currentUser = userService.getUserFromJwtToken(jwt);

        List<UserDto> users = userService.getAllUsers(currentUser)
                .stream()
                .map(UserMapper::toDTO)
                .toList();

        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(
            @PathVariable Long id,
            @RequestHeader("Authorization") String jwt
    ) throws Exception {

        User currentUser = userService.getUserFromJwtToken(jwt);

        User user = userService.getUserById(id, currentUser);

        return ResponseEntity.ok(UserMapper.toDTO(user));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(
            @PathVariable Long id,
            @RequestBody UserDto userDto
    ) throws Exception {

        User user = userService.updateUser(id, userDto);
        return ResponseEntity.ok(UserMapper.toDTO(user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteUser(
            @PathVariable Long id
    ) throws Exception {

        userService.deleteUser(id);

        ApiResponse response = new ApiResponse();
        response.setMessage("User deleted successfully");

        return ResponseEntity.ok(response);
    }

    // =========================
    // USERS BY ROLE
    // =========================

    @GetMapping("/role/{role}")
    public ResponseEntity<List<UserDto>> getUsersByRole(
            @PathVariable UserRole role
    ) {

        List<UserDto> users = userService.getUsersByRole(role)
                .stream()
                .map(UserMapper::toDTO)
                .toList();

        return ResponseEntity.ok(users);
    }

    // =========================
    // PROFILE
    // =========================

    @GetMapping("/profile")
    public ResponseEntity<UserDto> getProfile(
            @RequestHeader("Authorization") String jwt
    ) throws UserException {

        User user = userService.getUserFromJwtToken(jwt);
        return ResponseEntity.ok(UserMapper.toDTO(user));
    }

    // =========================
    // STORE EMPLOYEES
    // =========================

    @GetMapping("/store/{storeId}")
    public ResponseEntity<List<UserDto>> getEmployeesByStore(
            @PathVariable Long storeId
    ) {

        return ResponseEntity.ok(
                userService.getEmployeesByStore(storeId)
        );
    }

    @PostMapping("/employee")
    public ResponseEntity<UserDto> createEmployee(
            @RequestBody UserDto dto
    ) throws Exception {

        return ResponseEntity.ok(
                userService.createEmployee(dto)
        );
    }

    @PutMapping("/employee/{id}")
    public ResponseEntity<UserDto> updateEmployee(
            @PathVariable Long id,
            @RequestBody UserDto dto
    ) throws Exception {

        return ResponseEntity.ok(
                userService.updateEmployee(id, dto)
        );
    }

    @DeleteMapping("/employee/{id}")
    public ResponseEntity<ApiResponse> deleteEmployee(
            @PathVariable Long id
    ) throws Exception {

        userService.deleteEmployee(id);

        ApiResponse response = new ApiResponse();
        response.setMessage("Employee deleted successfully");

        return ResponseEntity.ok(response);
    }

}