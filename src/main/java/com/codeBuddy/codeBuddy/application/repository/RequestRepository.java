package com.codeBuddy.codeBuddy.application.repository;

import com.codeBuddy.codeBuddy.domain.Request;
import com.codeBuddy.codeBuddy.domain.RequestState;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RequestRepository extends JpaRepository<Request, Long> {
    Page<Request> getAllByRequestStateAndAndMentor_Id(RequestState requestState, Long mentorId, Pageable pageable);

    Optional<Request> findRequestByMentorIdAndStudentId(Long mentorId, Long studentId);
}