package com.kt.mail.entity;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DrillRequest {
    private List<Recipient> recipients;
    private String subject;
    private String body;

    public List<Recipient> getRecipients() {
        return recipients;
    }

    public String getSubject() {
        return subject;
    }

    public String getBody() {
        return body;
    }
}
