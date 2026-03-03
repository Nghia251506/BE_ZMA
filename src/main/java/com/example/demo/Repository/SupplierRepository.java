package com.example.demo.Repository;

import com.example.demo.Entity.Supplier;
import com.example.demo.Entity.SupplierType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {

    @Procedure(procedureName = "GetNextSupplierCode")
    String getNextSupplierCode();
    // Phải là find + [Tên thuộc tính] + [Điều kiện]
    Page<Supplier> findByNameContainingIgnoreCase(String name, Pageable pageable);

    // Tương tự với hàm Type (nếu có)
    Page<Supplier> findByType(SupplierType type, Pageable pageable);

    // Lọc các NCC chưa bị xóa
    Page<Supplier> findByDeletedAtIsNull(Pageable pageable);

    // Query tìm kiếm phân trang theo Tên, Code hoặc SĐT
    @Query("SELECT s FROM Supplier s WHERE " +
            "(:keyword IS NULL OR LOWER(s.name) LIKE LOWER(CONCAT('%', :keyword, '%'))) OR " +
            "(:keyword IS NULL OR LOWER(s.code) LIKE LOWER(CONCAT('%', :keyword, '%'))) OR " +
            "(:keyword IS NULL OR s.phone LIKE CONCAT('%', :keyword, '%'))")
    Page<Supplier> searchSuppliers(@Param("keyword") String keyword, Pageable pageable);
}