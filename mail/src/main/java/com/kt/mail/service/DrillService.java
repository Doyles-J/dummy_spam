package com.kt.mail.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kt.mail.domain.DrillInfoRepository;
import com.kt.mail.domain.DrillMailContentRepository;
import com.kt.mail.entity.DrillInfo;
import com.kt.mail.entity.DrillMailContent;
import com.kt.mail.entity.Recipient;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class DrillService {
    @Autowired
    private DrillInfoRepository drillInfoRepository;
    @Autowired
    private DrillMailContentRepository mailContentRepository;
    @Autowired
    private EmailService emailService;

    public DrillInfo createNewDrill() {
        DrillInfo drillInfo = new DrillInfo();
        drillInfo.setDrillDate(LocalDateTime.now());
        return drillInfoRepository.save(drillInfo);
    }

    public void saveDrillMailContent(DrillInfo drillInfo, List<Recipient> recipients, 
                                   String subject, String body) {
        for (Recipient recipient : recipients) {
            DrillMailContent content = new DrillMailContent();
            content.setDrillInfo(drillInfo);
            content.setEmpId(recipient.getEmpId());
            content.setSubject(subject);
            content.setContent(body);
            content.setTrackingLink(generateTrackingLink(drillInfo.getId(), recipient.getEmpId()));
            mailContentRepository.save(content);
        }
    }

    private String generateTrackingLink(Long drillId, Integer empId) {
        return String.format("https://sites.google.com/view/alpbmailtest/홈/"+empId);
    }
} 