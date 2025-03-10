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
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getRecipients'");
    }

    public String getSubject() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getSubject'");
    }

    public String getBody() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getBody'");
    }
}
