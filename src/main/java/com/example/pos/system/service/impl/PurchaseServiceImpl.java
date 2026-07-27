package com.example.pos.system.service.impl;

import com.example.pos.system.domain.MovementType;
import com.example.pos.system.domain.UserRole;
import com.example.pos.system.exception.UserException;
import com.example.pos.system.mapper.PurchaseItemMapper;
import com.example.pos.system.mapper.PurchaseMapper;
import com.example.pos.system.modal.*;
import com.example.pos.system.payload.dto.PurchaseDTO;
import com.example.pos.system.payload.dto.PurchaseItemDTO;
import com.example.pos.system.payload.dto.StockMovementDTO;
import com.example.pos.system.repository.*;
import com.example.pos.system.service.InventoryService;
import com.example.pos.system.service.PurchaseService;
import com.example.pos.system.service.StockMovementService;
import com.example.pos.system.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PurchaseServiceImpl implements PurchaseService {

    private final PurchaseRepository purchaseRepository;
    private final PurchaseItemRepository purchaseItemRepository;
    private final SupplierRepository supplierRepository;
    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;
    private final BranchRepository branchRepository;
    private final UserService userService;
    private final StockMovementService stockMovementService;
    private final InventoryService inventoryService;

    @Override
    public PurchaseDTO createPurchase(PurchaseDTO dto) throws Exception {

        boolean invoiceExists = purchaseRepository.existsByInvoiceNumber(dto.getInvoiceNumber());

        if (invoiceExists) {
            throw new Exception("Invoice number already exists. Please use a unique invoice number.");
        }

        User user = userService.getCurrentUser();

        Supplier supplier = supplierRepository.findById(dto.getSupplierId())
                .orElseThrow(() -> new Exception("Supplier not found"));

        checkAuthority(user, supplier.getStore());

        Purchase purchase = PurchaseMapper.toEntity(
                dto,
                supplier,
                supplier.getStore(),
                null,          // branch = null
                user
        );

        Purchase savedPurchase = purchaseRepository.save(purchase);

        List<PurchaseItem> items = new ArrayList<>();

        for (PurchaseItemDTO itemDTO : dto.getItems()) {

            Product product = productRepository.findById(itemDTO.getProductId())
                    .orElseThrow(() -> new Exception("Product not found"));

            PurchaseItem item = PurchaseItemMapper.toEntity(
                    itemDTO,
                    savedPurchase,
                    product
            );

            purchaseItemRepository.save(item);

            items.add(item);

            StockMovementDTO movementDTO = StockMovementDTO.builder()
                    .productId(product.getId())
                    .storeId(supplier.getStore().getId())
                    .branchId(null)
                    .quantity(item.getQuantity())
                    .type(MovementType.PURCHASE)
                    .description("Purchase invoice: " + dto.getInvoiceNumber())
                    .build();

            stockMovementService.createMovement(movementDTO);
        }

        savedPurchase.setItems(items);

        return PurchaseMapper.toDTO(savedPurchase);
    }

    @Override
    public PurchaseDTO updatePurchase(Long id, PurchaseDTO dto) throws Exception {

        throw new UnsupportedOperationException(
                "Purchase update is not allowed."
        );
    }

    @Override
    public void deletePurchase(Long id) throws Exception {

        Purchase purchase = purchaseRepository.findById(id)
                .orElseThrow(() -> new Exception("Purchase not found"));

        User user = userService.getCurrentUser();

        checkAuthority(user, purchase.getStore());

        purchaseRepository.delete(purchase);
    }

    @Override
    public PurchaseDTO getPurchaseById(Long id) throws Exception {

        Purchase purchase = purchaseRepository.findById(id)
                .orElseThrow(() -> new Exception("Purchase not found"));

        User user = userService.getCurrentUser();

        checkAuthority(user, purchase.getStore());

        return PurchaseMapper.toDTO(purchase);
    }

    @Override
    public List<PurchaseDTO> getAllPurchases() throws Exception {

        User user = userService.getCurrentUser();

        List<Purchase> purchases;

        if (user.getRole() == UserRole.ROLE_SUPER_ADMIN
                || user.getRole() == UserRole.ROLE_INVENTORY_MANAGER) {

            purchases = purchaseRepository.findAll();

        } else {

            purchases = purchaseRepository.findByStoreId(
                    user.getStore().getId()
            );
        }

        return purchases.stream()
                .map(PurchaseMapper::toDTO)
                .toList();
    }

    @Override
    public List<PurchaseDTO> getPurchasesBySupplier(Long supplierId)
            throws Exception {

        User user = userService.getCurrentUser();

        List<Purchase> purchases =
                purchaseRepository.findBySupplierId(supplierId);

        purchases.forEach(p -> {
            try {
                checkAuthority(user, p.getStore());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        return purchases.stream()
                .map(PurchaseMapper::toDTO)
                .toList();
    }

    private void checkAuthority(User user, Store store)
            throws Exception {

        if (user.getRole() == UserRole.ROLE_SUPER_ADMIN) {
            return;
        }

        if (user.getRole() == UserRole.ROLE_INVENTORY_MANAGER) {
            return;
        }

        if (user.getRole() == UserRole.ROLE_ACCOUNTANT) {
            return;
        }

        if (user.getRole() == UserRole.ROLE_STORE_ADMIN &&
                user.getStore() != null &&
                user.getStore().getId().equals(store.getId())) {
            return;
        }
        if (user.getRole() == UserRole.ROLE_BRANCH_MANAGER &&
                user.getBranch() != null &&
                user.getBranch().getStore().getId().equals(store.getId())) {
            return;
        }

        throw new UserException(
                "You don't have permission to manage purchases."
        );
    }
}