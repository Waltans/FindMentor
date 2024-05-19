package com.codeBuddy.codeBuddy.application.services;

import com.codeBuddy.codeBuddy.application.repository.CommentRepository;
import com.codeBuddy.codeBuddy.domain.Comment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class CommentService {

    private final CommentRepository commentRepository;

    @Autowired
    public CommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }


    /**
     * Метод для сохранения комментария
     *
     * @param comment
     */
    public void createComment(Comment comment) {
        commentRepository.save(comment);
    }

    /**
     * Метод для поиска комментария по id
     *
     * @param id
     * @return
     */
    public Optional<Comment> getCommentById(Long id) {
        Optional<Comment> comment = commentRepository.findById(id);
        if (comment.isPresent()) {
            log.info("Комментарий с id={} найден", id);
            return comment;
        }
        log.info("Комментарий с id ={} не найден", id);
        return comment;
    }

    /**
     * Метод для удаления комментария
     *
     * @param id
     */
    public void deleteComment(Long id) {
        Optional<Comment> comment = getCommentById(id);
        comment.ifPresent(commentRepository::delete);
        log.info("Комментарий с id={} удален", id);
    }

    //TODO
}
