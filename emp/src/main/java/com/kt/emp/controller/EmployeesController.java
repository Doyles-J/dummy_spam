package com.kt.emp.controller;

import org.springframework.web.bind.annotation.RestController;

import com.kt.emp.entity.Employees;
import com.kt.emp.service.EmployeesService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;

@CrossOrigin(origins = "http://localhost:5173") 
@RestController
public class EmployeesController {

  @Autowired
  private EmployeesService service;

  @GetMapping("/emp")
  public List<Employees> getEmployeesAll() {      
    return service.findAll();
  }









  
  // @GetMapping("/emp/{id}")
  // public Employees getEmployees(@PathVariable("id") Integer id) {
  //     return service.findById(id);
  // }
  
  // @PutMapping("/emp/{id}")
  // public String putEmployeeById(@PathVariable("id") Integer id, @RequestBody Employees entity) {
  //   service.putEmployeeById(id, entity);
  //   return "{}";
  // }
}
