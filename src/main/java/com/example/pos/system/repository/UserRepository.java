package com.example.pos.system.repository;

import com.example.pos.system.domain.UserRole;
import com.example.pos.system.modal.Store;
import com.example.pos.system.modal.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByEmail(String email);

    boolean existsByRole(UserRole role);

    List<User> findByStore(Store store);

    List<User> findByBranchId(Long branchId);

    List<User> findByStoreId(Long storeId);

    List<User> findByRole(UserRole role);

    @Query("""
SELECT COUNT(u)
FROM User u
WHERE u.role IN (
    com.example.pos.system.domain.UserRole.ROLE_BRANCH_MANAGER,
    com.example.pos.system.domain.UserRole.ROLE_BRANCH_CASHIER,
    com.example.pos.system.domain.UserRole.ROLE_STORE_ADMIN
)
""")
    Long countEmployees();

    Optional<User> findFirstByRole(UserRole role);
}