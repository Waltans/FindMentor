package com.codeBuddy.codeBuddy.application.repository;

import com.codeBuddy.codeBuddy.domain.Keyword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KeywordRepository extends JpaRepository<Keyword, Long> {
    List<Keyword> findAllByIdIn(List<Long> id);

    List<Keyword> findAllByKeywordIn(List<String> keywords);
}
