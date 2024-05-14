package com.CodeBuddy.CodeBuddy.application.repository;

import com.CodeBuddy.CodeBuddy.domain.Keyword;
import com.CodeBuddy.CodeBuddy.domain.Users.Mentor;
import com.CodeBuddy.CodeBuddy.domain.Users.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;


@Repository
public interface MentorRepository extends JpaRepository<Mentor, Long> {

    List<Mentor> getMentorsByKeywordsIn(Collection<List<Keyword>> keywords);

    Optional<Mentor> findByEmail(String email);
}
