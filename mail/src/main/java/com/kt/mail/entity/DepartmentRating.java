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
import jakarta.persistence.FetchType;

@Entity
@Table(name = "department_rating")
@Getter
@Setter
public class DepartmentRating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "drill_id", insertable = false, updatable = false)
    private Integer drillId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "drill_id")
    private DrillInfo drillInfo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dept_id")
    private Department department;

    @Column(name = "dept_rating")
    private String deptRating;

    @Column(name = "dept_open_ratio")
    private Double deptOpenRatio;

    public void setDeptId(Integer key) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setDeptId'");
    }
} 