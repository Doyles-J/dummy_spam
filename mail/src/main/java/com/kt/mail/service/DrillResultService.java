package com.kt.mail.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kt.mail.domain.DrillResultRepository;
import com.kt.mail.entity.DrillResult;

@Service
public class DrillResultService {
    private final DrillResultRepository drillResultRepository;

    @Autowired
    public DrillResultService(DrillResultRepository drillResultRepository) {
        this.drillResultRepository = drillResultRepository;
    }

    @Transactional
    public void saveResult(Integer drillId, String empIdHash) {
        DrillResult result = new DrillResult();
        result.setDrillId(drillId);
        result.setEmpId(decodeEmpIdHash(empIdHash));
        result.setClickTime(new java.sql.Timestamp(System.currentTimeMillis()));
        
        
        drillResultRepository.save(result);
    }

    private Integer decodeEmpIdHash(String empIdHash) {
        // empIdHash를 디코딩하여 실제 empId를 반환
        // 임시로 문자열을 정수로 변환
        try {
            return Integer.parseInt(empIdHash);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid empIdHash format");
        }
    }
} 