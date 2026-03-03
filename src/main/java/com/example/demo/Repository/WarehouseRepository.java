package com.example.demo.Repository;

import com.example.demo.Entity.Warehouse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WarehouseRepository extends JpaRepository<Warehouse, Long>, JpaSpecificationExecutor<Warehouse> {

    // Tìm tất cả kho đang hoạt động (Dùng để khởi tạo Inventory khi tạo SP mới)
    List<Warehouse> findAllByActiveTrue();

    @Procedure(procedureName = "sp_generate_warehouse_code")
    String generateWarehouseCode(@Param("p_address") String address);

    // Search cơ bản (Nếu không muốn dùng Specification cho đơn giản)
    @Query("SELECT w FROM Warehouse w WHERE " +
            "(:keyword IS NULL OR LOWER(w.name) LIKE LOWER(CONCAT('%', :keyword, '%'))) OR " +
            "(:keyword IS NULL OR LOWER(w.code) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Warehouse> searchWarehouses(@Param("keyword") String keyword, Pageable pageable);
}
