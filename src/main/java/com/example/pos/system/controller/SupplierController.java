package com.example.pos.system.controller;

import com.example.pos.system.modal.User;
import com.example.pos.system.payload.dto.SupplierDTO;
import com.example.pos.system.payload.response.ApiResponse;
import com.example.pos.system.service.SupplierService;
import com.example.pos.system.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/suppliers")
@RequiredArgsConstructor
public class SupplierController {

    private final SupplierService supplierService;
    private final UserService userService;

    @PostMapping
    public ResponseEntity<SupplierDTO> createSupplier(
            @RequestBody SupplierDTO supplierDTO,
            @RequestHeader("Authorization") String jwt
    ) throws Exception {

        userService.getUserFromJwtToken(jwt);

        return ResponseEntity.ok(
                supplierService.createSupplier(supplierDTO)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<SupplierDTO> updateSupplier(
            @PathVariable Long id,
            @RequestBody SupplierDTO supplierDTO,
            @RequestHeader("Authorization") String jwt
    ) throws Exception {

        userService.getUserFromJwtToken(jwt);

        return ResponseEntity.ok(
                supplierService.updateSupplier(id, supplierDTO)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteSupplier(
            @PathVariable Long id,
            @RequestHeader("Authorization") String jwt
    ) throws Exception {

        userService.getUserFromJwtToken(jwt);

        supplierService.deleteSupplier(id);

        ApiResponse response = new ApiResponse();
        response.setMessage("Supplier deleted successfully");

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<SupplierDTO>> getAllSuppliers(
            @RequestHeader("Authorization") String jwt
    ) throws Exception {

        User user = userService.getUserFromJwtToken(jwt);

        return ResponseEntity.ok(
                supplierService.getAllSuppliers(user)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<SupplierDTO> getSupplierById(
            @PathVariable Long id,
            @RequestHeader("Authorization") String jwt
    ) throws Exception {

        User user = userService.getUserFromJwtToken(jwt);

        return ResponseEntity.ok(
                supplierService.getSupplierById(id, user)
        );
    }

}