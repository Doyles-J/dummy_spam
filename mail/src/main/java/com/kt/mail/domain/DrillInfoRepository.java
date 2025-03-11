package com.kt.mail.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kt.mail.entity.DrillInfo;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DrillInfoRepository extends JpaRepository<DrillInfo, Long> {
    // 필요한 경우 커스텀 쿼리 메소드 추가
    List<DrillInfo> findByDrillDateBetween(LocalDateTime start, LocalDateTime end);
    
} 