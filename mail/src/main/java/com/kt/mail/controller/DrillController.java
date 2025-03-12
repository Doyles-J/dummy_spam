package com.kt.mail.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.kt.mail.entity.DepartmentRating;
import com.kt.mail.entity.DrillInfo;
import com.kt.mail.entity.DrillRequest;
import com.kt.mail.entity.DrillResult;
import com.kt.mail.service.DrillService;
import com.kt.mail.service.EmailService;
import com.kt.mail.service.ResultProcessingService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/drill")
public class DrillController {
    private static final Logger log = LoggerFactory.getLogger(DrillController.class);

    @Autowired
    private DrillService drillService;
    @Autowired
    private EmailService emailService;
    @Autowired
    private ResultProcessingService resultService;

    @PostMapping("/send")
    public ResponseEntity<?> startNewDrill(@RequestBody Map<String, Object> request) {
        try {
            @SuppressWarnings("unchecked")
            List<Object> recipients = (List<Object>) request.get("recipients");
            String subject = (String) request.get("subject");
            String body = (String) request.get("body");
            
            // 로깅 추가
            log.info("수신자 목록: {}", recipients);
            log.info("제목: {}", subject);
            log.info("내용: {}", body);
            
            // 메일 발송 로직
            DrillInfo drillInfo = drillService.createNewDrill();
            emailService.sendEmails(recipients, subject, body, drillInfo.getDrillId());
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "drillId", drillInfo.getDrillId()
            ));
        } catch (Exception e) {
            log.error("메일 발송 오류: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    @PostMapping("/{drillId}/results")
    public ResponseEntity<?> uploadResults(
            @PathVariable Long drillId,
            @RequestParam("file") MultipartFile file) {
        try {
            List<DrillResult> results = parseCSVFile(file);
            resultService.processResults(drillId, results);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    private List<DrillResult> parseCSVFile(MultipartFile file) {
        // CSV 파일 파싱 로직 구현
        // 1. CSV 읽기
        // 2. DrillResult 객체로 변환
        // 3. 반환
        return new ArrayList<>(); // 임시 반환
    }

    @GetMapping("/list")
    public ResponseEntity<?> getDrillList() {
        try {
            log.info("훈련 목록 조회 요청 받음");
            List<DrillInfo> drills = drillService.getAllDrills();
            List<Map<String, Object>> response = drills.stream()
                .map(DrillInfo::toMap)
                .collect(Collectors.toList());
            
            log.info("응답 데이터: {}", response);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("훈련 목록 조회 실패: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(
                Collections.singletonMap("error", "훈련 목록을 불러오는데 실패했습니다.")
            );
        }
    }

    @GetMapping("/{drillId}/stats")
    public ResponseEntity<List<Map<String, Object>>> getDrillStats(@PathVariable("drillId") Integer drillId) {
        List<Map<String, Object>> stats = drillService.getDepartmentRatings(drillId);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/api/drills/{drillId}")
    public ResponseEntity<?> getDrill(@PathVariable("drillId") Integer drillId) {
        // ... 메서드 내용 ...
        return null; // Placeholder return, actual implementation needed
    }

    @GetMapping("/{drillId}/clicked-employees")
    public ResponseEntity<?> getClickedEmployees(
        @PathVariable("drillId") Integer drillId, 
        @RequestParam("deptId") Integer deptId) {
        try {
            List<Map<String, Object>> clickedEmployees = drillService.getClickedEmployees(drillId, deptId);
            return ResponseEntity.ok(clickedEmployees);
        } catch (Exception e) {
            log.error("클릭한 사용자 정보 조회 실패. drillId: {}, deptId: {}", drillId, deptId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "클릭한 사용자 정보 조회에 실패했습니다."));
        }
    }
} 