package com.example.pos.system.service;

import com.example.pos.system.exception.UserException;
import com.example.pos.system.modal.User;
import com.example.pos.system.payload.dto.BranchDTO;

import java.util.List;

public interface BranchService {

    BranchDTO createBranch(BranchDTO branchDTO) throws UserException;
    BranchDTO updateBranch(Long id, BranchDTO branchDTO) throws Exception;
    void deleteBranch(Long id) throws Exception;
    List<BranchDTO> getAllBranchesByStoreId(Long storeId);
    BranchDTO getBranchById(Long id) throws Exception;

}
