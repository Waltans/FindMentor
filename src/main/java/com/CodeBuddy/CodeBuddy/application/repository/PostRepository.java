package com.CodeBuddy.CodeBuddy.application.repository;

import com.CodeBuddy.CodeBuddy.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
}
