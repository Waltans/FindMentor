package com.CodeBuddy.CodeBuddy.application.services;


import com.CodeBuddy.CodeBuddy.application.repository.RequestRepository;
import com.CodeBuddy.CodeBuddy.domain.Users.Mentor;
import com.CodeBuddy.CodeBuddy.domain.Request;
import com.CodeBuddy.CodeBuddy.domain.RequestState;
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
    private final MentorService mentorService;

    @Autowired
    public RequestService(RequestRepository requestRepository, @Lazy MentorService mentorService) {
        this.requestRepository = requestRepository;
        this.mentorService = mentorService;
    }


    /**
     * Сохранение запроса в БД и установка состояния отправлен
     *
     * @param request
     */
    public void saveRequest(Request request) {
        request.setRequestState(RequestState.SEND);
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
     * Получение всех запросов с определенным статусом
     *
     * @param requestState
     * @param mentorId
     * @param pageable
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
