package com.example.demo.Service.Warehouse;

import com.example.demo.Entity.Inventory;
import com.example.demo.Repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    /**
     * Chuyển hàng giữa các kho
     * Request gồm: productId, fromWarehouseId, toWarehouseId, quantity
     */
    @Transactional
    public void transferProduct(Long productId, Long fromWarehouseId, Long toWarehouseId, Integer quantity) {
        if (fromWarehouseId.equals(toWarehouseId)) {
            throw new RuntimeException("Kho gửi và kho nhận phải khác nhau!");
        }

        // 1. Lấy bản ghi tồn kho ở kho gửi
        Inventory fromInv = inventoryRepository.findByWarehouseIdAndProductId(fromWarehouseId, productId)
                .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại trong kho gửi"));

        // 2. Kiểm tra số lượng có đủ để chuyển không
        if (fromInv.getQuantity() < quantity) {
            throw new RuntimeException("Số lượng trong kho gửi không đủ (Hiện có: " + fromInv.getQuantity() + ")");
        }

        // 3. Lấy bản ghi tồn kho ở kho nhận (Nếu chưa có thì phải khởi tạo)
        Inventory toInv = inventoryRepository.findByWarehouseIdAndProductId(toWarehouseId, productId)
                .orElseThrow(() -> new RuntimeException("Sản phẩm chưa được cấu hình tại kho nhận"));

        // 4. Thực hiện trừ bên gửi, cộng bên nhận
        fromInv.setQuantity(fromInv.getQuantity() - quantity);
        toInv.setQuantity(toInv.getQuantity() + quantity);

        // 5. Lưu lại
        inventoryRepository.save(fromInv);
        inventoryRepository.save(toInv);

        // (Optional) Lưu thêm vào bảng StockHistory để sau này còn biết ai chuyển, lúc nào
    }
}
