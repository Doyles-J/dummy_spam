package com.kt.mail.entity;

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
@Table(name = "drill_mail_content")
@Getter
@Setter
public class DrillMailContent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "drill_id")
    private DrillInfo drillInfo;
    
    @Column(name = "emp_id", nullable = false)
    private Integer empId;
    
    @Column(name = "mail_title")
    private String mailTitle;
    
    @Column(name = "mail_content")
    private String mailContent;
    
    @Column(name = "mail_link")
    private String mailLink;

    public void setDrillInfo(DrillInfo drillInfo) {
        this.drillInfo = drillInfo;
    }

    public void setEmployeeId(Integer empId) {
        if (empId == null) {
            throw new IllegalArgumentException("사원 ID는 null일 수 없습니다.");
        }
        this.empId = empId;
    }

    public void setSubject(String subject) {
        this.mailTitle = subject;
    }

    public void setContent(String body) {
        this.mailContent = body;
    }

    public void setTrackingLink(String trackingLink) {
        this.mailLink = trackingLink;
    }

    public void setDrillId(Integer drillId) {
        if (this.drillInfo == null) {
            this.drillInfo = new DrillInfo();
            this.drillInfo.setDrillId(drillId);
        } else {
            this.drillInfo.setDrillId(drillId);
        }
    }

    public void generateTrackingLink(Integer drillId) {
        if (this.empId == null) {
            throw new IllegalArgumentException("추적 링크 생성을 위해서는 사원 ID가 필요합니다.");
        }
        this.mailLink = String.format("http://localhost:8080/track/%d/%s", 
            drillId, this.empId.hashCode());
    }
} 