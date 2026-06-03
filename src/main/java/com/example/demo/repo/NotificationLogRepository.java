package com.example.demo.repo;

import com.example.demo.entity.NotificationLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationLogRepository extends JpaRepository<NotificationLog, Long> {

    List<NotificationLog> findByUserId(Long userId);

    @Query("SELECT nl FROM NotificationLog nl WHERE nl.userId = :userId AND nl.sentAt >= :since ORDER BY nl.sentAt DESC")
    List<NotificationLog> findRecentByUserId(@Param("userId") Long userId, @Param("since") LocalDateTime since);

    @Query("SELECT COUNT(nl) FROM NotificationLog nl WHERE nl.success = true AND nl.sentAt >= :since")
    long countSuccessfulNotificationsSince(@Param("since") LocalDateTime since);

    @Query("SELECT COUNT(nl) FROM NotificationLog nl WHERE nl.success = false AND nl.sentAt >= :since")
    long countFailedNotificationsSince(@Param("since") LocalDateTime since);
}
