package com.example.demo.Dto.Customer;

import lombok.Data;

@Data
public class CustomerCreateRequest {
    private String fullName;
    private String email;
    private String phone;
    private String address;
    private String customerCode;
    private String mainPhone;
    private String subPhone;
    private String description;
    private String zaloGroup;
    private String company;
    private String taxCode;
    private String note;

    private Long userId;          // ID tài khoản nếu có
    private String groupUserMaNV; // maNV của nhóm khách hàng
    private Long assigneeId;      // ID nhân viên phụ trách
}
