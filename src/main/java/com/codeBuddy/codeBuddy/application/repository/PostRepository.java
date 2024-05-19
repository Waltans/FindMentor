package com.codeBuddy.codeBuddy.application.repository;

import com.codeBuddy.codeBuddy.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
}
