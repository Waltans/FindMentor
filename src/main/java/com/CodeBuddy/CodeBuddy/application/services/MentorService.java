package com.CodeBuddy.CodeBuddy.application.services;

import com.CodeBuddy.CodeBuddy.application.repository.MentorRepository;
import com.CodeBuddy.CodeBuddy.domain.Keyword;
import com.CodeBuddy.CodeBuddy.domain.Request;
import com.CodeBuddy.CodeBuddy.domain.RequestState;
import com.CodeBuddy.CodeBuddy.domain.Users.Mentor;
import com.CodeBuddy.CodeBuddy.domain.Users.Student;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;
import java.util.Optional;


@Slf4j
@Service
public class MentorService {

    private final MentorRepository mentorRepository;
    private final RequestService requestService;
    private final StudentService studentService;
    private final GoogleDriveService googleDriveService;
    private final KeywordService keywordService;

    @Autowired
    public MentorService(MentorRepository mentorRepository, RequestService requestService, StudentService studentService, KeywordService keywordService, GoogleDriveService googleDriveService) {
        this.mentorRepository = mentorRepository;
        this.requestService = requestService;
        this.studentService = studentService;
        this.googleDriveService = googleDriveService;
        this.keywordService = keywordService;
    }


    /**
     * Сохранение ментора
     *
     * @param mentor
     */
    public void saveMentor(Mentor mentor) {
        if (mentor != null) {
            mentorRepository.save(mentor);
            log.info("Ментор с id = {} сохранен в базу", mentor.getId());
        }
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
     * Обновление почты ментора
     *
     * @param mentorId
     * @param newEmail
     */
    public void updateEmail(Long mentorId, String newEmail) {
        Optional<Mentor> mentor = getMentorById(mentorId);
        mentor.ifPresent(value -> {
            value.setEmail(newEmail);
            mentorRepository.save(value);
            log.info("Пользователь с id ={} обновил почту", mentorId);
        });
    }

    /**
     * Обновить телеграм ментора
     *
     * @param mentorId
     * @param newTelegram
     */
    public void updateTelegram(Long mentorId, String newTelegram) {
        Optional<Mentor> mentor = getMentorById(mentorId);
        mentor.ifPresent(value -> {
            value.setEmail(newTelegram);
            mentorRepository.save(value);
            log.info("Ментор с id ={} обновил телеграм", mentorId);
        });
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
     * Метод для изменения статуса запроса
     *
     * @param requestId
     * @param mentorId
     */
    //TODO - 1
    public void changeStatusRequest(Long requestId, Long mentorId, Long studentId, RequestState requestState) {
        Optional<Request> request = requestService.getRequestById(requestId);
        Optional<Mentor> mentor = getMentorById(mentorId);
        Optional<Student> student = studentService.getStudentById(studentId);
        if (mentor.isPresent() && request.isPresent() && student.isPresent()) {
            if (request.get().getRequestState().equals(RequestState.SEND)) {
                request.get().setRequestState(requestState);
                log.info("Статус запроса с id = {} изменен на {} ментором с id ={}", requestId, requestState, mentorId);
                if (requestState.equals(RequestState.ACCEPTED)) {
                    mentor.get().getAcceptedStudent().add(student.get());
                    student.get().getAcceptedMentor().add(mentor.get());
                    request.get().setMentor(mentor.get());
                    request.get().setStudent(student.get());
                }
            }
            log.info("Изменение статуса запроса с id={} невозможно", requestId);
        }
    }

    /**
     * Метод для получения всех менторов
     *
     * @param pageable
     * @return
     */
    public Page<Mentor> getAllMentors(Pageable pageable) {
        log.info("Получены все менторы с пагинацией ");
        return mentorRepository.findAll(pageable);
    }

    /**
     * Метод для добавления ключевых слов
     * @param mentorId идентификатор ментора
     * @param keywordId идентификатор ключевого слова
     */
    public void addKeyword(Long mentorId, Long keywordId){
        Optional<Mentor> mentorOptional = mentorRepository.findById(mentorId);
        Optional<Keyword> keywordOptional = keywordService.getById(mentorId);
        if (keywordOptional.isPresent() && mentorOptional.isPresent()){
            Mentor mentor = mentorOptional.get();
            mentor.getKeywords().add(keywordOptional.get());
            mentorRepository.save(mentor);
            log.info("Ментору с id = {} добавлено ключевое слово с id = {}", mentorId, keywordId);
        }
        else
            log.info("Ментору с id = {} не добавлено ключевое слово с id = {}", mentorId, keywordId);
    }

    /**
     * Метод удаления ключевого слова
     * @param mentorId идентификатор ментора
     * @param keywordId идентификатор ключевого слова
     */
    public void removeKeyword(Long mentorId, Long keywordId){
        Optional<Mentor> mentorOptional = mentorRepository.findById(mentorId);
        Optional<Keyword> keywordOptional = keywordService.getById(mentorId);
        if (keywordOptional.isPresent() && mentorOptional.isPresent()){
            Mentor mentor = mentorOptional.get();
            mentor.getKeywords().remove(keywordOptional.get());
            mentorRepository.save(mentor);
            log.info("Ключевое слово с id = {} удалено у ментора с id = {}", mentorId, keywordId);
        }
        else
            log.info("Ключевое слово с id = {} удалено у ментора с id = {}", mentorId, keywordId);
    }

    /**
     * Метод получения менторов с определенными ключевыми словами
     * @param keywordId идентификатор ключевого слова
     * @param pageable Объект для пагинации
     * @return Страница с менторами
     */
    public Page<Mentor> getMentorsByKeywords(List<Long> keywordId, Pageable pageable){
        List<Keyword> keywords = keywordService.getAllKeywordsById(keywordId);
        Page<Mentor> mentorPage = mentorRepository.getMentorsByKeywordsIn(keywords, pageable);
        log.info("Получен список менторов с определенными ключевыми словами");
        return mentorPage;
    }


    public void answerToRequest(Long requestId, RequestState requestState){
        Optional<Request> optionalRequest = requestService.getRequestById(requestId);
        if (optionalRequest.isPresent()){
            Request request = optionalRequest.get();
            request.setRequestState(requestState);
            if(request.getRequestState().equals(RequestState.ACCEPTED)){
                Mentor mentor = request.getMentor();
                Student student = request.getStudent();
                mentor.getAcceptedStudent().add(request.getStudent());
                student.getAcceptedMentor().add(request.getMentor());
            }
            //TODO Проверить сохранение в лист запросов
            requestService.saveRequest(request);
            log.info("Статус запроса с id={} изменен на {}", requestId, requestState.name());
        }
        else {
            log.info("Статус запроса с id={} не изменен", requestId);
        }
    }
}
