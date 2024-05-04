package com.CodeBuddy.CodeBuddy.application.services;


import com.CodeBuddy.CodeBuddy.application.repository.StudentRepository;
import com.CodeBuddy.CodeBuddy.domain.Comment;
import com.CodeBuddy.CodeBuddy.domain.Post;
import com.CodeBuddy.CodeBuddy.domain.Request;
import com.CodeBuddy.CodeBuddy.domain.RequestState;
import com.CodeBuddy.CodeBuddy.domain.Users.Mentor;
import com.CodeBuddy.CodeBuddy.domain.Users.Student;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class StudentService {

    public final StudentRepository studentRepository;
    public final MentorService mentorService;
    public final RequestService requestService;
    public final PostService postService;
    public final CommentService commentService;
    public final GoogleDriveService googleDriveService;

    public StudentService(StudentRepository studentRepository, @Lazy MentorService mentorService,
                          RequestService requestService, PostService postService, CommentService commentService, GoogleDriveService googleDriveService) {
        this.studentRepository = studentRepository;
        this.mentorService = mentorService;
        this.requestService = requestService;
        this.postService = postService;
        this.commentService = commentService;
        this.googleDriveService = googleDriveService;
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
    public void updateInformation(Long studentId, String newEmail, String newTelegram, String description) {
        // TODO логин и пароль
        getStudentById(studentId).ifPresentOrElse(student -> {
            student.setEmail(newEmail);
            student.setTelegram(newTelegram);
            student.setDescription(description);
            studentRepository.save(student);
            log.info("Данные пользователя с id={} изменилась ", studentId);
        }, () -> log.info("Изменить данные не получилось "));
    }

    /**
     * Метод для создания запроса студентом
     *
     * @param mentorId
     * @param studentId
     * @param description
     */
    public Request createRequestForMentor(Long mentorId, Long studentId, String description) {
        Optional<Mentor> mentor = mentorService.getMentorById(mentorId);
        Optional<Student> student = getStudentById(studentId);
        if (student.isPresent() && mentor.isPresent()) {
            Request request = new Request();
            request.setRequestState(RequestState.SEND);
            request.setStudent(student.get());
            request.setMentor(mentor.get());
            student.get().getRequests().add(request);
            mentor.get().getRequests().add(request);
            request.setDescription(description);
            requestService.saveRequest(request);
            log.info("Запрос учеником c id={} и ментором c id={} был отправлен на сохранение", studentId, mentorId);
            return request;
        } else {
            log.info("Создать вопрос не удалось");
            return null;
        }
    }

    /**
     * Метод для создания поста для общего просмотра
     * МАКСИМУМ 3 ФОТОГРАФИИ
     *
     * @param studentId
     * @param description
     */
    public void createPost(Long studentId, String description, List<File> files) {
        Optional<Student> student = getStudentById(studentId);
        if (student.isPresent()) {
            Post post = new Post();
            post.setDescription(description);
            post.setStudent(student.get());
            if (files != null) {
                post.setUrlPhoto(addPhotoToPost(files));
            }
            log.info("Пост созданный учеником c id={} передан на создание", studentId);
            postService.createPost(post);
        } else {
            log.info("Ошибка создания поста");
        }
    }

    /**
     * Добавление фото к посту
     *
     * @param files - список файлов
     * @return список ссылок
     */
    public List<String> addPhotoToPost(List<File> files) {
        List<String> urls = new ArrayList<>(3);
        for (File file : files) {
            urls.add(googleDriveService.uploadImageToDrive(file));
        }
        return urls;
    }

    public void UpdatePhotoStudent(File file, Long studentId) {
        getStudentById(studentId).ifPresentOrElse(student -> {
            String url = googleDriveService.uploadImageToDrive(file);
            student.setPhotoUrl(url);
            log.info("Пользователь с id={} сменил фотографию", studentId);
            studentRepository.save(student);
        }, () -> log.info("Не удалось обновить фотографию у пользователя с id = {}", studentId));
    }

    public void sendComment(Long studentId, Long postId, String content) {
        Optional<Student> student = getStudentById(studentId);
        Optional<Post> post = postService.getPostById(postId);
        if (student.isPresent() && post.isPresent()) {
            Comment comment = new Comment();
            comment.setStudent(student.get());
            comment.setDate(LocalDate.now());
            comment.setContent(content);
            comment.setPost(post.get());
            student.get().getComments().add(comment);
            post.get().getComments().add(comment);
            commentService.createComment(comment);
            log.info("Комментарий передан на создание");
        } else {
            log.info("Не удалось создать комментарий");
        }
    }

//    public Mentor getMentorData(Long mentorId, Long studentId) {
//        //TODO
//        return null;
//    }

    /**
     * Метод для отмены запроса
     *
     * @param requestId
     * @param studentId
     */
    public void cancelRequest(Long requestId, Long studentId) {
        requestService.getRequestById(requestId).ifPresentOrElse(request -> {
            request.getStudent().getId().equals(studentId);
            requestService.deleteRequest(requestId);
            log.info("Запрос с id={}, удален учеником с id={}", requestId, studentId);
        }, () -> log.info("Не удалось отменить запрос с id={} ученику с id={}", requestId, studentId));
    }

    //TODO лайки постов
}
