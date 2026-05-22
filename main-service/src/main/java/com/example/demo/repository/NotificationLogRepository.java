package com.example.demo.repository;
import com.example.demo.model.NotificationLog;
import org.springframework.data.jpa.repository.JpaRepository;
public interface NotificationLogRepository extends JpaRepository<NotificationLog, Long> {
}