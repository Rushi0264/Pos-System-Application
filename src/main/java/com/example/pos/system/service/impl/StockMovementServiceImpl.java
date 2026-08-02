package com.example.pos.system.service.impl;


import com.example.pos.system.domain.MovementType;
import com.example.pos.system.exception.UserException;
import com.example.pos.system.modal.*;
import com.example.pos.system.payload.dto.StockMovementDTO;
import com.example.pos.system.repository.*;
import com.example.pos.system.service.NotificationService;
import com.example.pos.system.service.StockMovementService;

import com.example.pos.system.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.util.List;


@Service
@RequiredArgsConstructor
public class StockMovementServiceImpl implements StockMovementService {


    private final StockMovementRepository movementRepository;

    private final ProductRepository productRepository;

    private final BranchRepository branchRepository;

    private final StoreRepository storeRepository;

    private final InventoryRepository inventoryRepository;
    private final UserService userService;
    private final NotificationService notificationService;

    @Override
    public StockMovementDTO createMovement(
            StockMovementDTO dto
    ) throws Exception {

        Product product =
                productRepository.findById(dto.getProductId())
                        .orElseThrow(() -> new Exception("Product not found"));


        if (dto.getType().name().equals("PURCHASE")) {

            if (dto.getStoreId() == null) {
                throw new Exception("Store id is required for purchase");
            }

            Store store =
                    storeRepository.findById(dto.getStoreId())
                            .orElseThrow(() -> new Exception("Store not found"));

            StockMovement movement =
                    StockMovement.builder()
                            .product(product)
                            .branch(null)
                            .store(store)
                            .quantity(dto.getQuantity())
                            .type(dto.getType())
                            .description(dto.getDescription())
                            .build();

            StockMovement saved = movementRepository.save(movement);

            dto.setId(saved.getId());

            return dto;
        }


        // ---------- SALE / RETURN / ADJUSTMENT : branch level ----------
        if (dto.getBranchId() == null) {
            throw new Exception("Branch id is required for this movement type");
        }

        Branch branch =
                branchRepository.findById(dto.getBranchId())
                        .orElseThrow(() -> new Exception("Branch not found"));

        Inventory inventory =
                inventoryRepository.findByProductIdAndBranchId(
                        product.getId(),
                        branch.getId()
                );

        if (inventory == null) {
            throw new Exception("Inventory not created");
        }

        // STOCK OUT (SALE)
        if (dto.getType().name().equals("SALE")) {

            if (inventory.getQuantity() < dto.getQuantity()) {
                throw new Exception("Insufficient stock");
            }

            inventory.setQuantity(
                    inventory.getQuantity() - dto.getQuantity()
            );
        }

        // STOCK IN (RETURN)
        if (dto.getType().name().equals("RETURN")) {

            inventory.setQuantity(
                    inventory.getQuantity() + dto.getQuantity()
            );
        }

        // ADJUSTMENT — quantity directly set
        if (dto.getType().name().equals("ADJUSTMENT")) {

            inventory.setQuantity(
                    inventory.getQuantity() + dto.getQuantity()
            );
        }

        inventoryRepository.save(inventory);

        StockMovement movement =
                StockMovement.builder()
                        .product(product)
                        .branch(branch)
                        .store(branch.getStore())
                        .quantity(dto.getQuantity())
                        .type(dto.getType())
                        .description(dto.getDescription())
                        .build();

        StockMovement saved = movementRepository.save(movement);

        dto.setId(saved.getId());

        return dto;
    }


    @Override
    public List<StockMovementDTO> getByBranch(Long branchId) {

        return movementRepository
                .findByBranchId(branchId)
                .stream()
                .map(m -> {

                    StockMovementDTO dto = new StockMovementDTO();

                    dto.setId(m.getId());
                    dto.setProductId(m.getProduct().getId());
                    dto.setBranchId(
                            m.getBranch() != null ? m.getBranch().getId() : null
                    );
                    dto.setQuantity(m.getQuantity());
                    dto.setType(m.getType());
                    dto.setDescription(m.getDescription());

                    return dto;

                })
                .toList();
    }

    @Override
    public List<StockMovementDTO> getByProduct(Long productId) {

        return movementRepository
                .findByProductId(productId)
                .stream()
                .map(m -> {

                    StockMovementDTO dto = new StockMovementDTO();

                    dto.setId(m.getId());
                    dto.setProductId(m.getProduct().getId());
                    dto.setBranchId(
                            m.getBranch() != null ? m.getBranch().getId() : null
                    );
                    dto.setQuantity(m.getQuantity());
                    dto.setType(m.getType());
                    dto.setDescription(m.getDescription());

                    return dto;

                })
                .toList();
    }

    @Override
    public void transferStockToBranch(
            Long storeId,
            Long branchId,
            Long productId,
            Integer quantity
    ) throws Exception {

        User user = userService.getCurrentUser();


        if (user.getRole().name().equals("ROLE_BRANCH_MANAGER")) {
            throw new Exception(
                    "Branch cannot initiate stock transfer"
            );
        }

        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new Exception("Store not found"));

        Branch branch = branchRepository.findById(branchId)
                .orElseThrow(() -> new Exception("Branch not found"));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new Exception("Product not found"));


        if (!branch.getStore().getId().equals(storeId)) {
            throw new Exception("This branch does not belong to the given store");
        }

        Integer currentStoreStock = getStoreStock(storeId, productId);

        if (currentStoreStock < quantity) {
            throw new UserException("Insufficient stock in store to transfer");
        }


        StockMovement outMovement = StockMovement.builder()
                .product(product)
                .branch(null)
                .store(store)
                .quantity(quantity)
                .type(MovementType.TRANSFER_OUT)
                .description("Transferred to branch: " + branch.getName())
                .build();

        movementRepository.save(outMovement);


        StockMovement inMovement = StockMovement.builder()
                .product(product)
                .branch(branch)
                .store(store)
                .quantity(quantity)
                .type(MovementType.TRANSFER_IN)
                .description("Received from store")
                .build();

        movementRepository.save(inMovement);


        Inventory inventory = inventoryRepository
                .findByProductIdAndBranchId(productId, branchId);

        if (inventory != null) {

            inventory.setQuantity(
                    inventory.getQuantity() + quantity
            );

            Inventory savedInventory = inventoryRepository.save(inventory);

            notificationService.checkAndNotifyLowStock(savedInventory);

        } else {

            Inventory newInventory = Inventory.builder()
                    .product(product)
                    .branch(branch)
                    .quantity(quantity)
                    .build();

            Inventory savedInventory = inventoryRepository.save(newInventory);

            notificationService.checkAndNotifyLowStock(savedInventory);
        }
    }


    @Override
    public Integer getStoreStock(
            Long storeId,
            Long productId
    ) throws Exception {

        return movementRepository.getStoreStock(storeId, productId);
    }
}