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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;
import java.util.Optional;


@Slf4j
@Service
@RequiredArgsConstructor
public class MentorService implements UserDetailsService {

    private final MentorRepository mentorRepository;
    private final RequestService requestService;
    private final StudentService studentService;
    private final GoogleDriveService googleDriveService;
    private final KeywordService keywordService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public MentorService(MentorRepository mentorRepository, RequestService requestService, StudentService studentService, KeywordService keywordService, GoogleDriveService googleDriveService, PasswordEncoder passwordEncoder) {
        this.mentorRepository = mentorRepository;
        this.requestService = requestService;
        this.studentService = studentService;
        this.googleDriveService = googleDriveService;
        this.keywordService = keywordService;
        this.passwordEncoder = passwordEncoder;
    }


    /**
     * Сохранение ментора
     *
     * @param mentor
     */
    public boolean saveMentor(Mentor mentor) {
        if (!mentorRepository.findAll().stream().map(Mentor::getId).toList().contains(mentor.getId())) {
            mentor.setPassword(passwordEncoder.encode(mentor.getPassword()));
            mentorRepository.save(mentor);
            log.info("Ментор с id = {} сохранен в базу", mentor.getId());
            return true;
        }
        log.info("Ментор с id = {} уже существует", mentor.getId());
        return false;
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
     * Обновление почты и телеграма ментора
     *
     * @param mentorId
     * @param newEmail
     */
    public boolean updateEmailAndTelegram(Long mentorId, String newEmail, String newTelegram) {
        Optional<Mentor> optionalMentor = getMentorById(mentorId);
        if (optionalMentor.isPresent()) {
            Mentor mentor = optionalMentor.get();
            mentor.setEmail(newEmail);
            mentor.setTelegram(newTelegram);
            mentorRepository.save(mentor);
            log.info("Пользователь с id ={} обновил почту", mentorId);
            return true;
        }
        return false;
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
    public void addKeyword(Long mentorId, Long keywordId) {
        Optional<Mentor> mentorOptional = mentorRepository.findById(mentorId);
        Optional<Keyword> keywordOptional = keywordService.getById(keywordId);
        if (keywordOptional.isPresent() && mentorOptional.isPresent()) {
            Mentor mentor = mentorOptional.get();
            mentor.getKeywords().add(keywordOptional.get());
            mentorRepository.save(mentor);
            log.info("Ментору с id = {} добавлено ключевое слово с id = {}", mentorId, keywordId);
        } else
            log.info("Ментору с id = {} не добавлено ключевое слово с id = {}", mentorId, keywordId);
    }

    /**
     * Метод удаления ключевого слова
     *
     * @param mentorId  идентификатор ментора
     * @param keywordId идентификатор ключевого слова
     */
    public void removeKeyword(Long mentorId, Long keywordId) {
        Optional<Mentor> mentorOptional = mentorRepository.findById(mentorId);
        Optional<Keyword> keywordOptional = keywordService.getById(keywordId);
        if (keywordOptional.isPresent() && mentorOptional.isPresent()) {
            Mentor mentor = mentorOptional.get();
            mentor.getKeywords().remove(keywordOptional.get());
            mentorRepository.save(mentor);
            log.info("Ключевое слово с id = {} удалено у ментора с id = {}", mentorId, keywordId);
        } else
            log.info("Ключевое слово с id = {} удалено у ментора с id = {}", mentorId, keywordId);
    }

    /**
     * Метод получения менторов с определенными ключевыми словами
     *
     * @param keywordId идентификатор ключевого слова
     * @param pageable  Объект для пагинации
     * @return Страница с менторами
     */
    public Page<Mentor> getMentorsByKeywords(List<Long> keywordId, Pageable pageable) {
        List<Keyword> keywords = keywordService.getAllKeywordsById(keywordId);
        Page<Mentor> mentorPage = mentorRepository.getMentorsByKeywordsIn(keywords, pageable);
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
        return mentorRepository.getMentorByEmail(email);
    }
}
