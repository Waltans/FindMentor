package com.codeBuddy.codeBuddy.application.services;


import com.codeBuddy.codeBuddy.application.repository.MentorRepository;
import com.codeBuddy.codeBuddy.application.repository.StudentRepository;
import com.codeBuddy.codeBuddy.domain.Comment;
import com.codeBuddy.codeBuddy.domain.Post;
import com.codeBuddy.codeBuddy.domain.Request;
import com.codeBuddy.codeBuddy.domain.RequestState;
import com.codeBuddy.codeBuddy.domain.Users.Mentor;
import com.codeBuddy.codeBuddy.domain.Users.Student;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class StudentService implements UserDetailsService {

    public final StudentRepository studentRepository;
    public final MentorService mentorService;
    public final RequestService requestService;
    public final PostService postService;
    public final CommentService commentService;
    public final GoogleDriveService googleDriveService;
    public final PasswordEncoder passwordEncoder;
    private final MentorRepository mentorRepository;

    public StudentService(StudentRepository studentRepository, @Lazy MentorService mentorService,
                          RequestService requestService, PostService postService,
                          CommentService commentService, GoogleDriveService googleDriveService,
                          @Lazy PasswordEncoder passwordEncoder, MentorRepository mentorRepository1) {
        this.studentRepository = studentRepository;
        this.mentorService = mentorService;
        this.requestService = requestService;
        this.postService = postService;
        this.commentService = commentService;
        this.googleDriveService = googleDriveService;
        this.passwordEncoder = passwordEncoder;
        this.mentorRepository = mentorRepository1;
    }

    /**
     * Сохранение ученика в базу данных
     *
     * @param student
     */
    public void saveStudent(Student student) {
        String email = student.getEmail();
        if (mentorRepository.findByEmail(email).isEmpty() && findStudentByEmail(email).isEmpty()) {
            student.setPassword(passwordEncoder.encode(student.getPassword()));
            studentRepository.save(student);
            log.info("Ученик с id = {} сохранен в базу данных", student.getId());
        } else {
            log.info("Email already exists");
        }
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
     * @param student
     * @param newEmail
     */
    public void updateInformation(Student student, String newEmail, String newTelegram, String description) {
        if (newEmail != null) student.setEmail(newEmail);
        if (newTelegram != null) student.setTelegram(newTelegram);
        if (description != null) student.setDescription(description);
        studentRepository.save(student);
        log.info("Данные пользователя с id={} изменилась ", student.getId());
    }

    /**
     * Метод для создания запроса студентом
     *
     * @param description
     */
    public Request createRequestForMentor(Mentor mentor, Student student, String description) {
        if (student != null && mentor != null) {
            Request request = new Request();
            request.setRequestState(RequestState.SEND);
            request.setStudent(student);
            request.setMentor(mentor);
            student.getRequests().add(request);
            mentor.getRequests().add(request);
            request.setDescription(description);
            requestService.saveRequest(request);
            log.info("Запрос учеником c id={} и ментором c id={} был отправлен на сохранение", student.getId(), mentor.getId());
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
    public Post createPost(Long studentId, String description, List<File> files) {
        Optional<Student> student = getStudentById(studentId);
        if (student.isPresent()) {
            Post post = new Post();
            post.setDescription(description);
            post.setStudent(student.get());
            if (files != null && files.size() <= 3) {
                List<String> urls = addPhotoToPost(files);
                post.setUrlPhoto(urls);
            }
            log.info("Пост созданный учеником c id={} передан на создание", studentId);
            return postService.createPost(post);
        }
        return null;
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


    /**
     * Обновление фото студента
     *
     * @param file
     * @param student
     */
    public void updatePhotoStudent(File file, Student student) {
        String url = googleDriveService.uploadImageToDrive(file);
        student.setPhotoUrl(url);
        log.info("Пользователь с id={} сменил фотографию", student.getId());
        studentRepository.save(student);
    }

    public void sendComment(Long studentId, Long postId, String content) {
        Optional<Student> student = getStudentById(studentId);
        Optional<Post> post = postService.getPostById(postId);
        if (student.isPresent() && post.isPresent()) {
            Comment comment = new Comment();
            comment.setStudent(student.get());
            comment.setDate(LocalDateTime.now());
            comment.setContent(content);
            comment.setPost(post.get());
            student.get().getComments().add(comment);
            log.info("Комментарий передан на создание");
            commentService.createComment(comment);
        } else {
            log.info("Не удалось создать комментарий");
        }
    }

    /**
     * Метод для отмены запроса
     *
     * @param request
     * @param student
     */
    public void cancelRequest(Request request, Student student) {
        if (request.getStudent().getId().equals(student.getId())) {
            requestService.deleteRequest(request);
            log.info("Запрос с id={}, удален учеником с id={}", request.getId(), student.getId());

        }
    }

    public void updateSecurity(Student student, String newPassword, String newEmail) {
        if (newPassword != null) student.setPassword(passwordEncoder.encode(newPassword));
        if (student.getEmail() != null) student.setEmail(newEmail);
        studentRepository.save(student);
        log.info("Пользователь c id={} изменил пароль и почту", student.getId());
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<Student> student = studentRepository.findByEmail(email);
        if (student.isPresent()) {
            log.info("Найден ученик по данному email");
            return student.get();
        } else {
            Optional<Mentor> mentor = mentorRepository.findByEmail(email);
            if (mentor.isPresent()) {
                log.info("Найден ментор по данному email");
                return mentorService.loadUserByUsername(email);
            }
        }
        return null;
    }

    public Optional<Student> findStudentByEmail(String email) {
        return studentRepository.findByEmail(email);
    }
}
