package com.example.pos.system.controller;

import com.example.pos.system.exception.UserException;
import com.example.pos.system.payload.dto.BranchDTO;
import com.example.pos.system.service.BranchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/branches")
public class BranchController {
    private final BranchService branchService;

    public ResponseEntity<BranchDTO> createBranch(
            @RequestBody BranchDTO branchDTO
    ) throws UserException {
        BranchDTO createdBranch = branchService.createBranch(branchDTO, null);
        return ResponseEntity.ok(createdBranch);
    }
}
