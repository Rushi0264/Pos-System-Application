package com.example.pos.system.service;

import com.example.pos.system.modal.User;
import com.example.pos.system.payload.dto.ProductDTO;

import java.util.List;

public interface ProductService {

    ProductDTO createProduct(ProductDTO productDTO, User user) throws Exception;
    ProductDTO updateProduct(Long id, ProductDTO productDTO, User user) throws Exception;
    void deleteProduct(Long id, User user) throws Exception;
    List<ProductDTO> getProductsByStoreId(Long storeId);
    List<ProductDTO> searchByKeyword(Long storeId, String keyword);
    ProductDTO getProductById(Long id) throws Exception;
    List<ProductDTO> getAllProducts() throws Exception;

    List<ProductDTO> getProductsByStoreId(Long storeId, User user) throws Exception;

    ProductDTO getProductById(Long id, User user) throws Exception;

    List<ProductDTO> searchByKeyword(Long storeId, String keyword, User user) throws Exception;

    List<ProductDTO> getAllProducts(User user) throws Exception;

}
