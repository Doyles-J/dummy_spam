package com.kt.mail.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger log = LoggerFactory.getLogger(ResultProcessingService.class);

    @Autowired
    private DrillResultRepository resultRepository;
    @Autowired
    private DepartmentRatingRepository ratingRepository;
    @Autowired
    private DrillService drillService;

    public void processResults(Long drillId, List<DrillResult> results) {
        try {
            log.info("훈련 결과 처리 시작 - drillId: {}", drillId);
            
            // 결과 저장
            resultRepository.saveAll(results);
            log.info("훈련 결과 저장 완료 - {} 건", results.size());
            
            // 부서별 통계 업데이트
            drillService.updateDepartmentRatings(drillId.intValue());
            
        } catch (Exception e) {
            log.error("결과 처리 중 오류 발생", e);
            throw new RuntimeException("결과 처리 실패", e);
        }
    }

    public List<DepartmentRating> getRatingsByDrillId(Integer drillId) {
        return ratingRepository.findByDrillId(drillId);
    }
} 