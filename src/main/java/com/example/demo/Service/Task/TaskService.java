package com.example.demo.Service.Task;

import com.example.demo.Dto.Auth.UserResponseDTO;
import com.example.demo.Dto.Customer.CustomerResponseDto;
import com.example.demo.Dto.Task.Request.*;
import com.example.demo.Dto.Task.Response.*;
import com.example.demo.Entity.*;
import com.example.demo.Mapper.TaskLaborMapper;
import com.example.demo.Mapper.TaskMapper;
import com.example.demo.Repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class TaskService {

    private final TaskRepository taskRepository;
    private final TaskMemberRepository memberRepository;
    private final TaskLaborRepository laborRepository;
    private final TaskProductRequestRepository productRequestRepository;
    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository; // Cần repository này để xử lý kho
    private final UserRepository userRepository; // Để lấy thông tin User
    private final TaskMapper taskMapper;
    private final TaskLaborMapper laborMapper;
    private final CustomerRepository  customerRepository;

    // ==========================================
    // 1. CRUD & XEM CHI TIẾT (Giải quyết vấn đề ProductName, Leader, Members bị null)
    // ==========================================

    public TaskDetailResponse create(TaskCreateRequest request) {
        // 1. Map từ DTO sang Entity (Lúc này members đã được khởi tạo ArrayList rỗng nhờ Entity/Mapper fix)
        Task task = taskMapper.toEntity(request);

        // Đảm bảo không bị null sau khi qua Mapper (Lớp bảo hiểm thứ 2)
        if (task.getMembers() == null) {
            task.setMembers(new ArrayList<>());
        }

        task.setStatus(TaskStatus.NEW);

        // 2. Lưu Task trước để lấy ID (Bắt buộc vì TaskMember cần Task ID)
        task = taskRepository.save(task);

        // 3. Lưu Leader vào DB đồng thời add vào Object task hiện tại
        saveMember(task, request.getLeaderId(), true);

        // 4. Lưu Supporters
        if (request.getSupporterIds() != null && !request.getSupporterIds().isEmpty()) {
            for (Long userId : request.getSupporterIds()) {
                // Tránh add trùng nếu supporter trùng ID với Leader
                if (!userId.equals(request.getLeaderId())) {
                    saveMember(task, userId, false);
                }
            }
        }

        // 5. Quan trọng: Flush để đẩy hết dữ liệu xuống DB
        // và dùng chính cái ID đó để gọi getById, đảm bảo logic enrichTaskDetail chạy chuẩn
        taskRepository.flush();

        return getById(task.getId());
    }

    public Page<TaskDetailResponse> getAll(Pageable pageable, TaskStatus status, String keyword) {
        Page<Task> tasks;
        if (status != null) {
            tasks = taskRepository.findByStatus(status, pageable);
        } else if (keyword != null && !keyword.isEmpty()) {
            tasks = taskRepository.findByTaskCodeContainingOrTitleContaining(keyword, keyword, pageable);
        } else {
            tasks = taskRepository.findAll(pageable);
        }
        return tasks.map(this::enrichTaskDetail);
    }

    public TaskDetailResponse getById(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Task ID: " + id));
        return enrichTaskDetail(task);
    }

    // Hàm bổ trợ để đổ dữ liệu từ các bảng liên quan vào DTO
    private TaskDetailResponse enrichTaskDetail(Task task) {
        // 1. Map cơ bản từ Entity sang DTO
        TaskDetailResponse dto = taskMapper.toDetailResponse(task);

        // 2. Map Customer (Thông tin sống còn để Staff liên hệ)
        if (task.getCustomerId() != null) {
            customerRepository.findById(task.getCustomerId()).ifPresent(c -> {
                dto.setCustomer(CustomerResponseDto.builder()
                        .id(c.getId())
                        .fullName(c.getFullName())
                        .mainPhone(c.getMainPhone())
                        .address(c.getAddress())
                        .build());
            });
        }

        // 3. Map Leader
        if (task.getLeaderId() != null) {
            userRepository.findById(task.getLeaderId()).ifPresent(u -> {
                dto.setLeader(mapToUserResponseDTO(u));
            });
        }

        // 4. Map Members
        List<TaskMember> members = task.getMembers() != null ? task.getMembers() : new ArrayList<>();
        dto.setMembers(members.stream()
                .map(m -> userRepository.findById(m.getUserId()).map(this::mapToUserResponseDTO).orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList()));

        // 5. Map Product Requests
        List<TaskProductRequest> productRequests = task.getProductRequests() != null
                ? task.getProductRequests()
                : new ArrayList<>();

        dto.setProductRequests(productRequests.stream().map(req -> {
            TaskProductResponse pDto = taskMapper.toProductResponse(req);
            if (req.getProductId() != null) {
                productRepository.findById(req.getProductId()).ifPresent(p ->
                        pDto.setProductName(p.getName())
                );
                productRepository.findById(req.getProductId()).ifPresent(p ->
                        pDto.setProductCode(p.getSku())
                );
            }
            return pDto;
        }).collect(Collectors.toList()));

        return dto;
    }

    // Hàm bổ trợ để map User đầy đủ thông tin cho Mobile UI
    private UserResponseDTO mapToUserResponseDTO(User u) {
        return UserResponseDTO.builder()
                .id(u.getId())
                .maNV(u.getMaNV())
                .fullName(u.getFullName())
                .avatarZalo(u.getAvatarZalo())
                .position(u.getPosition())
                .mainPhone(u.getMainPhone())
                .zaloId(u.getZaloId())
                .build();
    }
    @Transactional
    public TaskDetailResponse update(Long id, TaskCreateRequest request) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Task ID: " + id));

        // 1. Cập nhật các thông tin cơ bản từ Mapper
        taskMapper.updateEntityFromRequest(request, task);

        // Cập nhật lại Leader nếu thay đổi
        task.setLeaderId(request.getLeaderId());

        // 2. Cập nhật danh sách Member (Supporters)
        // Cách đơn giản nhất: Xóa trắng member cũ của task này và add lại
        memberRepository.deleteByTaskId(id);

        // Lưu lại Leader mới
        saveMember(task, request.getLeaderId(), true);

        // Lưu lại danh sách Supporters mới
        if (request.getSupporterIds() != null) {
            for (Long userId : request.getSupporterIds()) {
                if (!userId.equals(request.getLeaderId())) { // Tránh add Leader 2 lần vào bảng Member
                    saveMember(task, userId, false);
                }
            }
        }

        task = taskRepository.save(task);
        return getById(task.getId());
    }

    public void delete(Long id) {
        taskRepository.deleteById(id);
    }

    // ==========================================
    // 2. GÁN NHÂN SỰ
    // ==========================================

    public void assignSupporters(Long taskId, List<Long> supporterIds) {
        Task task = taskRepository.findById(taskId).orElseThrow();
        for (Long userId : supporterIds) {
            saveMember(task, userId, false);
        }
    }

    private void saveMember(Task task, Long userId, boolean isLeader) {
        TaskMember member = new TaskMember();
        member.setTask(task);
        member.setUserId(userId);
        member.setIsLeader(isLeader);

        // Lưu vào Database
        memberRepository.save(member);

        // ĐỒNG BỘ NGƯỢC LẠI OBJECT: Giúp hàm enrichTaskDetail thấy được member ngay lập tức
        if (task.getMembers() == null) {
            task.setMembers(new ArrayList<>());
        }
        task.getMembers().add(member);
    }

    // ==========================================
    // 3. MƯỢN / TRẢ & DUYỆT KHO (Xử lý bảng Inventory)
    // ==========================================

    public void requestProducts(Long taskId, List<TaskProductRequestDto> products) {
        Task task = taskRepository.findById(taskId).orElseThrow();
        for (TaskProductRequestDto dto : products) {
            TaskProductRequest req = new TaskProductRequest();
            req.setTask(task);
            req.setProductId(dto.getProductId());
            req.setBorrowQuantity(dto.getBorrowQuantity());
            req.setStatus(RequestStatus.REQUESTED);
            productRequestRepository.save(req);
        }
    }

    @Transactional
    public void approveBorrow(Long requestId, Long warehouseId) {
        // Tìm đúng cái Request cụ thể (requestId) chứ không dùng taskId nữa
        TaskProductRequest req = productRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy yêu cầu mượn!"));

        if (req.getStatus() != RequestStatus.REQUESTED) {
            throw new RuntimeException("Yêu cầu này không hợp lệ hoặc đã được duyệt!");
        }

        // Tìm tồn kho của SP tại kho cụ thể
        Inventory inventory = inventoryRepository.findByProductIdAndWarehouseId(req.getProductId(), warehouseId)
                .orElseThrow(() -> new RuntimeException("Sản phẩm không có trong kho được chọn!"));

        if (inventory.getQuantity() < req.getBorrowQuantity()) {
            throw new RuntimeException("Kho không đủ số lượng cho sản phẩm ID: " + req.getProductId());
        }

        // TRỪ TỒN KHO
        inventory.setQuantity(inventory.getQuantity() - req.getBorrowQuantity().intValue());
        inventoryRepository.save(inventory);

        // CẬP NHẬT TRẠNG THÁI & LƯU KHO XUẤT
        req.setStatus(RequestStatus.EXPENDED);
        req.setWarehouseId(warehouseId); // Quan trọng để sau này biết đường mà trả về đúng kho
        productRequestRepository.save(req);
    }

    public void returnProducts(Long taskId, List<TaskProductReturnRequest> returns) {
        for (TaskProductReturnRequest reqDto : returns) {
            TaskProductRequest entity = productRequestRepository.findById(reqDto.getRequestId()).orElseThrow();
            double used = reqDto.getUsedQuantity();
            entity.setUsedQuantity(used);
            entity.setReturnQuantity(entity.getBorrowQuantity() - used);
            entity.setStatus(RequestStatus.RETURNED_PENDING); // Đổi sang chờ duyệt trả
            productRequestRepository.save(entity);
        }
    }

    @Transactional
    public void approveReturn(Long requestId) {
        TaskProductRequest req = productRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy yêu cầu!"));

        if (req.getStatus() != RequestStatus.RETURNED_PENDING) {
            throw new RuntimeException("Yêu cầu chưa báo cáo trả hoặc đã duyệt xong!");
        }

        // CỘNG LẠI TỒN KHO (Tự động lấy warehouseId từ lúc xuất kho để trả về)
        Inventory inventory = inventoryRepository.findByProductIdAndWarehouseId(req.getProductId(), req.getWarehouseId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bản ghi kho gốc!"));

        inventory.setQuantity(inventory.getQuantity() + req.getReturnQuantity().intValue());
        inventoryRepository.save(inventory);

        req.setStatus(RequestStatus.RETURNED);
        productRequestRepository.save(req);
    }

    // ==========================================
    // 4. THI CÔNG & CHỐT TASK (Giữ nguyên logic cũ đã fix)
    // ==========================================

    public void checkIn(TaskCheckInRequest request, Long currentUserId) {
        Task task = taskRepository.findById(request.getTaskId()).orElseThrow();
        TaskLabor labor = new TaskLabor();
        labor.setTask(task);
        labor.setUserId(currentUserId);
        labor.setCheckInTime(LocalDateTime.now());
        labor.setCheckInLocation(request.getLocation());
        labor.setCheckInImage(request.getImageBase64());
        laborRepository.save(labor);

        if (task.getStatus() == TaskStatus.NEW) {
            task.setStatus(TaskStatus.IN_PROGRESS);
            task.setActualStartDate(LocalDateTime.now());
            taskRepository.save(task);
        }
    }

    public void checkOut(TaskCheckOutRequest request, Long currentUserId) {
        TaskLabor labor = laborRepository.findByTaskIdAndUserIdAndCheckOutTimeIsNull(request.getTaskId(), currentUserId)
                .orElseThrow(() -> new RuntimeException("Chưa check-in!"));
        laborMapper.updateCheckOut(labor, request);
        long minutes = java.time.Duration.between(labor.getCheckInTime(), labor.getCheckOutTime()).toMinutes();
        labor.setDuration((int) minutes);
        laborRepository.save(labor);
    }

    public void updateTaskStatus(Long taskId, TaskStatus status, Long currentUserId, boolean isAdmin) {
        Task task = taskRepository.findById(taskId).orElseThrow();
        boolean isLeader = memberRepository.findByTaskIdAndUserIdAndIsLeaderTrue(taskId, currentUserId).isPresent();
        if (!isLeader && !isAdmin) throw new RuntimeException("Không có quyền!");

        task.setStatus(status);
        if (status == TaskStatus.DONE) {
            task.setActualEndDate(LocalDateTime.now());
            int total = laborRepository.findByTaskIdOrderByCheckInTimeDesc(taskId).stream()
                    .mapToInt(l -> l.getDuration() != null ? l.getDuration() : 0).sum();
            task.setTotalDuration(total);
        }
        taskRepository.save(task);
    }

    @Transactional(readOnly = true)
    public Page<TaskDetailResponse> getMyTasksForStaff(String zaloId, Pageable pageable) {
        // 1. Lấy danh sách Task từ Repository
        Page<Task> tasks = taskRepository.findTasksByMemberZaloId(zaloId, pageable);

        // 2. Sử dụng method reference để map từng Task thông qua hàm enrich
        // Cực kỳ ngắn gọn, bao nhiêu logic map Leader, Member, Customer, Product nó nằm gọn trong hàm enrich rồi
        return tasks.map(this::enrichTaskDetail);
    }
}