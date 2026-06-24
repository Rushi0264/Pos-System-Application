package com.example.pos.system.service;

import com.example.pos.system.exception.UserException;
import com.example.pos.system.payload.dto.CategoryDTO;

import java.util.List;

public interface CategoryService {

    CategoryDTO createCategory(CategoryDTO dto) throws Exception;

    List<CategoryDTO> getCategoriesByStore(Long storeId);

    CategoryDTO updateCategory(Long id, CategoryDTO dto) throws Exception;

    void deleteCategory(Long id) throws Exception;
}
