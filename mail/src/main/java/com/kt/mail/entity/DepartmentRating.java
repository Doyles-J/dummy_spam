package com.kt.mail.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "department_rating")
public class DepartmentRating {

    @Column(name = "dept_id")
    private Integer deptId;
    @Column(name = "drill_id")
    private Integer drillId;
    @Column(name = "dept_open_ratio")
    private Integer deptOpenRatio;
    @Column(name = "dept_rating")
    private String deptRating;
} 