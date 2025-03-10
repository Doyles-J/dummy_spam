package com.kt.mail.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.kt.mail.domain.DrillResultRepository;
import com.kt.mail.entity.DrillResult;

import java.net.URI;
import java.time.LocalDateTime;

import jakarta.servlet.http.HttpServletRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/track")
@Slf4j
@RequiredArgsConstructor
public class TrackController {
    
    private final DrillResultRepository drillResultRepository;
    
    @GetMapping("/{drillId}/{empIdHash}")
    public ResponseEntity<String> trackEmailClick(
            @PathVariable("drillId") Integer drillId,
            @PathVariable("empIdHash") String empIdHash,
            HttpServletRequest request) {
        
        try {
            log.info("훈련 링크 클릭 감지: drillId={}, empIdHash={}", drillId, empIdHash);
            
            // 클릭 시간 기록
            LocalDateTime clickTime = LocalDateTime.now();
            
            // IP 주소 기록 (프록시를 고려한 실제 IP 주소 획득)
            String ipAddress = extractIpAddress(request);
            
            // 결과 저장
            DrillResult result = new DrillResult();
            result.setDrillId(drillId);
            result.setEmpIdHash(empIdHash);
            result.setClickTime(clickTime);
            result.setIpAddress(ipAddress);
            
            drillResultRepository.save(result);
            
            log.info("훈련 클릭 기록 완료: drillId={}, empIdHash={}, ip={}", 
                drillId, empIdHash, ipAddress);
            
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
