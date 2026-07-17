package com.example.pos.system.repository;

import com.example.pos.system.modal.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    Inventory findByProductIdAndBranchId(Long productId, Long branchId);
    List<Inventory> findByBranchId(Long branchId);
    List<Inventory> findByBranchStoreId(Long storeId);
    boolean existsByProductIdAndBranchId(Long productId, Long branchId);

    @Query("""
SELECT COUNT(i)
FROM Inventory i
WHERE i.quantity <= 10
""")
    Long getLowStockProducts();



    @Query("""
SELECT i FROM Inventory i
LEFT JOIN FETCH i.product
LEFT JOIN FETCH i.branch
WHERE i.quantity <= 30
ORDER BY i.quantity ASC
""")
    List<Inventory> findLowStockList();
}
