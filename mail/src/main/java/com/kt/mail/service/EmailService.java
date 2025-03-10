package com.kt.mail.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.kt.mail.entity.Recipient;

@Service
public class EmailService {

    public void sendEmails(List<Recipient> recipients, String subject, String body, Long id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'sendEmails'");
    }

}
