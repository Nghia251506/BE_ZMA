package com.example.demo.Service.Customer;

import com.example.demo.Dto.Customer.CustomerCreateRequest;
import com.example.demo.Dto.Customer.CustomerResponseDto;
import com.example.demo.Dto.Customer.CustomerUpdateRequest;
import com.example.demo.Entity.Customer;
import com.example.demo.Entity.User;
import com.example.demo.Mapper.CustomerMapper;
import com.example.demo.Repository.CustomerRepository;
import com.example.demo.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;
    private final CustomerMapper customerMapper;

    // 1. Lấy danh sách phân trang & tìm kiếm (Dùng thẳng Query DTO từ Repo cho nhanh)
    public Page<CustomerResponseDto> getAllCustomers(String search, Pageable pageable) {
        return customerRepository.findAllWithSearch(search, pageable);
    }

    // 2. Tạo mới khách hàng
    @Transactional
    public CustomerResponseDto createCustomer(CustomerCreateRequest request) {
        // Chuyển DTO sang Entity thông qua MapStruct
        Customer customer = customerMapper.toEntity(request);

        // Tự động sinh mã khách hàng nếu FE không gửi lên
        if (customer.getCustomerCode() == null || customer.getCustomerCode().isEmpty()) {
            customer.setCustomerCode("CUS-" + System.currentTimeMillis());
        }

        // Lấy User hiện tại đang đăng nhập để gán làm người tạo (Creator)
        // Lưu ý: Ông cần ép kiểu tùy theo cách ông lưu Principal trong Security
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        userRepository.findByUsername(username).ifPresent(customer::setCreator);

        // Liên kết các Object liên quan dựa trên ID từ Request
        mapRelations(customer, request.getUserId(), request.getAssigneeId(), request.getGroupUserMaNV());

        Customer savedCustomer = customerRepository.save(customer);
        return customerMapper.toResponseDto(savedCustomer);
    }

    // 3. Cập nhật khách hàng
    @Transactional
    public CustomerResponseDto updateCustomer(Long id, CustomerUpdateRequest request) {
        Customer existingCustomer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng ID: " + id));

        // MapStruct cập nhật các trường từ Request vào Entity hiện tại
        customerMapper.updateEntityFromRequest(request, existingCustomer);

        // Cập nhật lại các mối quan hệ
        mapRelations(existingCustomer, request.getUserId(), request.getAssigneeId(), request.getGroupUserMaNV());

        return customerMapper.toResponseDto(customerRepository.save(existingCustomer));
    }

    // Hàm phụ trợ để map các liên kết User
    private void mapRelations(Customer customer, Long userId, Long assigneeId, String groupMaNV) {
        if (userId != null) {
            userRepository.findById(userId).ifPresent(customer::setUser);
        }
        if (assigneeId != null) {
            userRepository.findById(assigneeId).ifPresent(customer::setAssignee);
        }
        if (groupMaNV != null) {
            userRepository.findByMaNV(groupMaNV).ifPresent(customer::setCustomerGroupUser);
        }
    }

    // 4. Xóa khách hàng
    @Transactional
    public void deleteCustomer(Long id) {
        if (!customerRepository.existsById(id)) {
            throw new RuntimeException("Khách hàng không tồn tại");
        }
        customerRepository.deleteById(id);
    }
}
