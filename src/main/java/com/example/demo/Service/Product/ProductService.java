package com.example.demo.Service.Product;

import com.example.demo.Dto.Product.ProductRequest;
import com.example.demo.Dto.Product.ProductResponse;
import com.example.demo.Entity.*;
import com.example.demo.Mapper.ProductMapper;
import com.example.demo.Repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final ProductAttributeValueRepository attributeValueRepository;
    private final ProductAttributeRepository attributeRepository;
    private final InventoryRepository inventoryRepository;
    private final WarehouseRepository warehouseRepository;

    /**
     * CREATE: Tạo sản phẩm "All-in-one"
     */
    @Transactional
    public ProductResponse createProduct(ProductRequest request) {
        // 1. Sinh mã SKU tự động
        String sku = productRepository.generateProductSKU();

        // 2. Map và lưu Product cơ bản
        Product product = productMapper.toEntity(request);
        product.setSku(sku);
        Product savedProduct = productRepository.save(product);

        // 3. Lưu Attribute Values (RAM, BUS, Dung lượng...)
        if (request.getAttributes() != null && !request.getAttributes().isEmpty()) {
            List<ProductAttributeValue> attrValues = request.getAttributes().stream()
                    .map(attrReq -> {
                        ProductAttributeValue val = new ProductAttributeValue();
                        val.setProduct(savedProduct);
                        val.setAttribute(attributeRepository.getReferenceById(attrReq.getAttributeId()));
                        val.setValue(attrReq.getValue());
                        return val;
                    }).collect(Collectors.toList());
            attributeValueRepository.saveAll(attrValues);
            savedProduct.setAttributeValues(attrValues);
        }

        // 4. Khởi tạo tồn kho cho TẤT CẢ các kho
        List<Warehouse> allWarehouses = warehouseRepository.findAllByActiveTrue();
        List<Inventory> inventories = allWarehouses.stream().map(warehouse -> {
            Inventory inv = new Inventory();
            inv.setProduct(savedProduct);
            inv.setWarehouse(warehouse);

            // Nếu là kho chọn lúc tạo -> gán số lượng ban đầu, còn lại = 0
            if (warehouse.getId().equals(request.getWarehouseId())) {
                inv.setQuantity(request.getInitialQuantity() != null ? request.getInitialQuantity() : 0);
                inv.setBinLocation(request.getBinLocation());
            } else {
                inv.setQuantity(0);
            }
            return inv;
        }).collect(Collectors.toList());

        inventoryRepository.saveAll(inventories);
        savedProduct.setInventories(inventories);

        return productMapper.toResponse(savedProduct);
    }

    /**
     * READ: Search có phân trang và dùng EntityGraph để tối ưu
     */
    @Transactional(readOnly = true)
    public Page<ProductResponse> search(String keyword, Pageable pageable) {
        return productRepository.searchProducts(keyword, pageable)
                .map(productMapper::toResponse);
    }

    /**
     * READ: Lấy chi tiết 1 sản phẩm
     */
    @Transactional(readOnly = true)
    public ProductResponse getById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại với ID: " + id));
        return productMapper.toResponse(product);
    }

    /**
     * UPDATE: Cập nhật thông tin và thuộc tính
     */
    @Transactional
    public ProductResponse updateProduct(Long id, ProductRequest request) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại"));

        // Cập nhật các trường cơ bản (không đè SKU và ID)
        productMapper.updateProductFromRequest(request, existingProduct);

        // Cập nhật Attribute Values (Xóa cũ - Thêm mới để đồng bộ)
        if (request.getAttributes() != null) {
            attributeValueRepository.deleteByProductId(id); // Phải viết thêm hàm này trong Repo
            List<ProductAttributeValue> newAttrs = request.getAttributes().stream()
                    .map(attrReq -> {
                        ProductAttributeValue val = new ProductAttributeValue();
                        val.setProduct(existingProduct);
                        val.setAttribute(attributeRepository.getReferenceById(attrReq.getAttributeId()));
                        val.setValue(attrReq.getValue());
                        return val;
                    }).collect(Collectors.toList());
            attributeValueRepository.saveAll(newAttrs);
            existingProduct.setAttributeValues(newAttrs);
        }

        return productMapper.toResponse(productRepository.save(existingProduct));
    }

    /**
     * DELETE: Xóa sản phẩm và các dữ liệu liên quan
     */
    @Transactional
    public void delete(Long id) {
        // Xóa Inventory và AttributeValue trước để tránh lỗi Foreign Key (nếu không dùng Cascade)
        inventoryRepository.deleteByProductId(id);
        attributeValueRepository.deleteByProductId(id);
        productRepository.deleteById(id);
    }
}