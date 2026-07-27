package com.example.pos.system.controller;


import com.example.pos.system.payload.dto.StockMovementDTO;
import com.example.pos.system.service.StockMovementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/stock-movements")
public class StockMovementController {


    private final StockMovementService stockMovementService;



    /*
        Create Stock Movement

        PURCHASE  -> Increase Stock
        SALE      -> Decrease Stock
        RETURN    -> Increase Stock
        ADJUSTMENT-> Manual update
    */
    @PostMapping
    public ResponseEntity<StockMovementDTO> createMovement(
            @RequestBody StockMovementDTO dto
    ) throws Exception {


        return ResponseEntity.ok(
                stockMovementService.createMovement(dto)
        );

    }





    /*
        Get Stock History By Branch
    */
    @GetMapping("/branch/{branchId}")
    public ResponseEntity<List<StockMovementDTO>> getByBranch(
            @PathVariable Long branchId
    ){


        return ResponseEntity.ok(
                stockMovementService.getByBranch(branchId)
        );

    }






    /*
        Get Stock History By Product
        Optional for reports
    */
    @GetMapping("/product/{productId}")
    public ResponseEntity<List<StockMovementDTO>> getByProduct(
            @PathVariable Long productId
    ){


        return ResponseEntity.ok(
                stockMovementService.getByProduct(productId)
        );

    }


    @PostMapping("/transfer")
    public ResponseEntity<String> transferStockToBranch(
            @RequestParam Long storeId,
            @RequestParam Long branchId,
            @RequestParam Long productId,
            @RequestParam Integer quantity
    ) throws Exception {

        stockMovementService.transferStockToBranch(
                storeId, branchId, productId, quantity
        );

        return ResponseEntity.ok("Stock transferred successfully");
    }



    /*
        Get current store-level stock for a product
    */
    @GetMapping("/store/{storeId}/product/{productId}")
    public ResponseEntity<Integer> getStoreStock(
            @PathVariable Long storeId,
            @PathVariable Long productId
    ) throws Exception {

        return ResponseEntity.ok(
                stockMovementService.getStoreStock(storeId, productId)
        );

    }

}