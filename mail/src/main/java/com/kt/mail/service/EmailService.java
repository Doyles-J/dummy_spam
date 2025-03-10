package com.kt.mail.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import com.kt.mail.entity.Recipient;
import java.util.List;

@Service
public class EmailService {
    
    @Autowired
    private JavaMailSender mailSender;

    public void sendEmails(List<Recipient> recipients, String subject, String body, Long drillId) {
        for (Recipient recipient : recipients) {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(recipient.getEmpMail());
            message.setSubject(subject);
            
            // 추적 링크를 본문 끝에 추가
            String trackingLink = String.format("https://sites.google.com/view/alpbmailtest/홈/%d", 
                recipient.getEmpId());
            String fullBody = body + "\n\n" + trackingLink;
            message.setText(fullBody);
            
            mailSender.send(message);
        }
    }
}
