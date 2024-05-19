package com.codeBuddy.codeBuddy.application.repository;

import com.codeBuddy.codeBuddy.domain.Users.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {
    Page<Student> findAll(Pageable pageable);

    Optional<Student> findByEmail(String email);
}
