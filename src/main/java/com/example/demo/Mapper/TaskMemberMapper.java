package com.example.demo.Mapper;

import com.example.demo.Entity.Task;
import com.example.demo.Entity.TaskMember;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TaskMemberMapper {
    // Map từ ID sang Entity Member
    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "isLeader", source = "isLeader")
    TaskMember toEntity(Long userId, Boolean isLeader, Task task);
}
