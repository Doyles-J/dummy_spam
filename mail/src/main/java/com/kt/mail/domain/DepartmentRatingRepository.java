package com.kt.mail.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kt.mail.entity.DepartmentRating;

import java.util.List;

@Repository
public interface DepartmentRatingRepository extends JpaRepository<DepartmentRating, Long> {
    List<DepartmentRating> findByDrillInfo_DrillId(Integer drillId);
} 