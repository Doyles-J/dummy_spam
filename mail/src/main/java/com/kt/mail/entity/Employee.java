package com.kt.mail.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "employee")
@Getter
@Setter
public class Employee {
    @Id
    @Column(name = "emp_id")
    private Integer empId;

    @Column(name = "emp_name")
    private String empName;

    @Column(name = "emp_mail")
    private String empMail;

    @Column(name = "emp_rank")
    private String empRank;

    @Column(name = "hire_date")
    private LocalDateTime hireDate;

    @ManyToOne
    @JoinColumn(name = "dept_id")
    private Department department;

    // getters and setters
}