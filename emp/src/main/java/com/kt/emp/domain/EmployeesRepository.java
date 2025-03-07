package com.kt.emp.domain;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

import com.kt.emp.entity.Employees;

@Component
public interface EmployeesRepository extends CrudRepository<Employees, Integer> {
  String query = """
select employee_id, first_name, last_name
from employees as e
inner join departments as d on e.department_id = d.department_id
inner join locations as l on d.location_id = l.location_id
inner join countries as c on l.country_id = c.country_id
inner join regions as r on c.region_id = r.region_id
where r.region_name = 'Americas';
      """;
  @Query(value=query, nativeQuery=true)
  List<Employees> findAllEmp();
}
