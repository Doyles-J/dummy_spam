package com.kt.mail.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kt.mail.entity.Recipient;
import com.kt.mail.service.RecipientService;

@CrossOrigin(origins = "http://localhost:5173")
@RestController

public class RecipientController {
    @Autowired
    private RecipientService service;

    @GetMapping("/recipients")
    public List<Recipient> getRecipientsAll() {
        return service.findAll();
    }
}
