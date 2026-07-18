package com.example.pos.system.service.impl;

import com.example.pos.system.domain.UserRole;
import com.example.pos.system.exception.UserException;
import com.example.pos.system.mapper.SupplierMapper;
import com.example.pos.system.modal.Store;
import com.example.pos.system.modal.Supplier;
import com.example.pos.system.modal.User;
import com.example.pos.system.payload.dto.SupplierDTO;
import com.example.pos.system.repository.StoreRepository;
import com.example.pos.system.repository.SupplierRepository;
import com.example.pos.system.service.SupplierService;
import com.example.pos.system.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SupplierServiceImpl implements SupplierService {

    private final SupplierRepository supplierRepository;
    private final StoreRepository storeRepository;
    private final UserService userService;

    @Override
    public SupplierDTO createSupplier(SupplierDTO dto) throws Exception {

        User user = userService.getCurrentUser();

        Store store = storeRepository.findById(dto.getStoreId())
                .orElseThrow(() -> new Exception("Store not found"));

        checkAuthority(user, store);

        if (supplierRepository.existsByPhone(dto.getPhone())) {
            throw new Exception("Phone already exists");
        }

        if (dto.getEmail() != null &&
                !dto.getEmail().isBlank() &&
                supplierRepository.existsByEmail(dto.getEmail())) {

            throw new Exception("Email already exists");
        }

        Supplier supplier = SupplierMapper.toEntity(dto, store);

        return SupplierMapper.toDTO(
                supplierRepository.save(supplier)
        );
    }

    @Override
    public SupplierDTO updateSupplier(Long id, SupplierDTO dto) throws Exception {

        User user = userService.getCurrentUser();

        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new Exception("Supplier not found"));

        checkAuthority(user, supplier.getStore());

        supplier.setName(dto.getName());
        supplier.setContactPerson(dto.getContactPerson());
        supplier.setPhone(dto.getPhone());
        supplier.setEmail(dto.getEmail());
        supplier.setGstNumber(dto.getGstNumber());
        supplier.setAddress(dto.getAddress());

        return SupplierMapper.toDTO(
                supplierRepository.save(supplier)
        );
    }

    @Override
    public void deleteSupplier(Long id) throws Exception {

        User user = userService.getCurrentUser();

        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new Exception("Supplier not found"));

        checkDeleteAuthority(user, supplier.getStore());

        supplierRepository.delete(supplier);
    }

    @Override
    public List<SupplierDTO> getAllSuppliers() throws Exception {

        User user = userService.getCurrentUser();

        return getAllSuppliers(user);
    }

    @Override
    public List<SupplierDTO> getAllSuppliers(User user) {

        List<Supplier> suppliers;

        if (user.getRole() == UserRole.ROLE_SUPER_ADMIN
                || user.getRole() == UserRole.ROLE_INVENTORY_MANAGER) {

            suppliers = supplierRepository.findAll();

        } else {

            suppliers = supplierRepository.findByStoreId(
                    user.getStore().getId()
            );
        }

        return suppliers.stream()
                .map(SupplierMapper::toDTO)
                .toList();
    }

    @Override
    public SupplierDTO getSupplierById(Long id) throws Exception {

        User user = userService.getCurrentUser();

        return getSupplierById(id, user);
    }

    @Override
    public SupplierDTO getSupplierById(Long id, User user) throws Exception {

        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new Exception("Supplier not found"));

        checkAuthority(user, supplier.getStore());

        return SupplierMapper.toDTO(supplier);
    }

    // Used for CREATE and UPDATE — Inventory Manager gets full access
    private void checkAuthority(User user, Store store) throws Exception {

        if (user.getRole() == UserRole.ROLE_SUPER_ADMIN) {
            return;
        }

        if (user.getRole() == UserRole.ROLE_INVENTORY_MANAGER) {
            return;
        }

        if (user.getRole() == UserRole.ROLE_STORE_ADMIN) {

            if (user.getStore() != null &&
                    user.getStore().getId().equals(store.getId())) {
                return;
            }
        }

        throw new UserException(
                "You don't have permission to manage this supplier."
        );
    }

    // Used for DELETE only — Inventory Manager is NOT allowed
    private void checkDeleteAuthority(User user, Store store) throws Exception {

        if (user.getRole() == UserRole.ROLE_SUPER_ADMIN) {
            return;
        }

        if (user.getRole() == UserRole.ROLE_STORE_ADMIN) {

            if (user.getStore() != null &&
                    user.getStore().getId().equals(store.getId())) {
                return;
            }
        }

        throw new UserException(
                "You don't have permission to delete this supplier."
        );
    }
}