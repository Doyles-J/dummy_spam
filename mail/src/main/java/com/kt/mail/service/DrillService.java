package com.kt.mail.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kt.mail.domain.*;
import com.kt.mail.entity.*;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
public class DrillService {
    private static final Logger log = LoggerFactory.getLogger(DrillService.class);

    @Autowired
    private DrillInfoRepository drillInfoRepository;
    @Autowired
    private DrillMailContentRepository mailContentRepository;
    @Autowired
    private EmailService emailService;
    @Autowired
    private DepartmentRatingRepository departmentRatingRepository;
    @Autowired
    private RecipientRepository recipientRepository;  // EmployeeRepository 대신 RecipientRepository 사용
    @Autowired
    private DrillResultRepository resultRepository;

    public DrillInfo createNewDrill() {
        DrillInfo drillInfo = new DrillInfo();
        if (drillInfo.getDrillId() == null) {
            drillInfo.setDrillId((int) (System.currentTimeMillis() % 100000));
        }
        drillInfo.setDrillDate(LocalDateTime.now());
        return drillInfoRepository.save(drillInfo);
    }

    public void saveDrillMailContent(DrillInfo drillInfo, List<Recipient> recipients, 
                                   String subject, String body) {
        for (Recipient recipient : recipients) {
            DrillMailContent content = new DrillMailContent();
            content.setDrillId(drillInfo.getDrillId());
            content.setEmpId(recipient.getEmpId());
            content.setMailTitle(subject);
            content.setMailContent(body);

            String trackingLink = String.format("http://localhost:8080/track/%d/%d", 
                drillInfo.getDrillId(), recipient.getEmpId());
            content.setMailLink(trackingLink);
            
            mailContentRepository.save(content);
        }
        
        // 메일 컨텐츠 저장 후 부서별 통계 업데이트
        updateDepartmentRatings(drillInfo.getDrillId());
    }

    @Transactional
    public void updateDepartmentRatings(Integer drillId) {
        log.info("부서별 통계 업데이트 시작 - drillId: {}", drillId);
        
        List<DrillMailContent> allMailContents = mailContentRepository.findByDrillInfo_DrillId(drillId);
        log.info("메일 발송 기록 조회 결과: {} 건", allMailContents.size());
        
        Map<Integer, DepartmentStats> deptStatsMap = new HashMap<>();

        for (DrillMailContent content : allMailContents) {
            try {
                Recipient recipient = recipientRepository.findById(content.getEmpId())
                    .orElseThrow(() -> new RuntimeException("직원을 찾을 수 없습니다: " + content.getEmpId()));
                
                Integer deptId = recipient.getDeptId();
                DepartmentStats stats = deptStatsMap.computeIfAbsent(deptId, k -> new DepartmentStats());
                stats.addTotal();

                // 수정된 메서드명 사용
                boolean clicked = resultRepository.existsByDrillInfo_DrillIdAndEmpIdHash(drillId, content.getEmpId().toString());
                if (clicked) {
                    stats.addClicked();
                    log.info("부서 {} - 직원 {} 클릭 확인", deptId, content.getEmpId());
                }
            } catch (Exception e) {
                log.error("통계 처리 중 오류 발생 - empId: {}", content.getEmpId(), e);
            }
        }

        try {
            // 4. 기존 통계 삭제
            departmentRatingRepository.deleteByDrillId(drillId);
            log.info("기존 통계 삭제 완료 - drillId: {}", drillId);

            // 5. 새로운 통계 저장
            for (Map.Entry<Integer, DepartmentStats> entry : deptStatsMap.entrySet()) {
                DepartmentRating rating = new DepartmentRating();
                rating.setDrillId(drillId);
                
                // DrillInfo 설정
                DrillInfo drillInfo = allMailContents.get(0).getDrillInfo();
                rating.setDrillInfo(drillInfo);
                
                // Department 설정
                Department department = new Department();
                department.setDeptId(entry.getKey());
                rating.setDepartment(department);
                
                DepartmentStats stats = entry.getValue();
                double openRatio = stats.getTotal() > 0 
                    ? (double) stats.getClicked() / stats.getTotal() * 100 
                    : 0.0;
                
                rating.setDeptOpenRatio(openRatio);
                
                // 등급 계산
                String deptRating;
                if (openRatio >= 80) deptRating = "A";
                else if (openRatio >= 60) deptRating = "B";
                else if (openRatio >= 40) deptRating = "C";
                else deptRating = "D";
                
                rating.setDeptRating(deptRating);
                
                DepartmentRating savedRating = departmentRatingRepository.save(rating);
                log.info("부서 통계 저장 완료 - ID: {}, 부서: {}, 클릭률: {}%, 등급: {}", 
                    savedRating.getId(), entry.getKey(), openRatio, deptRating);
            }
        } catch (Exception e) {
            log.error("통계 저장 중 오류 발생", e);
            throw e;
        }
        
        log.info("부서별 통계 업데이트 완료");
    }

    public List<DrillInfo> getAllDrills() {
        return drillInfoRepository.findAll();
    }

    public List<DepartmentRating> getDrillStats(Integer drillId) {
        return departmentRatingRepository.findByDrillId(drillId);
    }

    // ... getAllDrills()와 getDrillStats() 메서드는 그대로 유지 ...

    @Getter
    public static class DepartmentStats {
        private int total = 0;
        private int clicked = 0;

        public void addTotal() {
            total++;
        }

        public void addClicked() {
            clicked++;
        }
    }
} 