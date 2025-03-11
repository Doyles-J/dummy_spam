package com.kt.mail.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Comment;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
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

    @JsonIgnore  // JSON 직렬화에서 제외
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dept_id")
    private Department department;

    @Column(name = "emp_rank")
    @Comment("직급")
    private String empRank;

    // department의 id를 반환하는 편의 메서드
    public Integer getDeptId() {
        return department != null ? department.getDeptId() : null;
    }

    // department의 이름을 반환하는 편의 메서드
    public String getDeptName() {
        return department != null ? department.getDeptName() : null;
    }
}
