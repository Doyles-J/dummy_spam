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
    
    private Integer empId;
    private String mailTitle;
    private String mailContent;
    private String mailLink;

    public void setDrillInfo(DrillInfo drillInfo) {
        this.drillInfo = drillInfo;
    }

    public void setEmpId(Integer empId) {
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
} 