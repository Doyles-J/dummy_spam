package com.kt.mail.entity;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "drill_result")
public class DrillResult {

    @Column(name = "drill_id")
    private Integer drillId;
    @Column(name = "emp_id")
    private Integer empId;
    @Column(name = "open_date")
    private Date openDate;
    @Column(name = "open_yn")
    private String openYn;
} 