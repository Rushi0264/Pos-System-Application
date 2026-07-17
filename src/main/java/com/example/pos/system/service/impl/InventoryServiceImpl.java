package com.example.pos.system.service.impl;

import com.example.pos.system.domain.UserRole;
import com.example.pos.system.exception.UserException;
import com.example.pos.system.mapper.InventoryMapper;
import com.example.pos.system.modal.Branch;
import com.example.pos.system.modal.Inventory;
import com.example.pos.system.modal.Product;
import com.example.pos.system.modal.User;
import com.example.pos.system.payload.dto.InventoryDTO;
import com.example.pos.system.repository.BranchRepository;
import com.example.pos.system.repository.InventoryRepository;
import com.example.pos.system.repository.ProductRepository;
import com.example.pos.system.service.InventoryService;
import com.example.pos.system.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;
    private final BranchRepository branchRepository;
    private final ProductRepository productRepository;
    private final UserService userService;

    @Override
    public InventoryDTO createInventory(InventoryDTO dto) throws Exception {

        User user = userService.getCurrentUser();


        Branch branch = branchRepository.findById(dto.getBranchId())
                .orElseThrow(() ->
                        new Exception("Branch not found"));


        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() ->
                        new Exception("Product not found"));


        checkAuthority(user, branch);



        // Duplicate inventory check
        boolean exists =
                inventoryRepository.existsByProductIdAndBranchId(
                        product.getId(),
                        branch.getId()
                );


        if(exists){

            throw new Exception(
                    "Inventory already exists for this product in this branch"
            );

        }



        Inventory inventory = Inventory.builder()
                .branch(branch)
                .product(product)
                .quantity(dto.getQuantity())
                .build();



        Inventory savedInventory =
                inventoryRepository.save(inventory);



        return InventoryMapper.toDTO(savedInventory);
    }

    @Override
    public InventoryDTO updateInventory(Long id, InventoryDTO dto) throws Exception {

        User user = userService.getCurrentUser();

        Inventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new Exception("Inventory not found"));

        checkAuthority(user, inventory.getBranch());

        inventory.setQuantity(dto.getQuantity());

        return InventoryMapper.toDTO(
                inventoryRepository.save(inventory)
        );
    }

    @Override
    public void deleteInventory(Long id) throws Exception {

        User user = userService.getCurrentUser();

        Inventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new Exception("Inventory not found"));

        checkAuthority(user, inventory.getBranch());

        inventoryRepository.delete(inventory);
    }

    @Override
    public List<InventoryDTO> getAllInventory() throws Exception {

        User user = userService.getCurrentUser();

        return getAllInventory(user);
    }

    @Override
    public List<InventoryDTO> getAllInventory(User user) {

        List<Inventory> list;

        if (user.getRole() == UserRole.ROLE_SUPER_ADMIN) {

            list = inventoryRepository.findAll();

        } else {

            list = inventoryRepository.findByBranchStoreId(
                    user.getStore().getId()
            );
        }

        return list.stream()
                .map(InventoryMapper::toDTO)
                .toList();
    }

    @Override
    public InventoryDTO getInventoryById(Long id) throws Exception {

        User user = userService.getCurrentUser();

        return getInventoryById(id, user);
    }

    @Override
    public InventoryDTO getInventoryById(Long id, User user) throws Exception {

        Inventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new Exception("Inventory not found"));

        checkAuthority(user, inventory.getBranch());

        return InventoryMapper.toDTO(inventory);
    }

    @Override
    public InventoryDTO getInventoryByProductIdAndBranchId(Long productId, Long branchId) {

        Inventory inventory =
                inventoryRepository.findByProductIdAndBranchId(productId, branchId);

        return InventoryMapper.toDTO(inventory);
    }

    @Override
    public InventoryDTO getInventoryByProductIdAndBranchId(Long productId,
                                                           Long branchId,
                                                           User user) throws Exception {

        Inventory inventory =
                inventoryRepository.findByProductIdAndBranchId(productId, branchId);

        checkAuthority(user, inventory.getBranch());

        return InventoryMapper.toDTO(inventory);
    }

    @Override
    public List<InventoryDTO> getAllInventoryByBranchId(Long branchId) {

        return inventoryRepository.findByBranchId(branchId)
                .stream()
                .map(InventoryMapper::toDTO)
                .toList();
    }

    @Override
    public List<InventoryDTO> getAllInventoryByBranchId(Long branchId,
                                                        User user) throws Exception {

        Branch branch = branchRepository.findById(branchId)
                .orElseThrow(() -> new Exception("Branch not found"));

        checkAuthority(user, branch);

        return inventoryRepository.findByBranchId(branchId)
                .stream()
                .map(InventoryMapper::toDTO)
                .toList();
    }

    @Override
    public void addStock(Long productId,
                         Long branchId,
                         Integer quantity) throws Exception {

        Inventory inventory =
                inventoryRepository.findByProductIdAndBranchId(productId, branchId);

        if (inventory != null) {

            inventory.setQuantity(inventory.getQuantity() + quantity);
            inventory.setLastUpdate(LocalDateTime.now());

            inventoryRepository.save(inventory);

        } else {

            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new Exception("Product not found"));

            Branch branch = branchRepository.findById(branchId)
                    .orElseThrow(() -> new Exception("Branch not found"));

            Inventory newInventory = Inventory.builder()
                    .product(product)
                    .branch(branch)
                    .quantity(quantity)
                    .lastUpdate(LocalDateTime.now())
                    .build();

            inventoryRepository.save(newInventory);
        }
    }

    private void checkAuthority(User user, Branch branch) throws Exception {

        if (user.getRole() == UserRole.ROLE_SUPER_ADMIN) {
            return;
        }

        if (user.getRole() == UserRole.ROLE_STORE_ADMIN) {

            if (user.getStore() != null &&
                    user.getStore().getId().equals(branch.getStore().getId())) {
                return;
            }
        }

        throw new UserException(
                "You don't have permission to access this inventory."
        );
    }
}