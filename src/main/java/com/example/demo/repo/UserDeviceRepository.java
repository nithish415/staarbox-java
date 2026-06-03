package com.example.demo.repo;

import com.example.demo.entity.UserDevice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserDeviceRepository extends JpaRepository<UserDevice, Long> {

    List<UserDevice> findByUserId(Long userId);

    List<UserDevice> findByUserIdAndIsActiveTrue(Long userId);

    @Query("SELECT ud FROM UserDevice ud WHERE ud.isActive = true")
    List<UserDevice> findAllActiveDevices();

    Optional<UserDevice> findByUserIdAndDeviceToken(Long userId, String deviceToken);

    @Modifying
    @Query("UPDATE UserDevice ud SET ud.isActive = false WHERE ud.deviceToken = :token")
    void deactivateByToken(@Param("token") String token);

    @Modifying
    @Query("UPDATE UserDevice ud SET ud.isActive = false WHERE ud.userId = :userId")
    void deactivateAllByUserId(@Param("userId") Long userId);
}
