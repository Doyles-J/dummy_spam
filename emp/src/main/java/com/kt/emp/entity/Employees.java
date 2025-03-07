package com.kt.emp.entity;

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
@Table(name = "employees")
public class Employees {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "employee_id")
  @Comment("사원번호 PK")
  private Integer employeeId;

  @Column(name = "first_name")
  @Comment("사원이름")
  private String firstName;

  @Column(name = "last_name")
  private String lastName;

  // @Column(name = "department_id")
  // private int departmentId;
}
