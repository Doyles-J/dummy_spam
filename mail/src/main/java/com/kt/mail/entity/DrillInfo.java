package com.kt.mail.entity;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "drill_info")
@Getter
@Setter
public class DrillInfo {
    @Id
    @Column(name = "drill_id")
    private Integer drillId;

    @Column(name = "drill_date", nullable = false)
    private LocalDateTime drillDate;

    @Column(name = "id", insertable = false, updatable = false)
    private Integer id;

    @OneToMany(mappedBy = "drillInfo")
    private List<DepartmentRating> departmentRatings;

    public DrillInfo() {
        this.drillId = (int) (System.currentTimeMillis() % 100000);
    }

    public Integer getId() {
        return drillId;
    }

    public void setDrillDate(LocalDateTime drillDate) {
        this.drillDate = drillDate;
    }

    public Integer getDrillId() {
        return drillId;
    }

    public void setDrillId(Integer drillId) {
        this.drillId = drillId;
    }

    // 기타 필요한 정보 필드
} 