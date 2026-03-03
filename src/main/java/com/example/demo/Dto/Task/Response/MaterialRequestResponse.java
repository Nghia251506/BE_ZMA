package com.example.demo.Dto.Task.Response;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor // Cần cái này nếu ông dùng Query "SELECT NEW..." trong Repository
@NoArgsConstructor
public class MaterialRequestResponse {
    private Long requestId;
    private String taskCode;
    private String taskTitle;

    // --- BỔ SUNG THÊM ---
    private Long leaderId;        // Để FE có thể click vào xem Profile ông Leader
    private String leaderName;
    private String leaderMaNV;

    private Long productId;
    private String productName;
    private String productCode;
    private String unit;          // Đơn vị tính: Mét, Cái, Bộ (Admin nhìn phát biết ngay mượn theo đơn vị gì)

    private Double borrowQuantity;
    private Double usedQuantity;
    private Double returnQuantity;

    private String status;

    // --- BỔ SUNG THÊM ---
    private Long warehouseId;     // ID kho thực hiện xuất/nhập (nếu đã EXPENDED)
    private String warehouseName; // Tên kho (Để biết đồ này từ kho nào ra)

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // TRUY VẾT NGƯỜI DUYỆT (Nếu ông đã dùng BaseEntity/Audit)
    private String approvedByName; // Tên Admin đã bấm nút "Duyệt"
    private String requesterName;
    private String requesterMaNV;
}