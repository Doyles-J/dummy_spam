package com.kt.mail.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.kt.mail.entity.Recipient;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender javaMailSender;

    public void sendSimpleMailMessage() {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        try {
            // 메일을 받을 수신자 설정
            simpleMailMessage.setTo("keev005@naver.com");
            // 메일의 제목 설정
            simpleMailMessage.setSubject("테스트 메일 제목");
            // 메일의 내용 설정
            simpleMailMessage.setText("테스트 메일 내용");
            javaMailSender.send(simpleMailMessage);
            log.info("메일 발송 성공!");
        } catch (Exception e) {
            log.info("메일 발송 실패!");
            throw new RuntimeException(e);
        }
    }

    public void sendEmails(List<Object> recipients, String subject, String body, Integer drillId) {
        try {
            // 디버그: 수신자 목록과 타입 출력
            log.info("수신자 목록 타입: {}", recipients.getClass());
            log.info("첫 번째 수신자 타입: {}", recipients.isEmpty() ? "없음" : recipients.get(0).getClass());
            log.info("수신자 목록 내용: {}", recipients);
            
            // 수신자 목록이 null이거나 비어 있는지 확인
            if (recipients == null || recipients.isEmpty()) {
                log.warn("수신자 목록이 비어 있습니다");
                return;
            }
            
            // 각 수신자에게 메일 발송
            for (Object recipientObj : recipients) {
                String email = null;
                
                // JSON에서 변환된 LinkedHashMap으로 처리
                if (recipientObj instanceof Map) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> recipientMap = (Map<String, Object>) recipientObj;
                    log.info("수신자 맵: {}", recipientMap);
                    
                    // 가능한 모든 이메일 필드명 시도
                    email = (String) recipientMap.get("email");
                    if (email == null) email = (String) recipientMap.get("empMail");
                    if (email == null) email = (String) recipientMap.get("emp_mail");
                    
                    log.info("추출된 이메일: {}", email);
                } 
                // Recipient 엔티티로 처리
                else if (recipientObj instanceof Recipient) {
                    Recipient recipient = (Recipient) recipientObj;
                    email = recipient.getEmpMail();
                    log.info("Recipient에서 추출된 이메일: {}", email);
                    
                    // 변환된 필드 값 모두 출력 (디버깅용)
                    log.info("Recipient 상세 정보: empId={}, empName={}, empMail={}", 
                        recipient.getEmpId(), recipient.getEmpName(), recipient.getEmpMail());
                }
                
                if (email != null && !email.isEmpty()) {
                    log.info("이메일 발송 시도: {}", email);
                    
                    SimpleMailMessage message = new SimpleMailMessage();
                    message.setTo(email);
                    message.setSubject(subject != null ? subject : "테스트 제목");
                    
                    // 추적 링크 추가
                    String trackingLink = String.format("https://localhost:8080/track/%d/%s", 
                        drillId, email.hashCode());
                    String fullBody = (body != null ? body : "테스트 내용") + 
                        "\n\n " + trackingLink;
                    
                    message.setText(fullBody);
                    javaMailSender.send(message);
                    log.info("메일 발송 성공: {}", email);
                } else {
                    log.warn("유효하지 않은 이메일 주소: {}", recipientObj);
                }
            }
            
            log.info("모든 메일 발송 완료");
        } catch (Exception e) {
            log.error("메일 발송 실패: {}", e.getMessage(), e);
            throw new RuntimeException("메일 발송 중 오류 발생", e);
        }
    }
}
