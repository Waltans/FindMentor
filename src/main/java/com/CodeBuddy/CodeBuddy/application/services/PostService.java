package com.CodeBuddy.CodeBuddy.application.services;


import com.CodeBuddy.CodeBuddy.application.repository.PostRepository;
import com.CodeBuddy.CodeBuddy.domain.Post;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
public class PostService {

    private final PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }


    /**
     * Метод для создания поста
     *
     * @param post
     */
    public void createPost(Post post) {
        post.setLocalDateTime(LocalDateTime.now());
        postRepository.save(post);
        log.info("Создан новый пост с id ={}, учеником с id ={}", post.getId(), post.getStudent().getId());
    }
}
