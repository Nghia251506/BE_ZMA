package com.example.demo.Repository;

import com.example.demo.Entity.ProductAttributeValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductAttributeValueRepository extends JpaRepository<ProductAttributeValue, Long> {
    void deleteByProductId(Long productId); // Dùng khi update sản phẩm
}
