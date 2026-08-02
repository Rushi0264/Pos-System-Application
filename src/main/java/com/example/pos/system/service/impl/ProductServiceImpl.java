package com.example.pos.system.service.impl;

import com.example.pos.system.mapper.ProductMapper;
import com.example.pos.system.modal.Category;
import com.example.pos.system.modal.Inventory;
import com.example.pos.system.modal.Product;
import com.example.pos.system.modal.Store;
import com.example.pos.system.modal.User;
import com.example.pos.system.domain.UserRole;
import com.example.pos.system.payload.dto.BranchStockDTO;
import com.example.pos.system.payload.dto.ProductDTO;
import com.example.pos.system.repository.*;
import com.example.pos.system.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.example.pos.system.repository.StockMovementRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final StoreRepository storeRepository;
    private final CategoryRepository categoryRepository;
    private final BranchRepository branchRepository;
    private final InventoryRepository inventoryRepository;
    private final StockMovementRepository stockMovementRepository;


    private boolean hasFullAccess(User user) {
        return user.getRole().name().equals("ROLE_SUPER_ADMIN")
                || user.getRole().name().equals("ROLE_INVENTORY_MANAGER");
    }

    @Override
    public ProductDTO createProduct(ProductDTO productDTO, User user) throws Exception {
        Store store = storeRepository.findById(
                productDTO.getStoreId()
        ).orElseThrow(
                () -> new Exception("Store not found..")
        );

        Category category = categoryRepository.findById(productDTO.getCategoryId()).orElseThrow(
                ()-> new Exception("category not found..")
        );

        Product product = ProductMapper.toEntity(productDTO, store, category);
        Product savedProduct = productRepository.save(product);

        // Note: No Inventory record is created here.
        // Stock is added later via the Purchase flow (or a dedicated Add Stock screen),
        // which will create the Inventory entry for the relevant branch + quantity.

        return attachStock(ProductMapper.toDTO(savedProduct), null);
    }

    @Override
    public ProductDTO updateProduct(Long id, ProductDTO productDTO, User user) throws Exception {
        Product product = productRepository.findById(id).orElseThrow(
                () -> new Exception("Product not found")
        );

        product.setName(productDTO.getName());
        product.setDescription(productDTO.getDescription());
        product.setSku(productDTO.getSku());

        product.setMrp(productDTO.getMrp());
        product.setSellingPrice(productDTO.getSellingPrice());
        product.setBrand(productDTO.getBrand());
        product.setImage(productDTO.getImage());
        product.setUpdatedAt(LocalDateTime.now());


        if (productDTO.getCategoryId()!=null) {
            Category category = categoryRepository.findById(productDTO.getCategoryId()).orElseThrow(
                    () -> new Exception("category not found..")
            );
            product.setCategory(category);
        }

        Product saveProduct = productRepository.save(product);
        return attachStock(ProductMapper.toDTO(saveProduct), null);
    }

    @Override
    public void deleteProduct(Long id, User user) throws Exception {
        Product product = productRepository.findById(id).orElseThrow(
                () -> new Exception("product not found..")
        );

        List<Inventory> inventories = inventoryRepository.findByProductId(id);
        inventoryRepository.deleteAll(inventories);

        productRepository.delete(product);
    }

    @Override
    public ProductDTO getProductById(Long id) throws Exception {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new Exception("Product not found"));

        return attachStock(ProductMapper.toDTO(product), null);
    }

    @Override
    public List<ProductDTO> getProductsByStoreId(Long storeId) {
        List<Product> products = productRepository.findByStoreId(storeId);
        return attachStock(
                products.stream()
                        .map(ProductMapper::toDTO)
                        .collect(Collectors.toList()),
                null
        );
    }

    @Override
    public List<ProductDTO> searchByKeyword(Long storeId, String keyword) {
        List<Product> products = productRepository.searchByKeyword(storeId, keyword);
        return attachStock(
                products.stream()
                        .map(ProductMapper::toDTO)
                        .collect(Collectors.toList()),
                null
        );
    }

    @Override
    public List<ProductDTO> getAllProducts() throws Exception {

        return attachStock(
                productRepository.findAll()
                        .stream()
                        .map(ProductMapper::toDTO)
                        .toList(),
                null
        );
    }

    @Override
    public List<ProductDTO> getProductsByStoreId(Long storeId, User user) throws Exception {

        if (!hasFullAccess(user) &&
                user.getStore() != null &&
                !user.getStore().getId().equals(storeId)) {

            throw new Exception("Access Denied");
        }

        return attachStock(
                productRepository.findByStoreId(storeId)
                        .stream()
                        .map(ProductMapper::toDTO)
                        .toList(),
                user
        );
    }

    @Override
    public ProductDTO getProductById(Long id, User user) throws Exception {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new Exception("Product not found"));

        if (!hasFullAccess(user) &&
                user.getStore() != null &&
                !product.getStore().getId().equals(user.getStore().getId())) {

            throw new Exception("Access Denied");
        }

        return attachStock(ProductMapper.toDTO(product), user);
    }

    @Override
    public List<ProductDTO> searchByKeyword(Long storeId,
                                            String keyword,
                                            User user) throws Exception {

        if (!hasFullAccess(user) &&
                user.getStore() != null &&
                !user.getStore().getId().equals(storeId)) {

            throw new Exception("Access Denied");
        }

        return attachStock(
                productRepository.searchByKeyword(storeId, keyword)
                        .stream()
                        .map(ProductMapper::toDTO)
                        .toList(),
                user
        );
    }

    @Override
    public List<ProductDTO> getAllProducts(User user) throws Exception {

        if (hasFullAccess(user)) {

            return attachStock(
                    productRepository.findAll()
                            .stream()
                            .map(ProductMapper::toDTO)
                            .toList(),
                    user
            );
        }

        if (user.getStore() == null) {
            throw new Exception("Store not assigned");
        }

        return attachStock(
                productRepository.findByStoreId(user.getStore().getId())
                        .stream()
                        .map(ProductMapper::toDTO)
                        .toList(),
                user
        );
    }

    // ===========================================================
    // Helper: attach total stock (sum across all branches) to DTOs
    // ===========================================================

    private ProductDTO attachStock(ProductDTO dto, User user) {

        boolean isBranchManager =
                user != null
                        && user.getRole() == UserRole.ROLE_BRANCH_MANAGER
                        && user.getBranch() != null;

        Integer branchTotal;

        if (isBranchManager) {

            branchTotal = inventoryRepository.findAll().stream()
                    .filter(inv -> inv.getProduct() != null
                            && inv.getProduct().getId().equals(dto.getId())
                            && inv.getBranch() != null
                            && inv.getBranch().getId().equals(user.getBranch().getId()))
                    .mapToInt(Inventory::getQuantity)
                    .sum();

        } else {

            branchTotal = inventoryRepository.findAll().stream()
                    .filter(inv -> inv.getProduct() != null
                            && inv.getProduct().getId().equals(dto.getId()))
                    .mapToInt(Inventory::getQuantity)
                    .sum();
        }

        dto.setBranchStock(branchTotal);

        if (!isBranchManager) {

            List<BranchStockDTO> breakdown = inventoryRepository.findAll().stream()
                    .filter(inv -> inv.getProduct() != null
                            && inv.getProduct().getId().equals(dto.getId())
                            && inv.getQuantity() != null
                            && inv.getQuantity() > 0)
                    .map(inv -> BranchStockDTO.builder()
                            .branchId(inv.getBranch().getId())
                            .branchName(inv.getBranch().getName())
                            .quantity(inv.getQuantity())
                            .build())
                    .toList();

            dto.setBranchBreakdown(breakdown);
        }

        Integer storeTotal = 0;

        if (dto.getStoreId() != null) {
            try {
                Integer result = stockMovementRepository.getStoreStock(
                        dto.getStoreId(), dto.getId()
                );
                storeTotal = result != null ? result : 0;
            } catch (Exception ignored) {
            }
        }

        dto.setStoreStock(storeTotal);
        dto.setTotalStock(branchTotal + storeTotal);

        return dto;
    }

    private List<ProductDTO> attachStock(List<ProductDTO> dtos, User user) {
        dtos.forEach(d -> attachStock(d, user));
        return dtos;
    }
}