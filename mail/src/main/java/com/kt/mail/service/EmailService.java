package com.kt.mail.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.kt.mail.entity.Recipient;
import com.kt.mail.entity.DrillMailContent;
import com.kt.mail.entity.DrillInfo;
import com.kt.mail.domain.DrillMailContentRepository;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender javaMailSender;
    private final DrillMailContentRepository drillMailContentRepository;

    @Value("${server.domain:localhost}")  // Spring의 @Value 사용
    private String serverDomain;

    @Value("${server.port:8080}")        // Spring의 @Value 사용
    private String serverPort;

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
            log.info("이메일 발송 시작: 수신자 {}명", recipients.size());
            
            if (recipients == null || recipients.isEmpty()) {
                log.warn("수신자 목록이 비어 있습니다");
                return;
            }
            
            for (Object recipientObj : recipients) {
                try {
                    // Map으로 변환하여 데이터 추출
                    Map<String, Object> recipient = (Map<String, Object>) recipientObj;
                    log.debug("수신자 데이터: {}", recipient);  // 데이터 구조 확인용 로그
                    
                    // 이메일 주소 추출 (여러 가능한 키 시도)
                    String email = null;
                    for (String key : new String[]{"empMail", "email", "mail"}) {
                        Object value = recipient.get(key);
                        if (value != null) {
                            email = value.toString();
                            break;
                        }
                    }
                    
                    // empId 추출 (여러 가능한 키 시도)
                    Integer empId = null;
                    for (String key : new String[]{"empId", "id", "employeeId"}) {
                        Object value = recipient.get(key);
                        if (value != null) {
                            if (value instanceof Number) {
                                empId = ((Number) value).intValue();
                            } else if (value instanceof String) {
                                empId = Integer.parseInt((String) value);
                            }
                            break;
                        }
                    }
                    
                    if (email == null || empId == null) {
                        log.warn("유효하지 않은 수신자 데이터: email={}, empId={}, raw={}", email, empId, recipient);
                        continue;
                    }

                    log.info("이메일 발송 시도: {}, 사원ID: {}", email, empId);
                    
                    // 추적 링크 생성 (실제 서버 도메인 사용)
                    String empIdHash = String.valueOf(empId.hashCode());
                    String trackingLink = String.format("http://%s:%s/track/%d/%s", 
                        serverDomain, serverPort, drillId, empIdHash);
                    
                    log.info("생성된 추적 링크: {}", trackingLink);
                    
                    // DrillMailContent 생성 및 저장
                    DrillMailContent mailContent = new DrillMailContent();
                    DrillInfo drillInfo = new DrillInfo();
                    drillInfo.setDrillId(drillId);
                    
                    mailContent.setDrillInfo(drillInfo);
                    mailContent.setEmployeeId(empId);
                    mailContent.setSubject(subject);
                    mailContent.setContent(body);
                    mailContent.setTrackingLink(trackingLink);
                    
                    drillMailContentRepository.save(mailContent);
                    log.info("메일 콘텐츠 저장 완료: empId={}", empId);
                    
                    // 이메일 발송
                    SimpleMailMessage message = new SimpleMailMessage();
                    message.setTo(email);
                    message.setSubject(subject != null ? subject : "테스트 제목");
                    
                    String fullBody = (body != null ? body : "테스트 내용") + 
                        "\n\n추적 링크: " + trackingLink;
                    
                    message.setText(fullBody);
                    javaMailSender.send(message);
                    log.info("메일 발송 성공: {}", email);
                } catch (Exception e) {
                    log.error("개별 메일 발송 실패: {}", e.getMessage(), e);
                    // 개별 실패는 기록하고 계속 진행
                    continue;
                }
            }
            
            log.info("모든 메일 발송 완료");
        } catch (Exception e) {
            log.error("메일 발송 실패: {}", e.getMessage(), e);
            throw new RuntimeException("메일 발송 중 오류 발생", e);
        }
    }
}
