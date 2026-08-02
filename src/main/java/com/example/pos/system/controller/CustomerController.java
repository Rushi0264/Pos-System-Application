package com.example.pos.system.controller;

import com.example.pos.system.payload.dto.CustomerDTO;
import com.example.pos.system.payload.response.ApiResponse;
import com.example.pos.system.service.CustomerService;
import com.example.pos.system.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService customerService;
    private final UserService userService;

    @PostMapping
    public ResponseEntity<CustomerDTO> create(
            @RequestBody CustomerDTO customerDTO,
            @RequestHeader("Authorization") String jwt
    ) throws Exception {

        userService.getUserFromJwtToken(jwt);
        return ResponseEntity.ok(
                customerService.createCustomer(customerDTO)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomerDTO> update(
            @PathVariable Long id,
            @RequestBody CustomerDTO customerDTO,
            @RequestHeader("Authorization") String jwt
    ) throws Exception {

        userService.getUserFromJwtToken(jwt);
        return ResponseEntity.ok(
                customerService.updateCustomer(id, customerDTO)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> delete(
            @PathVariable Long id,
            @RequestHeader("Authorization") String jwt
    ) throws Exception {

        userService.getUserFromJwtToken(jwt);

        customerService.deleteCustomer(id);

        ApiResponse response = new ApiResponse();
        response.setMessage("Customer deleted successfully");

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerDTO> getCustomer(
            @PathVariable Long id,
            @RequestHeader("Authorization") String jwt
    ) throws Exception {

        userService.getUserFromJwtToken(jwt);

        return ResponseEntity.ok(
                customerService.getCustomer(id)
        );
    }

    @GetMapping
    public ResponseEntity<List<CustomerDTO>> getAll(
            @RequestHeader("Authorization") String jwt
    )
            throws Exception {

        userService.getUserFromJwtToken(jwt);
        return ResponseEntity.ok(
                customerService.getAllCustomers()
        );
    }

    @GetMapping("/search")
    public ResponseEntity<List<CustomerDTO>> search(
            @RequestParam String q,
            @RequestHeader("Authorization") String jwt
    ) throws Exception {

        userService.getUserFromJwtToken(jwt);

        return ResponseEntity.ok(
                customerService.searchCustomer(q)
        );
    }

}