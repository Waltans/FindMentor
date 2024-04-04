package com.CodeBuddy.CodeBuddy.application.repository;

import com.CodeBuddy.CodeBuddy.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
}
