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
            content.setDrillId(drillInfo.getDrillId());  // drillId 설정
            content.setEmpId(recipient.getEmpId());
            content.setMailTitle(subject);
            content.setMailContent(body);

            String trackingLink = String.format("http://localhost:8080/track/%d/%d", 
                drillInfo.getDrillId(), recipient.getEmpId());
            content.setMailLink(trackingLink);
            
            mailContentRepository.save(content);
        }
    }
} 