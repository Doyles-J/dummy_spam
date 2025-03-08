package com.kt.mail.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kt.mail.entity.DrillResult;

import java.util.List;

@Repository
public interface DrillResultRepository extends JpaRepository<DrillResult, Integer> {
    List<DrillResult> findByDrillInfoId(Integer drillId);
} 