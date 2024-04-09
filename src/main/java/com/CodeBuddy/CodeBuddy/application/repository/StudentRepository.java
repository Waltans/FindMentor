package com.CodeBuddy.CodeBuddy.application.repository;

import com.CodeBuddy.CodeBuddy.domain.Users.Student;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student,Long> {
}
