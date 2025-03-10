package com.kt.mail.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.kt.mail.entity.DrillInfo;
import com.kt.mail.entity.DrillRequest;
import com.kt.mail.entity.DrillResult;
import com.kt.mail.service.DrillService;
import com.kt.mail.service.EmailService;
import com.kt.mail.service.ResultProcessingService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/drill")
public class DrillController {
    @Autowired
    private DrillService drillService;
    @Autowired
    private EmailService emailService;
    @Autowired
    private ResultProcessingService resultService;

    @PostMapping("/send")
    public ResponseEntity<?> startNewDrill(@RequestBody DrillRequest request) {
        try {
            // 1. 새 훈련 정보 생성
            DrillInfo drillInfo = drillService.createNewDrill();
            
            // 2. 메일 컨텐츠 저장
            drillService.saveDrillMailContent(
                drillInfo, 
                request.getRecipients(), 
                request.getSubject(), 
                request.getBody()
            );
            
            // 3. 이메일 발송
            emailService.sendEmails(
                request.getRecipients(),
                request.getSubject(),
                request.getBody(),
                drillInfo.getId()
            );

            return ResponseEntity.ok(Map.of(
                "success", true,
                "drillId", drillInfo.getId()
            ));
        } catch (Exception e) {
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
} 