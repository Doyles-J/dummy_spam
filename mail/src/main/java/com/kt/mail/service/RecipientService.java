package com.kt.mail.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kt.mail.domain.RecipientRepository;
import com.kt.mail.entity.Recipient;

@Service
public class RecipientService {

    @Autowired
    private RecipientRepository repository;

    public List<Recipient> findAll() {
        return repository.findAllRec();
    }

    public void putRecipientById(Integer id, Recipient entity) {
        entity.setEmpId(id);
        repository.save(entity);
    }

    public Recipient findById(Integer id) {
        return repository.findById(id).orElse(null);
    }
}
