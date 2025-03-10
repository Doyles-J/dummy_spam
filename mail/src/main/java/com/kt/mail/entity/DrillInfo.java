package com.kt.mail.entity;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "drill_id", unique = true)
    private Integer drillId;

    @Column(name = "drill_date")
    private LocalDateTime drillDate;

    @OneToMany(mappedBy = "drillInfo")
    private List<DepartmentRating> departmentRatings;

    public void setDrillDate(LocalDateTime now) {
        this.drillDate = now;
    }

    public Long getId() {
        return Long.valueOf(this.id);
    }
    
    
    // 기타 필요한 정보 필드
} 