package com.CodeBuddy.CodeBuddy;

import com.CodeBuddy.CodeBuddy.application.repository.MentorRepository;
import com.CodeBuddy.CodeBuddy.application.repository.RequestRepository;
import com.CodeBuddy.CodeBuddy.application.repository.StudentRepository;
import com.CodeBuddy.CodeBuddy.application.services.MentorService;
import com.CodeBuddy.CodeBuddy.application.services.RequestService;
import com.CodeBuddy.CodeBuddy.domain.Request;
import com.CodeBuddy.CodeBuddy.domain.RequestState;
import com.CodeBuddy.CodeBuddy.domain.Users.Mentor;
import com.CodeBuddy.CodeBuddy.domain.Users.Student;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RequestServiceTest {

    @Mock
    private RequestRepository requestRepository;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private MentorRepository mentorRepository;

    @Mock
    private MentorService mentorService;

    @InjectMocks
    private RequestService requestService;

    private Request request;
    private Student student;
    private Mentor mentor;

    @BeforeEach
    void setUp() {
        student = new Student();
        student.setId(1L);

        mentor = new Mentor();
        mentor.setId(1L);

        request = new Request();
        request.setId(1L);
        request.setStudent(student);
        request.setMentor(mentor);
    }

    @Test
    void saveRequestTest() {
        requestService.saveRequest(request);
        assertEquals(RequestState.SEND, request.getRequestState());
        verify(requestRepository).save(request);
    }

    @Test
    void getRequestByIdTest() {
        when(requestRepository.findById(1L)).thenReturn(Optional.of(request));
        Optional<Request> result = requestService.getRequestById(1L);
        assertTrue(result.isPresent());
        assertEquals(request, result.get());
        verify(requestRepository).findById(1L);
    }

    @Test
    void getRequestByIdNotFoundTest() {
        when(requestRepository.findById(1L)).thenReturn(Optional.empty());
        Optional<Request> result = requestService.getRequestById(1L);
        assertFalse(result.isPresent());
        verify(requestRepository).findById(1L);
    }

    @Test
    void deleteRequestTest() {
        when(requestRepository.findById(1L)).thenReturn(Optional.of(request));

        requestService.deleteRequest(1L);

        verify(requestRepository).findById(1L);
        verify(requestRepository).delete(request);
        verify(studentRepository).save(student);
        verify(mentorRepository).save(mentor);
    }

    @Test
    void getAllRequestWithStateTest() {
        Pageable pageable = Pageable.unpaged();
        when(mentorService.getMentorById(1L)).thenReturn(Optional.of(mentor));
        when(requestRepository.getAllByRequestStateAndAndMentor_Id(RequestState.SEND, 1L, pageable)).thenReturn(Page.empty());

        Page<Request> result = requestService.getAllRequestWithState(RequestState.SEND, 1L, pageable);

        assertNotNull(result);
        assertEquals(Page.empty(), result);
        verify(mentorService).getMentorById(1L);
        verify(requestRepository).getAllByRequestStateAndAndMentor_Id(RequestState.SEND, 1L, pageable);
    }
}
