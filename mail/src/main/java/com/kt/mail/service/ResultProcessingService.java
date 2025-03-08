package com.kt.mail.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kt.mail.domain.DepartmentRatingRepository;
import com.kt.mail.domain.DrillResultRepository;
import com.kt.mail.entity.DepartmentRating;
import com.kt.mail.entity.DrillResult;

import java.util.List;

@Service
@Transactional
public class ResultProcessingService {
    @Autowired
    private DrillResultRepository resultRepository;
    @Autowired
    private DepartmentRatingRepository ratingRepository;

    public void processResults(Long drillId, List<DrillResult> results) {
        // 결과 저장
        resultRepository.saveAll(results);
        
        // 부서별 통계 계산 및 저장
        calculateDepartmentRatings(drillId);
    }

    private void calculateDepartmentRatings(Long drillId) {
        // 부서별 통계 계산 로직 구현
        // 1. 해당 훈련의 모든 결과 조회
        // 2. 부서별로 그룹화
        // 3. 비율 계산
        // 4. DepartmentRating 저장
    }

    public List<DepartmentRating> getRatingsByDrillId(Integer drillId) {
        return ratingRepository.findByDrillInfo_DrillId(drillId);
    }
} 