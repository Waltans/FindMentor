package com.CodeBuddy.CodeBuddy.application.services;

import com.CodeBuddy.CodeBuddy.application.repository.MentorRepository;
import com.CodeBuddy.CodeBuddy.domain.Keyword;
import com.CodeBuddy.CodeBuddy.domain.Request;
import com.CodeBuddy.CodeBuddy.domain.RequestState;
import com.CodeBuddy.CodeBuddy.domain.Users.Mentor;
import com.CodeBuddy.CodeBuddy.domain.Users.Student;
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
import java.util.Collections;
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

    @Autowired
    public MentorService(MentorRepository mentorRepository, RequestService requestService,
                         KeywordService keywordService, GoogleDriveService googleDriveService,
                         @Lazy PasswordEncoder passwordEncoder, StudentService studentService) {
        this.mentorRepository = mentorRepository;
        this.requestService = requestService;
        this.googleDriveService = googleDriveService;
        this.keywordService = keywordService;
        this.passwordEncoder = passwordEncoder;
        this.studentService = studentService;
    }


    /**
     * Сохранение ментора
     *
     * @param mentor
     */
    public void saveMentor(Mentor mentor) {
        if (mentorRepository.findByEmail(mentor.getEmail()).isEmpty() &&
            studentService.findStudentByEmail(mentor.getEmail()).isEmpty()){
            mentor.setPassword(passwordEncoder.encode(mentor.getPassword()));
            mentorRepository.save(mentor);
            log.info("Ментор с id = {} сохранен в базу", mentor.getId());
        }else
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
     * @param mentor ментор
     * @param newEmail новый email
     * @param newTelegram новый telegram
     * @param description новое описание
     * @param keywordsId список новых ключевых слов
     */
    public void updateInformation(Mentor mentor, String newEmail, String newTelegram, String description, List<Long> keywordsId) {
        mentor.setEmail(newEmail);
        mentor.setTelegram(newTelegram);
        mentor.setDescription(description);
        mentor.setKeywords(keywordService.getAllKeywordsById(keywordsId));
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
     * Метод для получения всех менторов
     *
     * @return
     */
    public List<Mentor> getAllMentors() {
        log.info("Получены все менторы с пагинацией ");
        return mentorRepository.findAll();
    }

    /**
     * Метод для добавления ключевых слов
     *
     * @param mentorId  идентификатор ментора
     * @param keywordId идентификатор ключевого слова
     */
    public boolean addKeyword(Long mentorId, Long keywordId) {
        Optional<Mentor> mentorOptional = mentorRepository.findById(mentorId);
        Optional<Keyword> keywordOptional = keywordService.getById(keywordId);
        if (keywordOptional.isPresent() && mentorOptional.isPresent()) {
            Mentor mentor = mentorOptional.get();
            mentor.getKeywords().add(keywordOptional.get());
            mentorRepository.save(mentor);
            log.info("Ментору с id = {} добавлено ключевое слово с id = {}", mentorId, keywordId);
            return true;
        }
        log.info("Ментору с id = {} не добавлено ключевое слово с id = {}", mentorId, keywordId);
        return false;
    }

    /**
     * Метод удаления ключевого слова
     *
     * @param mentorId  идентификатор ментора
     * @param keywordId идентификатор ключевого слова
     */
    public boolean removeKeyword(Long mentorId, Long keywordId) {
        Optional<Mentor> mentorOptional = mentorRepository.findById(mentorId);
        Optional<Keyword> keywordOptional = keywordService.getById(keywordId);
        if (keywordOptional.isPresent() && mentorOptional.isPresent()) {
            Mentor mentor = mentorOptional.get();
            mentor.getKeywords().remove(keywordOptional.get());
            mentorRepository.save(mentor);
            log.info("Ключевое слово с id = {} удалено у ментора с id = {}", mentorId, keywordId);
            return true;
        }
        log.info("Ключевое слово с id = {} удалено у ментора с id = {}", mentorId, keywordId);
        return false;
    }

    public void updateSecurity(Mentor mentor, String newPassword, String newEmail) {
        if (newPassword != null) mentor.setPassword(newPassword);
        if (mentor.getEmail() != null) mentor.setEmail(newEmail);
        mentorRepository.save(mentor);
        log.info("Пользователь c id={} изменил пароль и почту", mentor.getId());
    }

    /**
     * Метод получения менторов с определенными ключевыми словами
     *
     * @param keywordId идентификатор ключевого слова
     * @return Страница с менторами
     */
    public List<Mentor> getMentorsByKeywords(List<Long> keywordId) {
        List<Keyword> keywords = keywordService.getAllKeywordsById(keywordId);
        List<Mentor> mentorPage = mentorRepository.getMentorsByKeywordsIn(Collections.singleton(keywords));
        log.info("Получен список менторов с определенными ключевыми словами");
        return mentorPage;
    }


    public void answerToRequest(Long requestId, RequestState requestState) {
        Optional<Request> optionalRequest = requestService.getRequestById(requestId);
        if (optionalRequest.isPresent()) {
            Request request = optionalRequest.get();
            request.setRequestState(requestState);
            if (request.getRequestState().equals(RequestState.ACCEPTED)) {
                Mentor mentor = request.getMentor();
                Student student = request.getStudent();
                mentor.getAcceptedStudent().add(request.getStudent());
                student.getAcceptedMentor().add(request.getMentor());
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



}
