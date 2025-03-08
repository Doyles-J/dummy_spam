package com.kt.mail.entity;

import java.util.List;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import com.kt.mail.entity.Employee;
import com.kt.mail.entity.DepartmentRating;

@Entity
@Table(name = "department")
@Getter
@Setter
public class Department {
    @Id
    @Column(name = "dept_id")
    private Integer deptId;

    @Column(name = "dept_name")
    private String deptName;

    @ManyToOne
    @JoinColumn(name = "manager_id")
    private Employee manager;

    // Employee와의 양방향 관계 (필요한 경우)
    @OneToMany(mappedBy = "department", targetEntity = Employee.class)
    private List<Employee> employees;

    // DepartmentRating과의 양방향 관계 (필요한 경우)
    @OneToMany(mappedBy = "department", targetEntity = DepartmentRating.class)
    private List<DepartmentRating> departmentRatings;
}