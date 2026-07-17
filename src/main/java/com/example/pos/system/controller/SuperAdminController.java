package com.example.pos.system.controller;

import com.example.pos.system.exception.UserException;
import com.example.pos.system.modal.Store;
import com.example.pos.system.modal.User;
import com.example.pos.system.payload.dto.UserDto;
import com.example.pos.system.service.SuperAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/super-admin")
@RequiredArgsConstructor
public class SuperAdminController {

    private final SuperAdminService superAdminService;

    @PostMapping("/store")
    public ResponseEntity<Store> createStore(@RequestBody Store store) throws Exception {
        return ResponseEntity.ok(superAdminService.createStore(store));
    }

    @GetMapping("/stores")
    public ResponseEntity<List<Store>> getAllStores() {
        return ResponseEntity.ok(superAdminService.getAllStores());
    }

    @PutMapping("/store/{id}")
    public ResponseEntity<Store> updateStore(
            @PathVariable Long id,
            @RequestBody Store store
    ) throws Exception {

        return ResponseEntity.ok(
                superAdminService.updateStore(id, store)
        );
    }

    @DeleteMapping("/store/{id}")
    public ResponseEntity<String> deleteStore(@PathVariable Long id) throws Exception {

        superAdminService.deleteStore(id);

        return ResponseEntity.ok("Store deleted successfully");
    }

    @PostMapping("/store-admin")
    public ResponseEntity<User> createStoreAdmin(
            @RequestBody UserDto dto
    ) throws UserException {

        return ResponseEntity.ok(
                superAdminService.createStoreAdmin(dto)
        );
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {

        return ResponseEntity.ok(
                superAdminService.getAllUsers()
        );
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<String> deleteUser(
            @PathVariable Long id
    ) throws Exception {

        superAdminService.deleteUser(id);

        return ResponseEntity.ok("User deleted successfully");
    }
}