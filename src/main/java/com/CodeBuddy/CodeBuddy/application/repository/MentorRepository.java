package com.CodeBuddy.CodeBuddy.application.repository;

import com.CodeBuddy.CodeBuddy.domain.Keyword;
import com.CodeBuddy.CodeBuddy.domain.Users.Mentor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface MentorRepository extends JpaRepository<Mentor, Long> {
    Page<Mentor> getMentorsByKeywordsIn(List<Keyword> keywords, Pageable pageable);

    Mentor getMentorByEmail(String email);
}
