package com.example.pos.system.service.impl;

import com.example.pos.system.domain.UserRole;
import com.example.pos.system.exception.UserException;
import com.example.pos.system.mapper.BranchMapper;
import com.example.pos.system.modal.*;
import com.example.pos.system.payload.dto.BranchDTO;
import com.example.pos.system.repository.*;
import com.example.pos.system.service.BranchService;
import com.example.pos.system.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BranchServiceImpl implements BranchService {

    private final BranchRepository branchRepository;
    private final StoreRepository storeRepository;
    private final UserService userService;
    private final InventoryRepository inventoryRepository;
    private final PurchaseRepository purchaseRepository;
    private final UserRepository userRepository;

    @Override
    public BranchDTO createBranch(BranchDTO branchDTO) throws UserException {

        User currentUser = userService.getCurrentUser();

        Store store = storeRepository.findById(branchDTO.getStoreId())
                .orElseThrow(() ->
                        new UserException("Store not found"));

        // Store isolation
        if (currentUser.getStore() != null &&
                !currentUser.getStore().getId().equals(store.getId())) {

            throw new UserException("You cannot create branch for another store");
        }

        Branch branch = BranchMapper.toEntity(branchDTO, store);

        Branch savedBranch = branchRepository.save(branch);

        return BranchMapper.toDTO(savedBranch);
    }

    @Override
    public List<BranchDTO> getBranchesByStore(
            Long storeId
    ) throws Exception {

        User user = userService.getCurrentUser();

        if (user.getRole() != UserRole.ROLE_SUPER_ADMIN
                && user.getRole() != UserRole.ROLE_INVENTORY_MANAGER) {

            if (user.getStore() == null ||
                    !user.getStore().getId().equals(storeId)) {

                throw new UserException(
                        "You cannot access another store branches"
                );
            }
        }

        return branchRepository
                .findByStoreId(storeId)
                .stream()
                .map(BranchMapper::toDTO)
                .toList();
    }

    @Override
    public BranchDTO updateBranch(
            Long id,
            BranchDTO branchDTO
    ) throws Exception {

        User currentUser = userService.getCurrentUser();

        Branch existing = branchRepository.findById(id)
                .orElseThrow(() ->
                        new Exception("Branch not found"));

        // Branch belongs to logged-in store?
        if (currentUser.getStore() != null &&
                !existing.getStore().getId().equals(currentUser.getStore().getId())) {

            throw new UserException("You cannot update another store's branch");
        }

        if (branchDTO.getStoreId() != null) {

            Store store = storeRepository.findById(branchDTO.getStoreId())
                    .orElseThrow(() ->
                            new Exception("Store not found"));

            // Prevent changing to another store
            if (currentUser.getStore() != null &&
                    !currentUser.getStore().getId().equals(store.getId())) {

                throw new UserException("You cannot move branch to another store");
            }

            existing.setStore(store);
        }

        existing.setName(branchDTO.getName());
        existing.setWorkingDays(branchDTO.getWorkingDays());
        existing.setEmail(branchDTO.getEmail());
        existing.setPhone(branchDTO.getPhone());
        existing.setAddress(branchDTO.getAddress());
        existing.setOpenTime(branchDTO.getOpenTime());
        existing.setCloseTime(branchDTO.getCloseTime());
        existing.setUpdatedAt(LocalDateTime.now());

        Branch updatedBranch = branchRepository.save(existing);

        return BranchMapper.toDTO(updatedBranch);
    }

    @Override
    public void deleteBranch(Long id) throws Exception {
        Branch existing = branchRepository.findById(id).orElseThrow(
                () -> new Exception("branch not found..")
        );

        List<Inventory> inventories = inventoryRepository.findByBranchId(id);
        inventoryRepository.deleteAll(inventories);

        List<Purchase> purchases = purchaseRepository.findByBranchId(id);
        purchaseRepository.deleteAll(purchases);

        List<User> users = userRepository.findByBranchId(id);
        userRepository.deleteAll(users);

        branchRepository.delete(existing);
    }

/*    @Override
    public List<BranchDTO> getAllBranchesByStoreId(Long storeId) {
        List<Branch> branches = branchRepository.findByStoreId(storeId);
        return branches.stream().map(BranchMapper::toDTO)
                .collect(Collectors.toList());
    }*/

    @Override
    public BranchDTO getBranchById(Long id) throws Exception {
        Branch existing = branchRepository.findById(id).orElseThrow(
                () -> new Exception("branch not found..")
        );
        return BranchMapper.toDTO(existing);
    }

    @Override
    public List<BranchDTO> getAllBranches() {
        return branchRepository.findAll()
                .stream()
                .map(BranchMapper::toDTO)
                .toList();
    }
    @Override
    public List<BranchDTO> getAllBranchesByStoreId(Long storeId, User user) throws Exception {

        if(user.getRole().name().equals("BRANCH_MANAGER")) {

            if(user.getBranch() == null) {
                throw new UserException(
                        "Branch manager is not assigned to branch"
                );
            }

            Long userStoreId =
                    user.getBranch()
                            .getStore()
                            .getId();

            if(!userStoreId.equals(storeId)) {
                throw new UserException(
                        "You cannot access another store branches"
                );
            }
        }

        if(user.getStore() != null
                && user.getRole() != UserRole.ROLE_SUPER_ADMIN
                && user.getRole() != UserRole.ROLE_INVENTORY_MANAGER
                && user.getRole() != UserRole.ROLE_ACCOUNTANT) {

            if(!user.getStore().getId().equals(storeId)) {
                throw new UserException(
                        "You cannot access another store branches"
                );
            }
        }

        return branchRepository.findByStoreId(storeId)
                .stream()
                .map(BranchMapper::toDTO)
                .toList();
    }
    @Override
    public BranchDTO getBranchById(Long id, User user) throws Exception {

        Branch branch = branchRepository.findById(id)
                .orElseThrow(() -> new Exception("Branch not found"));


        if (user.getRole() == UserRole.ROLE_SUPER_ADMIN) {
            return BranchMapper.toDTO(branch);
        }

        if (user.getStore() == null) {
            throw new UserException("User is not assigned to any store");
        }

        if (!branch.getStore().getId().equals(user.getStore().getId())) {
            throw new UserException("Access Denied");
        }

        return BranchMapper.toDTO(branch);
    }
    @Override
    public List<BranchDTO> getAllBranches(User user) throws Exception {

        if (user.getStore() == null) {

            return branchRepository.findAll()
                    .stream()
                    .map(BranchMapper::toDTO)
                    .toList();
        }

        return branchRepository.findByStoreId(user.getStore().getId())
                .stream()
                .map(BranchMapper::toDTO)
                .toList();
    }
}
