package com.kt.mail.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.kt.mail.domain.DrillResultRepository;
import com.kt.mail.entity.DrillResult;
import com.kt.mail.service.DrillService;
import com.kt.mail.service.DrillResultService;

import java.net.URI;
import java.time.LocalDateTime;
import java.sql.Timestamp;

import jakarta.servlet.http.HttpServletRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;

@RestController
@RequestMapping("/track")
@Slf4j
@RequiredArgsConstructor
public class TrackController {
    
    private final DrillResultRepository drillResultRepository;
    private final DrillService drillService;
    private final DrillResultService drillResultService;
    private final JdbcTemplate jdbcTemplate;
    
    @GetMapping("/{drillId}/{empIdHash}")
    public ResponseEntity<String> trackEmailClick(
            @PathVariable("drillId") Integer drillId,
            @PathVariable("empIdHash") String empIdHash,
            HttpServletRequest request) {
        
        try {
            log.info("훈련 링크 클릭 감지: drillId={}, empIdHash={}", drillId, empIdHash);
            
            // empIdHash를 Integer로 변환 (이 부분이 중요)
            Integer empId = null;
            try {
                empId = Integer.parseInt(empIdHash);
                log.info("변환된 empId: {}", empId);
            } catch (NumberFormatException e) {
                log.error("empIdHash를 Integer로 변환할 수 없습니다: {}", empIdHash);
            }
            
            // 클릭 시간 기록
            LocalDateTime clickTime = LocalDateTime.now();
            
            // IP 주소 기록
            String ipAddress = extractIpAddress(request);
            
            // 결과 저장 - 직접 SQL 쿼리 사용
            String sql = "INSERT INTO drill_result (drill_id, emp_id, emp_id_hash, open_yn, open_date, ip_address) " +
                         "VALUES (?, ?, ?, 'Y', ?, ?)";
            
            jdbcTemplate.update(sql, 
                drillId, 
                empId,  // 변환된 empId 사용
                empIdHash, 
                Timestamp.valueOf(clickTime), 
                ipAddress);
            
            log.info("훈련 클릭 기록 완료: drillId={}, empId={}, empIdHash={}, ip={}", 
                drillId, empId, empIdHash, ipAddress);
            
            // 부서별 통계 업데이트
            try {
                log.info("부서별 통계 업데이트 시작: drillId={}", drillId);
                drillService.updateDepartmentRatings(drillId);
                log.info("부서별 통계 업데이트 완료: drillId={}", drillId);
            } catch (Exception e) {
                log.error("부서별 통계 업데이트 실패: drillId={}", drillId, e);
            }
            
            // 클릭 후 리다이렉트할 페이지로 이동
            return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create("/result.html"))
                .build();
                
        } catch (Exception e) {
            log.error("훈련 결과 기록 실패: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("처리 중 오류가 발생했습니다.");
        }
    }

    // 실제 IP 주소를 추출하는 메서드
    private String extractIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        
        return ip;
    }
}
