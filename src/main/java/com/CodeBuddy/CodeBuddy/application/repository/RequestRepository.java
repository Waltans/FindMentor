package com.CodeBuddy.CodeBuddy.application.repository;

import com.CodeBuddy.CodeBuddy.domain.Request;
import com.CodeBuddy.CodeBuddy.domain.RequestState;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RequestRepository extends JpaRepository<Request, Long> {
    Page<Request> getAllByRequestStateAndAndMentor_Id(RequestState requestState, Long mentorId, Pageable pageable);

    Optional<Request> findRequestByMentorIdAndStudentId(Long mentorId, Long studentId);
}