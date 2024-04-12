package com.CodeBuddy.CodeBuddy.application.services;


import com.CodeBuddy.CodeBuddy.application.repository.StudentRepository;
import com.CodeBuddy.CodeBuddy.domain.Comment;
import com.CodeBuddy.CodeBuddy.domain.Post;
import com.CodeBuddy.CodeBuddy.domain.Request;
import com.CodeBuddy.CodeBuddy.domain.Users.Mentor;
import com.CodeBuddy.CodeBuddy.domain.Users.Student;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
@Slf4j
public class StudentService {

    public final StudentRepository studentRepository;
    public final MentorService mentorService;
    public final RequestService requestService;
    public final PostService postService;
    public final CommentService commentService;

    public StudentService(StudentRepository studentRepository, @Lazy MentorService mentorService,
                          RequestService requestService, PostService postService, CommentService commentService) {
        this.studentRepository = studentRepository;
        this.mentorService = mentorService;
        this.requestService = requestService;
        this.postService = postService;
        this.commentService = commentService;
    }

    /**
     * Сохранение ученика в базу данных
     *
     * @param student
     */
    public void saveStudent(Student student) {
        studentRepository.save(student);
        log.info("Ученик с id = {} сохранен в базу данных", student.getId());
    }

    /**
     * Метод для поиска студента по id
     *
     * @param id
     * @return Optional<Student>
     */
    public Optional<Student> getStudentById(Long id) {
        Optional<Student> student = studentRepository.findById(id);
        if (student.isPresent()) {
            log.info("Пользователь с id = {} найден", id);
            return student;
        }
        log.info("пользователь с id ={} не найден", id);
        return student;
    }

    /**
     * Обновление почты ученика
     *
     * @param studentId
     * @param newEmail
     */
    public void updateEmail(Long studentId, String newEmail) {
        Optional<Student> student = getStudentById(studentId);
        student.ifPresent(value -> {
            value.setEmail(newEmail);
            studentRepository.save(value);
            log.info("Почта пользователя с id={} изменилась ", studentId);
        });
    }

    /**
     * Обновить телеграм ученика
     *
     * @param studentId
     * @param newTelegram
     */
    public void updateTelegram(Long studentId, String newTelegram) {
        Optional<Student> student = getStudentById(studentId);
        student.ifPresent(value -> {
            value.setTelegram(newTelegram);
            studentRepository.save(value);
            log.info("Телеграм ученика с id={} изменился ", studentId);
        });
    }

    /**
     * Метод для создания запроса студентом
     *
     * @param mentorId
     * @param studentId
     * @param description
     */
    public void createRequestForMentor(Long mentorId, Long studentId, String description) {
        Optional<Mentor> mentor = mentorService.getMentorById(mentorId);
        Optional<Student> student = getStudentById(studentId);
        if (student.isPresent() && mentor.isPresent()) {
            Request request = new Request();
            request.setStudent(student.get());
            request.setMentor(mentor.get());
            request.setDescription(description);
            requestService.saveRequest(request);
            log.info("Запрос учеником c id={} и ментором c id={} был отправлен на сохранение", studentId, mentorId);
        }
        else {
            log.info("Создать вопрос не удалось");
        }
    }

    /**
     * Метод для создания поста для общего просмотра
     *
     * @param studentId
     * @param description
     */
    public void createPost(Long studentId, String description) {
        Optional<Student> student = getStudentById(studentId);
        if (student.isPresent()) {
            Post post = new Post();
            post.setDescription(description);
            post.setStudent(student.get());
            postService.createPost(post);
            log.info("Пост созданный учеником c id={} передан на создание", studentId);
        }
        else {
            log.info("Ошибка создания поста");
        }
    }

    public void sendComment(Long studentId, Long postId, String content) {
        Optional<Student> student = getStudentById(studentId);
        Optional<Post> post = postService.getPostById(postId);
        if (student.isPresent() && post.isPresent()) {
            Comment comment = new Comment();
            comment.setStudent(student.get());
            comment.setDate(new Date());
            comment.setContent(content);
            comment.setPost(post.get());
            student.get().getComments().add(comment);
            studentRepository.save(student.get());
            commentService.createComment(comment);
            log.info("Комментарий передан на создание");
        }
        else {
            log.info("Не удалось создать комментарий");
        }
    }

}
