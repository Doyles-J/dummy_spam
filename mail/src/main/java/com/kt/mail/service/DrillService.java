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
            
            // 1. 훈련 정보 조회
            DrillInfo drillInfo = drillInfoRepository.findById(Long.valueOf(drillId))
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 훈련입니다: " + drillId));
            log.info("훈련 정보 조회 완료 - drillId: {}, 날짜: {}", drillId, drillInfo.getDrillDate());

            // 2. 메일 컨텐츠 조회
            List<DrillMailContent> contents = mailContentRepository.findByDrillInfo_DrillId(drillId);
            log.info("메일 컨텐츠 조회 완료 - 건수: {}", contents.size());

            // 3. 부서별 대상자 수 집계
            Map<Integer, Integer> deptTotalMap = new HashMap<>();
            for (DrillMailContent content : contents) {
                try {
                    Recipient recipient = recipientRepository.findById(content.getEmpId())
                        .orElseThrow(() -> new RuntimeException("직원 정보를 찾을 수 없습니다: " + content.getEmpId()));
                    
                    deptTotalMap.merge(recipient.getDeptId(), 1, Integer::sum);
                    log.debug("대상자 집계 - empId: {}, deptId: {}", content.getEmpId(), recipient.getDeptId());
                } catch (Exception e) {
                    log.error("대상자 정보 조회 실패 - empId: {}", content.getEmpId(), e);
                }
            }
            log.info("부서별 대상자 수 집계 완료 - 부서 수: {}, 상세: {}", deptTotalMap.size(), deptTotalMap);

            // 4. 클릭 결과 조회
            List<DrillResult> results = drillResultRepository.findByDrillInfo_DrillIdAndOpenYn(drillId, "Y");
            log.info("클릭 결과 조회 완료 - 건수: {}", results.size());

            // 5. 부서별 클릭 수 집계
            Map<Integer, Integer> deptClickMap = new HashMap<>();
            for (DrillResult result : results) {
                try {
                    Integer empId = Integer.valueOf(result.getEmpIdHash());
                    Recipient recipient = recipientRepository.findById(empId)
                        .orElseThrow(() -> new RuntimeException("직원 정보를 찾을 수 없습니다: " + empId));
                    
                    deptClickMap.merge(recipient.getDeptId(), 1, Integer::sum);
                    log.debug("클릭 집계 - empId: {}, deptId: {}", empId, recipient.getDeptId());
                } catch (Exception e) {
                    log.error("클릭 직원 정보 조회 실패 - empIdHash: {}", result.getEmpIdHash(), e);
                }
            }
            log.info("부서별 클릭 수 집계 완료 - 부서 수: {}, 상세: {}", deptClickMap.size(), deptClickMap);

            // 6. 부서별 통계 생성
            List<Map<String, Object>> stats = new ArrayList<>();
            for (Map.Entry<Integer, Integer> entry : deptTotalMap.entrySet()) {
                Integer deptId = entry.getKey();
                Integer totalCount = entry.getValue();
                Integer clickCount = deptClickMap.getOrDefault(deptId, 0);
                
                double openRatio = totalCount > 0 ? (clickCount * 100.0 / totalCount) : 0.0;
                
                Map<String, Object> stat = new HashMap<>();
                stat.put("deptId", deptId);
                stat.put("deptName", "부서 " + deptId); // 부서명이 있다면 해당 부서명 사용
                stat.put("totalEmployees", totalCount);
                stat.put("clickedCount", clickCount);
                stat.put("openRatio", openRatio);
                stat.put("rating", calculateSecurityRating(openRatio));
                
                stats.add(stat);
            }
            
            log.info("부서별 통계 생성 완료 - 부서 수: {}", stats.size());
            return stats;
                
        } catch (Exception e) {
            log.error("부서별 통계 조회 중 오류 발생. drillId: {}", drillId, e);
            throw new RuntimeException("부서별 통계 조회 실패", e);
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