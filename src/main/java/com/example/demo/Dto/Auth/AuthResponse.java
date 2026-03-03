package com.example.demo.Dto.Auth;
import lombok.*;
@Data
@Builder
public class AuthResponse {
    private String accessToken;
    private UserResponseDTO user;
}
