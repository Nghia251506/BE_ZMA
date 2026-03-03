package com.example.demo.Dto.Task.Response;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberResponse {
    private Long id;
    private String fullName;
    private String zaloId;
    private String avatarZalo;
    private String position;
    private Boolean isLeader;
}
