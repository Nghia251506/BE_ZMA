package com.example.demo.Dto.Auth;

import lombok.Data;

@Data
public class RegisterRequest {
    private String username;
    private String password;
    private String fullName;
    private String email;
}
