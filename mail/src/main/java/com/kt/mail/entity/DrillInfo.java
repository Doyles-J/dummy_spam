package com.kt.mail.entity;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "drill_info")
public class DrillInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "drill_id")
    private Integer drillId;

    @Column(name = "drill_date")
    private Date drillDate;
    
    
    // 기타 필요한 정보 필드
} 