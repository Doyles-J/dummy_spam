package com.kt.mail.domain;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.kt.mail.entity.DrillMailContent;

@Repository
public interface DrillMailContentRepository extends JpaRepository<DrillMailContent, Long> {

    List<DrillMailContent> findByDrillInfo_DrillId(Integer drillId);
    // 기본적인 CRUD 작업은 JpaRepository에서 제공
    // 필요한 경우 커스텀 쿼리 메소드 추가 가능

    @Query("SELECT COUNT(d) FROM DrillMailContent d WHERE d.drillInfo.drillId = :drillId AND d.empId IN " +
           "(SELECT e.empId FROM Employee e WHERE e.department.deptId = :deptId)")
    int countByDrillInfo_DrillIdAndEmployee_Department_DeptId(
        @Param("drillId") Integer drillId, 
        @Param("deptId") Integer deptId
    );
}