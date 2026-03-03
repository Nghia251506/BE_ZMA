package com.example.demo.Repository;

import com.example.demo.Dto.Customer.CustomerResponseDto;
import com.example.demo.Entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long>, JpaSpecificationExecutor<Customer> {

    @Query("SELECT new com.example.demo.Dto.Customer.CustomerResponseDto(" +
            "c.id, " +                               // 1
            "c.customerCode, " +                     // 2
            "COALESCE(c.fullName, u.fullName), " +   // 3
            "c.mainPhone, " +                        // 4
            "c.subPhone, " +                         // 5 - THÊM VÀO ĐÂY
            "COALESCE(c.email, u.email), " +         // 6
            "cg.maNV, " +                            // 7
            "ans.fullName, " +                       // 8
            "crt.fullName, " +                       // 9
            "c.createdAt, " +                        // 10
            "c.company, " +                          // 11
            "c.address) " +                          // 12
            "FROM Customer c " +
            "LEFT JOIN c.user u " +
            "LEFT JOIN c.customerGroupUser cg " +
            "LEFT JOIN c.assignee ans " +
            "LEFT JOIN c.creator crt " +
            "WHERE (:search IS NULL OR " +
            "      LOWER(COALESCE(c.fullName, u.fullName)) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "      LOWER(c.customerCode) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "      c.mainPhone LIKE CONCAT('%', :search, '%') OR " + // Tìm kiếm theo mainPhone
            "      c.subPhone LIKE CONCAT('%', :search, '%')) " +    // Tìm kiếm theo subPhone
            "ORDER BY c.createdAt DESC")
    Page<CustomerResponseDto> findAllWithSearch(@Param("search") String search, Pageable pageable);

    // Tìm nhanh theo mã khách hàng
    Optional<Customer> findByCustomerCode(String customerCode);

    // Đếm số khách hàng của một nhân viên phụ trách
    long countByAssignee_Id(Long assigneeId);
}
