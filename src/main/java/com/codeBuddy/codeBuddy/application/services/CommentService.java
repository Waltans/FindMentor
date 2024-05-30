package com.codeBuddy.codeBuddy.application.services;

import com.codeBuddy.codeBuddy.application.repository.CommentRepository;
import com.codeBuddy.codeBuddy.application.repository.MentorRepository;
import com.codeBuddy.codeBuddy.application.repository.StudentRepository;
import com.codeBuddy.codeBuddy.domain.Comment;
import com.codeBuddy.codeBuddy.domain.Post;
import com.codeBuddy.codeBuddy.domain.Users.Mentor;
import com.codeBuddy.codeBuddy.domain.Users.Student;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final StudentService studentService;
    private final MentorService mentorService;
    private final PostService postService;
    private final MentorRepository mentorRepository;
    private final StudentRepository studentRepository;

    @Autowired
    public CommentService(CommentRepository commentRepository, StudentService studentService, MentorService mentorService, PostService postService, MentorRepository mentorRepository, StudentRepository studentRepository) {
        this.commentRepository = commentRepository;
        this.studentService = studentService;
        this.mentorService = mentorService;
        this.postService = postService;
        this.mentorRepository = mentorRepository;
        this.studentRepository = studentRepository;
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

    public Page<Comment> getCommentsByPost(Long postId, Pageable pageable) {
        log.info("Комментарии для поста найдены");
        return commentRepository.getCommentByPostId(postId, pageable);
    }

    public void sendComment(UserDetails userDetails, Long postId, String content) {
        Optional<Post> post = postService.getPostById(postId);
        if (post.isPresent()) {
            Comment comment = new Comment();
            if (userDetails.getAuthorities().equals("ROLE_MENTOR")) {
                Mentor mentor = mentorService.findMentorByEmail(userDetails.getUsername()).get();
                comment.setMentor(mentor);
                mentor.getComments().add(comment);
                mentorRepository.save(mentor);
            } else {
                Student student = studentService.findStudentByEmail(userDetails.getUsername()).get();
                comment.setStudent(student);
                student.getComments().add(comment);
                studentRepository.save(student);
            }
            comment.setDate(LocalDateTime.now());
            comment.setContent(content);
            comment.setPost(post.get());

            log.info("Комментарий передан на создание");
            this.createComment(comment);
        } else {
            log.info("Не удалось создать комментарий");
        }
    }
}
