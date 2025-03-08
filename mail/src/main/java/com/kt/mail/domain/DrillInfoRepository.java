package com.kt.mail.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kt.mail.entity.DrillInfo;

@Repository
public interface DrillInfoRepository extends JpaRepository<DrillInfo, Long> {
    // 필요한 경우 커스텀 쿼리 메소드 추가
} 