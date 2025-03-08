package com.kt.mail.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "drill_mail_content")
public class DrillMailContent {

    @Column(name = "emp_id")
    private Integer empId;
    @Column(name = "drill_id")
    private Integer drillId;
    @Column(name = "mail_title")
    private String mailTitle;
    @Column(name = "mail_content")
    private String mailContent;
    @Column(name = "mail_link")
    private String mailLink;
} 