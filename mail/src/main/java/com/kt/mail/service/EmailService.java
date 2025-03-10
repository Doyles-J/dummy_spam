package com.kt.mail.service;

import java.util.List;
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

    public void sendMimeMessage() {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
            // 메일을 받을 수신자 설정
            mimeMessageHelper.setTo("chm2006@naver.com");
            // 메일의 제목 설정
            mimeMessageHelper.setSubject("html 적용 테스트 메일 제목");
            // html 문법 적용한 메일의 내용
            String content = """
                    <!DOCTYPE html>
                    <html xmlns:th="http://www.thymeleaf.org">
                    <body>
                    <div style="margin:100px;">
                    <h1> 테스트 메일 </h1>
                    <br>

                    <div align="center" style="border:1px solid black;">
                    <h3> 테스트 메일 내용 </h3>
                    </div>
                    <br/>
                    </div>
                    </body>
                    </html>
                    """;
            // 메일의 내용 설정
            mimeMessageHelper.setText(content, true);
            javaMailSender.send(mimeMessage);
            log.info("메일 발송 성공!");
        } catch (Exception e) {
            log.info("메일 발송 실패!");
            throw new RuntimeException(e);
        }
    }

    public void sendEmails(List<Recipient> recipients, String subject, String body, Integer drillId) {
        try {
            // 직접 테스트 메일 발송 (recipients와 무관하게 실행)
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo("keev005@naver.com");  // 실제 존재하는 이메일 주소
            message.setSubject(subject != null ? subject : "테스트 제목");
            message.setText(body != null ? body : "테스트 내용");
            
            javaMailSender.send(message);
            log.info("메일 발송 성공: 수신자 1명");
            
            /* 수신자 목록 처리는 나중에
            // 수신자 목록이 null이거나 비어 있는지 확인
            if (recipients == null || recipients.isEmpty()) {
                log.warn("수신자 목록이 비어 있습니다");
                return;
            }
            
            // null이 아닌 유효한 이메일 주소만 필터링
            List<String> validEmails = recipients.stream()
                .map(Recipient::getEmpMail)
                .filter(email -> email != null && !email.isEmpty())
                .collect(Collectors.toList());
            
            // 유효한 이메일이 없으면 처리 중단
            if (validEmails.isEmpty()) {
                log.warn("유효한 이메일 주소가 없습니다");
                return;
            }
            */
        } catch (Exception e) {
            log.error("메일 발송 실패: {}", e.getMessage(), e);
            throw new RuntimeException("메일 발송 중 오류 발생", e);
        }
    }
}
