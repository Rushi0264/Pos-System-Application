package com.example.pos.system.controller;

import com.example.pos.system.exception.UserException;
import com.example.pos.system.payload.dto.UserDto;
import com.example.pos.system.payload.response.AuthResponse;
import com.example.pos.system.service.AuthService;
import com.example.pos.system.service.impl.AuthServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    //private final AuthServiceImpl authServiceImpl;
    private final AuthService authService;

//      http://localhost:8080/auth/signup

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> signupHandler(
            @RequestBody UserDto userDto
    ) throws UserException {

        return ResponseEntity.ok(
                authService.signUp(userDto)
        );
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> loginHandler(
            @RequestBody UserDto userDto
    ) throws UserException {

        return ResponseEntity.ok(
                authService.login(userDto)
        );
    }

    @PostMapping("/admin/create-user")
    @PreAuthorize("hasRole('ROLE_SUPER_ADMIN')")
    public ResponseEntity<AuthResponse> createUserHandler(@RequestBody UserDto userDto) throws UserException {
        return ResponseEntity.ok(authService.createUserByAdmin(userDto));
    }

}

