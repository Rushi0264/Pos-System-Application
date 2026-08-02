package com.example.pos.system.repository;

import com.example.pos.system.modal.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    @Query("SELECT COALESCE(SUM(i.quantity), 0) FROM Inventory i WHERE i.branch.id = :branchId")
    long getTotalStockByBranch(@Param("branchId") Long branchId);

    List<Inventory> findTop10ByOrderByLastUpdateDesc();

    List<Inventory> findByProductId(Long id);

    @Query("""
    SELECT i FROM Inventory i
    LEFT JOIN FETCH i.product
    LEFT JOIN FETCH i.branch
    WHERE i.branch.store.id = :storeId AND i.quantity <= 30
    ORDER BY i.quantity ASC
""")
    List<Inventory> findLowStockListByStore(@Param("storeId") Long storeId);

    @Query("SELECT COUNT(i) FROM Inventory i WHERE i.branch.store.id = :storeId AND i.quantity <= 10")
    Long getLowStockProductsByStore(@Param("storeId") Long storeId);

    List<Inventory> findTop10ByBranch_Store_IdOrderByLastUpdateDesc(Long storeId);
}
