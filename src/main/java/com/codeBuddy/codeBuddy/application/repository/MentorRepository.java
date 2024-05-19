package com.codeBuddy.codeBuddy.application.repository;

import com.codeBuddy.codeBuddy.domain.Keyword;
import com.codeBuddy.codeBuddy.domain.Users.Mentor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface MentorRepository extends JpaRepository<Mentor, Long> {

    List<Mentor> getMentorsByKeywordsIn(List<Keyword> keywords);

    Optional<Mentor> findByEmail(String email);
}
