package com.example.demo.Repository;

import com.example.demo.Entity.TaskEquipmentSerial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TaskEquipmentSerialRepository extends JpaRepository<TaskEquipmentSerial, Long> {

    // Tìm kiếm thiết bị theo Serial (Cực kỳ quan trọng để check bảo hành)
    Optional<TaskEquipmentSerial> findBySerialNumber(String serialNumber);
}
