package com.kt.mail.entity;

import org.hibernate.annotations.Comment;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Table(name = "employee")
public class Recipient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "emp_id")
    @Comment("사원번호")
    private Integer empId;
    
    @Column(name = "emp_name")
    @Comment("사원이름")
    private String empName;

    @Column(name = "emp_mail")
    @Comment("사원이메일")
    private String empMail;

    @Column(name = "dept_id")
    @Comment("부서번호")
    private Integer deptId;

    @Column(name = "emp_rank")
    @Comment("직급")
    private String empRank;
}
