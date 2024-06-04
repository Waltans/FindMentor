package com.codeBuddy.codeBuddy.application.services;

import com.codeBuddy.codeBuddy.application.repository.MentorRepository;
import com.codeBuddy.codeBuddy.domain.*;
import com.codeBuddy.codeBuddy.domain.Users.Mentor;
import com.codeBuddy.codeBuddy.domain.Users.Student;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Slf4j
@Service
@RequiredArgsConstructor
public class MentorService implements UserDetailsService {

    private final MentorRepository mentorRepository;
    private final RequestService requestService;
    private final GoogleDriveService googleDriveService;
    private final KeywordService keywordService;
    private final PasswordEncoder passwordEncoder;
    private final StudentService studentService;
    private final PostService postService;
    private final CommentService commentService;

    @Autowired
    public MentorService(MentorRepository mentorRepository, RequestService requestService,
                         KeywordService keywordService, GoogleDriveService googleDriveService,
                         @Lazy PasswordEncoder passwordEncoder, StudentService studentService, PostService postService, CommentService commentService) {
        this.mentorRepository = mentorRepository;
        this.requestService = requestService;
        this.googleDriveService = googleDriveService;
        this.keywordService = keywordService;
        this.passwordEncoder = passwordEncoder;
        this.studentService = studentService;
        this.postService = postService;
        this.commentService = commentService;
    }


    /**
     * Сохранение ментора
     *
     * @param mentor
     */
    public void saveMentor(Mentor mentor) {
        if (mentorRepository.findByEmail(mentor.getEmail()).isEmpty() &&
                studentService.findStudentByEmail(mentor.getEmail()).isEmpty()) {
            mentor.setPassword(passwordEncoder.encode(mentor.getPassword()));
            mentorRepository.save(mentor);
            log.info("Ментор с id = {} сохранен в базу", mentor.getId());
        } else
            log.info("Пользователь с id = {} уже существует", mentor.getId());
    }

    /**
     * Поиск ментора по Id
     *
     * @param id
     * @return Optional<Mentor>
     */
    public Optional<Mentor> getMentorById(Long id) {
        Optional<Mentor> mentor = mentorRepository.findById(id);
        if (mentor.isPresent()) {
            log.info("Пользователь с id = {} найден", id);
            return mentor;
        }
        log.info("Пользователь с id = {} не найден", id);
        return Optional.empty();
    }

    /**
     * Метод обновления информации о менторе
     *
     * @param mentor      ментор
     * @param newEmail    новый email
     * @param newTelegram новый telegram
     * @param description новое описание
     */
    public void updateInformation(Mentor mentor, String newEmail, String newTelegram, String description, List<String> keywordsName) {
        if (newEmail != null) mentor.setEmail(newEmail);
        if (newTelegram != null) mentor.setTelegram(newTelegram);
        if (description != null) mentor.setDescription(description);
        mentor.setKeywords(keywordService.getAllKeywordsByName(keywordsName));
        mentorRepository.save(mentor);
        log.info("Данные ментора с id={} изменилась ", mentor.getId());
    }

    /**
     * Метод сохраняет фотографии ментора
     *
     * @param mentorId
     * @param file
     */
    public void updatePhoto(Long mentorId, File file) {
        getMentorById(mentorId).ifPresentOrElse(mentor -> {
            mentor.setUrlPhoto(googleDriveService.uploadImageToDrive(file));
            log.info("Пользователь с id={} сменил фотографию", mentorId);
            mentorRepository.save(mentor);
        }, () -> log.info("Не удалось обновить фотографию у пользователя с id = {}", mentorId));
    }


    /**
     * Метод удаления ключевого слова
     *
     * @param mentorId идентификатор ментора
     */
    public void changeKeywords(Long mentorId, List<String> keywordsName) {
        Optional<Mentor> mentorOptional = mentorRepository.findById(mentorId);
        List<Keyword> keywords = keywordService.getAllKeywordsByName(keywordsName);
        if (mentorOptional.isPresent()) {
            Mentor mentor = mentorOptional.get();
            mentor.setKeywords(keywords);
            mentorRepository.save(mentor);
            log.info("Ключевое слова изменены у ментора с id = {}", mentorId);
        } else
            log.info("Ключевое слова не изменены у ментора с id = {}", mentorId);
    }

    /**
     * Метод для получения всех менторов
     *
     * @return
     */
    public List<Mentor> getAllMentors() {
        log.info("Получены все менторы с пагинацией ");
        return mentorRepository.findAll();
    }


    public void updateSecurity(Mentor mentor, String newPassword, String newEmail) {
        if (newPassword != null) mentor.setPassword(passwordEncoder.encode(newPassword));
        if (mentor.getEmail() != null) mentor.setEmail(newEmail);
        mentorRepository.save(mentor);
        log.info("Пользователь c id={} изменил пароль и почту", mentor.getId());
    }

    /**
     * Метод получения менторов с определенными ключевыми словами
     *
     * @return Страница с менторами
     */
    public List<Mentor> getMentorsByKeywords(List<String> keywordName) {
        List<Keyword> keywords = keywordService.getAllKeywordsByName(keywordName);
        List<Mentor> mentorPage = mentorRepository.getMentorsByKeywordsIn(keywords);
        log.info("Получен список менторов с определенными ключевыми словами");
        return mentorPage;
    }


    public void answerToRequest(Long requestId, RequestState requestState) {
        Optional<Request> optionalRequest = requestService.getRequestById(requestId);
        if (optionalRequest.isPresent()) {
            Request request = optionalRequest.get();
            request.setRequestState(requestState);
            Mentor mentor = request.getMentor();
            Student student = request.getStudent();
            if (request.getRequestState().equals(RequestState.ACCEPTED)) {
                mentor.getAcceptedStudent().add(request.getStudent());
                student.getAcceptedMentor().add(request.getMentor());
            } else {
                mentor.getAcceptedStudent().remove(request.getStudent());
                student.getAcceptedMentor().remove(request.getMentor());
            }
            requestService.saveRequest(request);
            log.info("Статус запроса с id={} изменен на {}", requestId, requestState.name());
        } else {
            log.info("Статус запроса с id={} не изменен", requestId);
        }
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return mentorRepository.findByEmail(email).get();
    }

    public Optional<Mentor> findMentorByEmail(String email) {
        return mentorRepository.findByEmail(email);
    }

    public void sendComment(Long mentorId, Long postId, String content) {
        Optional<Mentor> mentor = getMentorById(mentorId);
        Optional<Post> post = postService.getPostById(postId);
        if (mentor.isPresent() && post.isPresent()) {
            Comment comment = new Comment();
            comment.setMentor(mentor.get());
            comment.setDate(LocalDateTime.now());
            comment.setContent(content);
            comment.setPost(post.get());
            mentor.get().getComments().add(comment);
            log.info("Комментарий передан на создание");
            commentService.createComment(comment);
        } else {
            log.info("Не удалось создать комментарий");
        }
    }

}
