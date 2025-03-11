package com.kt.mail.entity;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "drill_info")
@Getter
@Setter
public class DrillInfo {
    @Id
    @Column(name = "drill_id")
    private Integer drillId;

    @Column(name = "drill_date", nullable = false)
    private LocalDateTime drillDate;

    @Transient // DB에 저장하지 않는 필드
    private int recipientCount;

    @OneToMany(mappedBy = "drillInfo")
    @JsonIgnore // 순환 참조 방지
    private List<DrillMailContent> mailContents;

    public DrillInfo() {
        this.drillId = (int) (System.currentTimeMillis() % 100000);
    }

    public Integer getId() {
        return drillId;
    }

    public void setDrillDate(LocalDateTime drillDate) {
        this.drillDate = drillDate;
    }

    public Integer getDrillId() {
        return drillId;
    }

    public void setDrillId(Integer drillId) {
        this.drillId = drillId;
    }

    // JSON 직렬화를 위한 getter
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", this.drillId);
        map.put("date", this.drillDate.toString());
        map.put("recipients", Collections.singletonMap("length", this.recipientCount));
        return map;
    }

    // 기타 필요한 정보 필드
} 