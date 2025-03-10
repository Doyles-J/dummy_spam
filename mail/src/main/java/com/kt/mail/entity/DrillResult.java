package com.kt.mail.entity;

import java.time.LocalDateTime;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "drill_result")
@Getter
@Setter
public class DrillResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @ManyToOne
    @JoinColumn(name = "drill_id")
    private DrillInfo drillInfo;
    
    @Column(name = "emp_id")
    private Integer empId;

    @Column(name = "emp_id_hash")
    private String empIdHash;
    
    @Column(name = "open_date")
    private LocalDateTime openDate;
    
    @Column(name = "open_yn")
    private String openYn;

    @Column(name = "ip_address")
    private String ipAddress;

    public void setDrillId(Integer drillId) {
        if (this.drillInfo == null) {
            this.drillInfo = new DrillInfo();
        }
        this.drillInfo.setDrillId(drillId);
    }

    public void setEmpIdHash(String empIdHash) {
        this.empIdHash = empIdHash;
        try {
            this.empId = Integer.valueOf(empIdHash);
        } catch (NumberFormatException e) {
            this.empId = null;
        }
    }

    public void setClickTime(LocalDateTime clickTime) {
        this.openDate = clickTime;
        this.openYn = "Y";
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
} 