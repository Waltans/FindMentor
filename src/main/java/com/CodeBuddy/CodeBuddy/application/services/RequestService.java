package com.CodeBuddy.CodeBuddy.application.services;


import com.CodeBuddy.CodeBuddy.application.repository.MentorRepository;
import com.CodeBuddy.CodeBuddy.application.repository.RequestRepository;
import com.CodeBuddy.CodeBuddy.application.repository.StudentRepository;
import com.CodeBuddy.CodeBuddy.domain.Request;
import com.CodeBuddy.CodeBuddy.domain.RequestState;
import com.CodeBuddy.CodeBuddy.domain.Users.Mentor;
import com.CodeBuddy.CodeBuddy.domain.Users.Student;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class RequestService {

    private final RequestRepository requestRepository;
    private final StudentRepository studentRepository;
    private final MentorRepository mentorRepository;
    private final MentorService mentorService;

    @Autowired
    public RequestService(RequestRepository requestRepository, StudentRepository studentRepository, MentorRepository mentorRepository, @Lazy MentorService mentorService) {
        this.requestRepository = requestRepository;
        this.studentRepository = studentRepository;
        this.mentorRepository = mentorRepository;
        this.mentorService = mentorService;
    }


    /**
     * Сохранение запроса в БД и установка состояния отправлен
     *
     * @param request
     */
    public void saveRequest(Request request) {
        requestRepository.save(request);
        log.info("Запрос с id = {} был сохранен в базу данных", request.getId());
    }

    /**
     * Получение запроса по id
     *
     * @param id
     * @return
     */

    public Optional<Request> getRequestById(Long id) {
        Optional<Request> request = requestRepository.findById(id);
        if (request.isPresent()) {
            log.info("Запрос с id = {} найден", id);
            return request;
        }
        log.info("Запрос с id ={} не найден", id);
        return request;
    }

    /**
     * Удаление запроса
     *
     * @param requestId
     */
    public void deleteRequest(Long requestId) {
        Optional<Request> request = getRequestById(requestId);
        if (request.isPresent()) {
            Student student = request.get().getStudent();
            student.getRequests().remove(request.get());
            Mentor mentor = request.get().getMentor();
            mentor.getRequests().remove(request.get());
            request.get().getMentor().getRequests().remove(request.get());
            requestRepository.delete(request.get());
            mentorRepository.save(mentor);
            studentRepository.save(student);
            log.info("Запрос с id={} удален", requestId);
        }
    }


    /**
     * Получение всех запросов с определенным статусом
     *
     * @param requestState - статус запроса
     * @param mentorId
     * @param pageable     - пагинация
     * @return
     */
    public Page<Request> getAllRequestWithState(RequestState requestState, Long mentorId, Pageable pageable) {
        Optional<Mentor> mentor = mentorService.getMentorById(mentorId);
        if (mentor.isPresent()) {
            return requestRepository.getAllByRequestStateAndAndMentor_Id(requestState, mentorId, pageable);
        }
        return null;
    }
}
