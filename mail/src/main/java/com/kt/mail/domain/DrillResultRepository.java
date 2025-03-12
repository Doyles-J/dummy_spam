package com.kt.mail.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.kt.mail.entity.DrillResult;

import java.util.List;
import java.util.Map;

@Repository
public interface DrillResultRepository extends JpaRepository<DrillResult, Long> {
    List<DrillResult> findByDrillInfoId(Integer drillId);

    @Query("SELECT new map(" +
           "r.empIdHash as empIdHash, " +
           "COUNT(r) as totalCount, " +
           "SUM(CASE WHEN r.openYn = 'Y' THEN 1 ELSE 0 END) as clickedCount, " +
           "(SUM(CASE WHEN r.openYn = 'Y' THEN 1 ELSE 0 END) * 100.0 / COUNT(r)) as openRatio) " +
           "FROM DrillResult r " +
           "WHERE r.drillInfo.drillId = :drillId " +
           "GROUP BY r.empIdHash")
    List<Map<String, Object>> getDepartmentStatistics(@Param("drillId") Integer drillId);

    boolean existsByDrillInfo_DrillIdAndEmpIdHash(Integer drillId, String empIdHash);

    @Query("SELECT COUNT(r) FROM DrillResult r WHERE r.drillInfo.drillId = :drillId AND r.empId IN " +
           "(SELECT e.empId FROM Employee e WHERE e.department.deptId = :deptId) AND r.openYn = :openYn")
    int countByDrillInfo_DrillIdAndEmployee_Department_DeptIdAndOpenYn(
        @Param("drillId") Integer drillId, 
        @Param("deptId") Integer deptId, 
        @Param("openYn") String openYn
    );

    List<DrillResult> findByDrillInfo_DrillIdAndOpenYn(Integer drillId, String openYn);

    List<DrillResult> findByDrillInfo_DrillIdAndEmployee_Department_DeptIdAndOpenYn(
        Integer drillId, Integer deptId, String openYn);
} 