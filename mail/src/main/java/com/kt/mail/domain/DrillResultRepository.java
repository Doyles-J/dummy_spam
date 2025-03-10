package com.kt.mail.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.kt.mail.entity.DrillResult;

import java.util.List;

@Repository
public interface DrillResultRepository extends JpaRepository<DrillResult, Long> {
    List<DrillResult> findByDrillInfoId(Integer drillId);

    @Query("SELECT COUNT(dr) > 0 FROM DrillResult dr WHERE dr.drillInfo.drillId = :drillId AND dr.empIdHash = :empIdHash")
    boolean existsByDrillInfo_DrillIdAndEmpIdHash(@Param("drillId") Integer drillId, @Param("empIdHash") String empIdHash);
} 