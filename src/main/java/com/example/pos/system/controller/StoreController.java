package com.example.pos.system.controller;

import com.example.pos.system.domain.StoreStatus;
import com.example.pos.system.mapper.StoreMapper;
import com.example.pos.system.modal.User;
import com.example.pos.system.payload.dto.StoreDTO;
import com.example.pos.system.payload.dto.UserDto;
import com.example.pos.system.payload.response.ApiResponse;
import com.example.pos.system.service.StoreService;
import com.example.pos.system.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/stores")
public class StoreController {

    private final StoreService storeService;
    private final UserService userService;

    @PostMapping
    public ResponseEntity<StoreDTO> createStore(
            @RequestBody StoreDTO storeDTO,
            @RequestHeader("Authorization") String jwt
    ) throws Exception {

        User user = userService.getUserFromJwtToken(jwt);

        return ResponseEntity.ok(
                storeService.createStore(storeDTO, user)
        );
    }

    @GetMapping
    public ResponseEntity<List<StoreDTO>> getAllStores(
            @RequestHeader("Authorization") String jwt
    ) throws Exception {

        User user = userService.getUserFromJwtToken(jwt);

        return ResponseEntity.ok(
                storeService.getAllStores(user)
        );
    }

    @GetMapping("/my-store")
    public ResponseEntity<StoreDTO> getMyStore(
            @RequestHeader("Authorization") String jwt
    ) throws Exception {

        User user = userService.getUserFromJwtToken(jwt);

        return ResponseEntity.ok(
                storeService.getStoreByUser(user)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<StoreDTO> getStoreById(
            @PathVariable Long id,
            @RequestHeader("Authorization") String jwt
    ) throws Exception {

        User user = userService.getUserFromJwtToken(jwt);

        return ResponseEntity.ok(
                storeService.getStoreById(id, user)
        );
    }

    @GetMapping("/admin")
    public ResponseEntity<StoreDTO> getStoreByAdmin()
            throws Exception {

        return ResponseEntity.ok(
                StoreMapper.toDTO(
                        storeService.getStoreByAdmin()
                )
        );
    }

    @GetMapping("/employee")
    public ResponseEntity<StoreDTO> getStoreByEmployee()
            throws Exception {

        return ResponseEntity.ok(
                storeService.getStoreByEmployee()
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<StoreDTO> updateStore(
            @PathVariable Long id,
            @RequestBody StoreDTO storeDTO
    ) throws Exception {

        return ResponseEntity.ok(
                storeService.updateStore(id, storeDTO)
        );
    }

    @PutMapping("/{id}/moderate")
    public ResponseEntity<StoreDTO> moderateStore(
            @PathVariable Long id,
            @RequestParam StoreStatus status
    ) throws Exception {

        return ResponseEntity.ok(
                storeService.moderateStore(id, status)
        );
    }

    @GetMapping("/{id}/admin-contact")
    public ResponseEntity<List<UserDto>> getStoreAdminsContact(
            @PathVariable Long id,
            @RequestHeader("Authorization") String jwt
    ) throws Exception {

        User user = userService.getUserFromJwtToken(jwt);

        return ResponseEntity.ok(
                storeService.getStoreAdminsContact(id, user)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteStore(
            @PathVariable Long id
    ) throws Exception {

        storeService.deleteStore(id);

        ApiResponse response = new ApiResponse();
        response.setMessage("Store deleted successfully");

        return ResponseEntity.ok(response);
    }

}