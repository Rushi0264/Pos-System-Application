package com.example.pos.system.controller;

import com.example.pos.system.payload.dto.PurchaseDTO;
import com.example.pos.system.payload.response.ApiResponse;
import com.example.pos.system.service.PurchaseService;
import com.example.pos.system.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/purchases")
public class PurchaseController {

    private final PurchaseService purchaseService;
    private final UserService userService;

    @PostMapping
    public ResponseEntity<PurchaseDTO> createPurchase(
            @RequestBody PurchaseDTO purchaseDTO,
            @RequestHeader("Authorization") String jwt
    ) throws Exception {

        userService.getUserFromJwtToken(jwt);

        return ResponseEntity.ok(
                purchaseService.createPurchase(purchaseDTO)
        );
    }

    @GetMapping
    public ResponseEntity<List<PurchaseDTO>> getAllPurchases(
            @RequestHeader("Authorization") String jwt
    ) throws Exception {

        userService.getUserFromJwtToken(jwt);

        return ResponseEntity.ok(
                purchaseService.getAllPurchases()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<PurchaseDTO> getPurchase(
            @PathVariable Long id,
            @RequestHeader("Authorization") String jwt
    ) throws Exception {

        userService.getUserFromJwtToken(jwt);

        return ResponseEntity.ok(
                purchaseService.getPurchaseById(id)
        );
    }

    @GetMapping("/supplier/{supplierId}")
    public ResponseEntity<List<PurchaseDTO>> getBySupplier(
            @PathVariable Long supplierId,
            @RequestHeader("Authorization") String jwt
    ) throws Exception {

        userService.getUserFromJwtToken(jwt);

        return ResponseEntity.ok(
                purchaseService.getPurchasesBySupplier(supplierId)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deletePurchase(
            @PathVariable Long id,
            @RequestHeader("Authorization") String jwt
    ) throws Exception {

        userService.getUserFromJwtToken(jwt);

        purchaseService.deletePurchase(id);

        ApiResponse response = new ApiResponse();
        response.setMessage("Purchase deleted successfully");

        return ResponseEntity.ok(response);
    }
}