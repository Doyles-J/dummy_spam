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

    @Query("SELECT COUNT(r) FROM DrillResult r WHERE r.drillInfo.drillId = :drillId AND r.empId IN " +
           "(SELECT e.empId FROM Employee e WHERE e.department.deptId = :deptId) AND r.openYn = :openYn")
    int countByDrillInfo_DrillIdAndEmployee_Department_DeptIdAndOpenYn(
        @Param("drillId") Integer drillId, 
        @Param("deptId") Integer deptId, 
        @Param("openYn") String openYn
    );
} 