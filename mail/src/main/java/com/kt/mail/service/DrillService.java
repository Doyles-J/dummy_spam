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
import java.util.stream.Collectors;

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
    private DepartmentRatingRepository departmentRatingRepository;
    @Autowired
    private RecipientRepository recipientRepository;  // EmployeeRepository 대신 RecipientRepository 사용
    @Autowired
    private DrillResultRepository resultRepository;
    @Autowired
    private DrillResultRepository drillResultRepository;

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
                if (openRatio <= 5) deptRating = "Green";
                else if (openRatio <= 20) deptRating = "Yellow";
                else deptRating = "Red";
                
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
        log.info("모든 훈련 목록 조회 시작");
        List<DrillInfo> drills = drillInfoRepository.findAll();
        log.info("조회된 훈련 수: {}", drills.size());
        
        // 각 훈련별 수신자 수 설정
        for (DrillInfo drill : drills) {
            List<DrillMailContent> contents = mailContentRepository.findByDrillInfo_DrillId(drill.getDrillId());
            drill.setRecipientCount(contents.size());
            log.info("훈련 ID: {}, 날짜: {}, 수신자 수: {}", 
                drill.getDrillId(), drill.getDrillDate(), contents.size());
        }
        
        return drills;
    }

    public List<Map<String, Object>> getDrillStats(Integer drillId) {
        log.info("부서별 통계 조회 시작 - drillId: {}", drillId);
        List<DepartmentRating> ratings = departmentRatingRepository.findByDrillId(drillId);
        
        return ratings.stream().map(rating -> {
            Map<String, Object> stat = new HashMap<>();
            Integer deptId = rating.getDepartment().getDeptId();
            
            // 부서 정보
            stat.put("department", Map.of(
                "deptId", deptId,
                "deptName", rating.getDepartment().getDeptName() != null ? 
                           rating.getDepartment().getDeptName() : 
                           "부서 " + deptId
            ));
            
            // 통계 정보
            int totalEmployees = mailContentRepository.countByDrillInfo_DrillIdAndEmployee_Department_DeptId(
                drillId, deptId);
            int clickedCount = drillResultRepository.countByDrillInfo_DrillIdAndEmployee_Department_DeptIdAndOpenYn(
                drillId, deptId, "Y");
            
            stat.put("totalEmployees", totalEmployees);
            stat.put("clickedCount", clickedCount);
            stat.put("deptOpenRatio", rating.getDeptOpenRatio());
            stat.put("deptRating", rating.getDeptRating());
            
            log.info("부서 {} 통계: 전체={}, 클릭={}, 비율={}%, 등급={}", 
                deptId, totalEmployees, clickedCount, rating.getDeptOpenRatio(), rating.getDeptRating());
            
            return stat;
        }).collect(Collectors.toList());
    }

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

    @Transactional
    public List<Map<String, Object>> getDepartmentRatings(Integer drillId) {
        try {
            log.info("부서별 통계 조회 시작 - drillId: {}", drillId);
            
            // 1. 선택된 날짜의 모든 훈련 ID 조회
            DrillInfo selectedDrill = drillInfoRepository.findById(drillId.longValue())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 훈련입니다: " + drillId));
            
            LocalDateTime startOfDay = selectedDrill.getDrillDate().toLocalDate().atStartOfDay();
            LocalDateTime endOfDay = startOfDay.plusDays(1);
            
            List<DrillInfo> sameDayDrills = drillInfoRepository.findByDrillDateBetween(startOfDay, endOfDay);
            List<Integer> drillIds = sameDayDrills.stream()
                .map(DrillInfo::getDrillId)
                .collect(Collectors.toList());
            
            log.info("같은 날짜의 훈련 ID 목록: {}", drillIds);

            // 2. 부서별 통계 집계
            Map<Integer, DepartmentSummary> deptSummaryMap = new HashMap<>();

            // 2-1. 메일 발송 대상자 수 집계
            List<DrillMailContent> allContents = mailContentRepository.findByDrillInfo_DrillIdIn(drillIds);
            for (DrillMailContent content : allContents) {
                Recipient recipient = recipientRepository.findById(content.getEmpId())
                    .orElseThrow(() -> new RuntimeException("직원 정보를 찾을 수 없습니다: " + content.getEmpId()));
                
                // getDepartment() 대신 getDeptId()와 부서명 생성 사용
                Integer deptId = recipient.getDeptId();
                String deptName = recipient.getDeptName();
                
                deptSummaryMap.computeIfAbsent(deptId,
                    k -> new DepartmentSummary(deptName)).addRecipient();
            }

            // 2-2. 클릭 수와 미탐지율 집계
            List<DepartmentRating> allRatings = departmentRatingRepository.findByDrillIdIn(drillIds);
            for (DepartmentRating rating : allRatings) {
                // Department의 이름을 직접 가져오거나 기본값 설정
                String deptName = rating.getDepartment() != null ? 
                    "부서 " + rating.getDepartment().getDeptId() : "알 수 없는 부서";
                
                DepartmentSummary summary = deptSummaryMap.computeIfAbsent(
                    rating.getDepartment().getDeptId(),
                    k -> new DepartmentSummary(deptName));
                
                // DrillResult에서 클릭 수를 직접 조회
                long clickCount = drillResultRepository.countByDrillInfo_DrillIdAndEmployee_Department_DeptIdAndOpenYn(
                    rating.getDrillId(), 
                    rating.getDepartment().getDeptId(), 
                    "Y"
                );
                
                summary.addClickCount((int) clickCount);
                summary.addOpenRatio(rating.getDeptOpenRatio());
            }

            // 3. 결과 변환
            return deptSummaryMap.entrySet().stream()
                .map(entry -> {
                    DepartmentSummary summary = entry.getValue();
                    Map<String, Object> result = new HashMap<>();
                    result.put("deptId", entry.getKey());
                    result.put("deptName", summary.getDeptName());
                    result.put("totalEmployees", summary.getTotalRecipients());
                    result.put("clickedCount", summary.getTotalClicks());
                    result.put("openRatio", summary.getAverageOpenRatio());
                    result.put("rating", calculateSecurityRating(summary.getAverageOpenRatio()));
                    return result;
                })
                .collect(Collectors.toList());
                
        } catch (Exception e) {
            log.error("부서별 통계 조회 중 오류 발생. drillId: {}", drillId, e);
            throw new RuntimeException("부서별 통계 조회 실패", e);
        }
    }

    // 부서별 통계 요약을 위한 내부 클래스 추가
    @Getter
    private static class DepartmentSummary {
        private final String deptName;
        private int totalRecipients = 0;
        private int totalClicks = 0;
        private double totalOpenRatio = 0.0;
        private int ratingCount = 0;

        public DepartmentSummary(String deptName) {
            this.deptName = deptName != null ? deptName : "알 수 없는 부서";
        }

        public void addRecipient() {
            totalRecipients++;
        }

        public void addClickCount(int clicks) {
            totalClicks += clicks;
        }

        public void addOpenRatio(double ratio) {
            totalOpenRatio += ratio;
            ratingCount++;
        }

        public double getAverageOpenRatio() {
            return ratingCount > 0 ? totalOpenRatio / ratingCount : 0.0;
        }
    }

    private String calculateSecurityRating(double openRatio) {
        if (openRatio <= 20) return "A";
        if (openRatio <= 40) return "B";
        if (openRatio <= 60) return "C";
        if (openRatio <= 80) return "D";
        return "F";
    }
} 