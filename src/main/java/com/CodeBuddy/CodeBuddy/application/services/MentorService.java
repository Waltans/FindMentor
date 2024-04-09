package com.CodeBuddy.CodeBuddy.application.services;

import com.CodeBuddy.CodeBuddy.application.repository.MentorRepository;
import com.CodeBuddy.CodeBuddy.domain.Request;
import com.CodeBuddy.CodeBuddy.domain.RequestState;
import com.CodeBuddy.CodeBuddy.domain.Users.Mentor;
import com.CodeBuddy.CodeBuddy.domain.Users.Student;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Slf4j
@Service
public class MentorService {

    private final MentorRepository mentorRepository;
    private final RequestService requestService;
    private final StudentService studentService;

    @Autowired
    public MentorService(MentorRepository mentorRepository, RequestService requestService, StudentService studentService) {
        this.mentorRepository = mentorRepository;
        this.requestService = requestService;
        this.studentService = studentService;
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
     * Метод для изменения статуса запроса
     *
     * @param requestId
     * @param mentorId
     */
    //TODO - 1
    public void ChangeStatusRequest(Long requestId, Long mentorId, Long studentId, RequestState requestState) {
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
                }
            }
            log.info("Изменение статуса запроса с id={} невозможно", requestId);
        }
    }
}
