package com.kt.emp.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kt.emp.domain.EmployeesRepository;
import com.kt.emp.entity.Employees;

@Service
public class EmployeesService {

  @Autowired
  private EmployeesRepository repository;

  public List<Employees> findAll() {
    return repository.findAllEmp();
  }

  public void putEmployeeById(Integer id, Employees entity) {
    entity.setEmployeeId(id);
    repository.save(entity);
  }

  public Employees findById(Integer id) {
    return repository.findById(id).orElse(null);
  }
}
