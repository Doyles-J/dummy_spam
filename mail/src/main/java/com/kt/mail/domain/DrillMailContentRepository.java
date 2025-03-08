package com.kt.mail.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kt.mail.entity.DrillMailContent;

import java.util.List;

@Repository
public interface DrillMailContentRepository extends JpaRepository<DrillMailContent, Long> {
    List<DrillMailContent> findByDrillInfoId(Integer drillId);
} 