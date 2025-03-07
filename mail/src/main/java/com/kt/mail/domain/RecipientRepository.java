package com.kt.mail.domain;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

import com.kt.mail.entity.Recipient;

@Component
public interface RecipientRepository extends CrudRepository<Recipient, Integer> {
    String query = """
        SELECT emp_id, emp_name, emp_mail, dept_id, emp_rank
        FROM employee
        ORDER BY dept_id, emp_name
            """;
@Query(value = query, nativeQuery = true)
    List<Recipient> findAllRec();

}
