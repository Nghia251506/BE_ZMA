package com.example.demo.Repository;

import com.example.demo.Entity.Inventory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long>, JpaSpecificationExecutor<Inventory> {

    // Tìm chính xác vị trí của 1 SP trong 1 Kho
    Optional<Inventory> findByProductIdAndWarehouseId(Long productId, Long warehouseId);

    // Lấy danh sách tồn kho của 1 sản phẩm cụ thể (Để hiện danh sách: Kho A: 10, Kho B: 5...)
    @EntityGraph(attributePaths = {"warehouse"})
    List<Inventory> findByProductId(Long productId);

    // Lấy danh sách sản phẩm trong 1 kho cụ thể (Dùng cho chức năng kiểm kho chi nhánh)
    @EntityGraph(attributePaths = {"product"})
    Page<Inventory> findByWarehouseId(Long warehouseId, Pageable pageable);

    void deleteByProductId(Long id);

    // Soi hàng theo kho (Dùng cho InventoryResponse)
    @Query("SELECT i FROM Inventory i " +
            "JOIN FETCH i.product " +
            "JOIN FETCH i.warehouse " +
            "WHERE (:warehouseId IS NULL OR i.warehouse.id = :warehouseId) " +
            "AND (:productId IS NULL OR i.product.id = :productId)")
    List<Inventory> findByWarehouseAndProduct(
            @Param("warehouseId") Long warehouseId,
            @Param("productId") Long productId
    );

    // Tìm chính xác bản ghi trong kho để cập nhật số lượng khi Duyệt mượn/trả
    Optional<Inventory> findByWarehouseIdAndProductId(Long warehouseId, Long productId);
}
