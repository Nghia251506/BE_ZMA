package com.example.demo.Repository;

import com.example.demo.Entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    @Procedure(procedureName = "GenerateProductSKU")
    String generateProductSKU();

    /**
     * EntityGraph này là "vũ khí" tối thượng để UI không bị lag.
     * Nó sẽ hốt luôn: Supplier, Category, Inventories và cả các Attribute Values kèm tên Attribute của chúng.
     */
    @EntityGraph(attributePaths = {"supplier", "category"})
    @Query("SELECT p FROM Product p WHERE " +
            ":keyword IS NULL OR " +
            "p.sku LIKE CONCAT(:keyword, '%') OR " + // Ưu tiên tìm từ đầu mã SKU để ăn Index
            "p.barcode = :keyword OR " +             // Tìm chính xác Barcode (Cực nhanh vì có Index)
            "p.name LIKE CONCAT('%', :keyword, '%')") // Chấp nhận Table Scan cho Name vì tính tiện dụng
    Page<Product> searchProducts(@Param("keyword") String keyword, Pageable pageable);

    /**
     * Dùng cho trang chi tiết sản phẩm, cũng cần đầy đủ thông tin để map sang Response
     */
    @EntityGraph(attributePaths = {
            "supplier", "category", "inventories", "inventories.warehouse",
            "attributeValues", "attributeValues.attribute"
    })
    Optional<Product> findById(Long id);

    // Tìm theo Supplier
    Page<Product> findBySupplierId(Long supplierId, Pageable pageable);

    // Tìm theo Category
    Page<Product> findByCategoryId(Long categoryId, Pageable pageable);
}