package com.CodeBuddy.CodeBuddy.application.services;


import com.CodeBuddy.CodeBuddy.application.repository.PostRepository;
import com.CodeBuddy.CodeBuddy.domain.Post;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

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
    public Post createPost(Post post) {
        post.setLocalDateTime(LocalDateTime.now());
        log.info("Создан новый пост, учеником с id ={}", post.getStudent().getId());
        return postRepository.save(post);
    }

    /**
     * Метод для поиска поста по id
     *
     * @param id
     * @return
     */
    public Optional<Post> getPostById(Long id) {
        Optional<Post> post = postRepository.findById(id);
        if (post.isPresent()) {
            log.info("Пост с id ={} найден ", id);
            return post;
        }
        log.info("Пост с id={} не найден", id);
        return post;
    }


}
