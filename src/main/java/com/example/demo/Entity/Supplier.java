package com.example.demo.Entity;
import jakarta.persistence.*;
import lombok.*;
@Entity
@Table(name = "suppliers")
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Supplier extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String code;

    @Column(nullable = false)
    private String name; // Tên công ty/Cửa hàng đối tác

    private String taxCode; // Mã số thuế (Để xuất hóa đơn VAT)

    private String phone; // Số hotline/Zalo liên hệ

    private String email; // Nhận báo giá/Hóa đơn điện tử

    private String address; // Địa chỉ xuất hóa đơn/Kho hàng

    private String contactName; // Tên người phụ trách (Sale/Kỹ thuật)

    @Enumerated(EnumType.STRING)
    private SupplierType type; // GOODS (Hàng), SERVICE (Sửa thuê), BOTH (Cả hai)

    @Column(columnDefinition = "TEXT")
    private String note; // Ghi chú đặc thù (VD: Chiết khấu 5%, chuyên main laptop...)

    private Boolean active = true; // Trạng thái hợp tác
}
