package com.example.pos.system.controller;

import com.example.pos.system.modal.User;
import com.example.pos.system.payload.dto.InventoryDTO;
import com.example.pos.system.payload.response.ApiResponse;
import com.example.pos.system.service.InventoryService;
import com.example.pos.system.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/inventories")
public class InventoryController {

    private final InventoryService inventoryService;
    private final UserService userService;

    @PostMapping
    public ResponseEntity<InventoryDTO> create(
            @RequestBody InventoryDTO inventoryDTO,
            @RequestHeader("Authorization") String jwt
    ) throws Exception {

        //User user = userService.getUserFromJwtToken(jwt);

        return ResponseEntity.ok(
                inventoryService.createInventory(inventoryDTO)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<InventoryDTO> update(
            @RequestBody InventoryDTO inventoryDTO,
            @PathVariable Long id,
            @RequestHeader("Authorization") String jwt
    ) throws Exception {

        //User user = userService.getUserFromJwtToken(jwt);

        return ResponseEntity.ok(
                inventoryService.updateInventory(id, inventoryDTO)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> delete(
            @PathVariable Long id,
            @RequestHeader("Authorization") String jwt
    ) throws Exception {

        //User user = userService.getUserFromJwtToken(jwt);

        inventoryService.deleteInventory(id);

        ApiResponse response = new ApiResponse();
        response.setMessage("Inventory deleted successfully");

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<InventoryDTO>> getAllInventory(
            @RequestHeader("Authorization") String jwt
    ) throws Exception {

        User user = userService.getUserFromJwtToken(jwt);

        return ResponseEntity.ok(
                inventoryService.getAllInventory(user)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<InventoryDTO> getInventoryById(
            @PathVariable Long id,
            @RequestHeader("Authorization") String jwt
    ) throws Exception {

        User user = userService.getUserFromJwtToken(jwt);

        return ResponseEntity.ok(
                inventoryService.getInventoryById(id, user)
        );
    }

    @GetMapping("/branch/{branchId}")
    public ResponseEntity<List<InventoryDTO>> getInventoryByBranch(
            @PathVariable Long branchId,
            @RequestHeader("Authorization") String jwt
    ) throws Exception {

        User user = userService.getUserFromJwtToken(jwt);

        return ResponseEntity.ok(
                inventoryService.getAllInventoryByBranchId(branchId, user)
        );
    }

    @GetMapping("/branch/{branchId}/product/{productId}")
    public ResponseEntity<InventoryDTO> getInventoryByProductAndBranchId(
            @PathVariable Long branchId,
            @PathVariable Long productId,
            @RequestHeader("Authorization") String jwt
    ) throws Exception {

        User user = userService.getUserFromJwtToken(jwt);

        return ResponseEntity.ok(
                inventoryService.getInventoryByProductIdAndBranchId(
                        productId,
                        branchId,
                        user
                )
        );
    }
}