package com.CodeBuddy.CodeBuddy;

import com.CodeBuddy.CodeBuddy.application.repository.MentorRepository;
import com.CodeBuddy.CodeBuddy.application.services.MentorService;
import com.CodeBuddy.CodeBuddy.application.services.RequestService;
import com.CodeBuddy.CodeBuddy.application.services.StudentService;
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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MentorServiceTest {

    @Mock
    private MentorRepository mentorRepository;

    @Mock
    private RequestService requestService;

    @Mock
    private StudentService studentService;

    @InjectMocks
    private MentorService mentorService;

    private Mentor mentor;
    private Student student;
    private Request request;

    @BeforeEach
    void setUp() {
        mentor = new Mentor();
        mentor.setId(1L);

        student = new Student();
        student.setId(1L);

        request = new Request();
        request.setId(1L);
        request.setStudent(student);
        request.setMentor(mentor);
    }

    @Test
    void saveMentorTest() {
        mentorService.saveMentor(mentor);
        verify(mentorRepository, times(1)).save(mentor);
    }

    @Test
    void getMentorByIdTest() {
        when(mentorRepository.findById(1L)).thenReturn(Optional.of(mentor));
        Optional<Mentor> result = mentorService.getMentorById(1L);
        assertEquals(mentor, result.get());
        verify(mentorRepository, times(1)).findById(1L);
    }

//    @Test
//    void updateEmailTest() {
//        when(mentorRepository.findById(1L)).thenReturn(Optional.of(mentor));
//
//        mentorService.updateEmail(1L, "newemail@example.com");
//
//        verify(mentorRepository, times(1)).findById(1L);
//        assertEquals("newemail@example.com", mentor.getEmail());
//    }

    @Test
    void ChangeStatusRequestTest() {
        when(requestService.getRequestById(1L)).thenReturn(Optional.of(request));
        when(mentorRepository.findById(1L)).thenReturn(Optional.of(mentor));
        when(studentService.getStudentById(1L)).thenReturn(Optional.of(student));

        mentorService.changeStatusRequest(1L, 1L, 1L, RequestState.ACCEPTED);

        verify(requestService, times(1)).getRequestById(1L);
        verify(mentorRepository, times(1)).findById(1L);
        verify(studentService, times(1)).getStudentById(1L);
        assertEquals(RequestState.ACCEPTED, request.getRequestState());
        assertEquals(1, mentor.getAcceptedStudent().size());
        assertEquals(1, student.getAcceptedMentor().size());
    }

    @Test
    void ChangeStatusRequestInvalidTest() {
        when(requestService.getRequestById(1L)).thenReturn(Optional.of(request));
        when(mentorRepository.findById(1L)).thenReturn(Optional.of(mentor));
        when(studentService.getStudentById(1L)).thenReturn(Optional.of(student));

        request.setRequestState(RequestState.SEND);
        mentorService.changeStatusRequest(1L, 1L, 1L, RequestState.ACCEPTED);

        verify(requestService, times(1)).getRequestById(1L);
        verify(mentorRepository, times(0)).findById(1L);
        verify(studentService, times(0)).getStudentById(1L);
    }
}

