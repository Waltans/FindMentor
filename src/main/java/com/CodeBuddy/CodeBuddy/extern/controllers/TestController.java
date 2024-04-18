package com.CodeBuddy.CodeBuddy.extern.controllers;

import com.CodeBuddy.CodeBuddy.application.repository.RequestRepository;
import com.CodeBuddy.CodeBuddy.application.services.MentorService;
import com.CodeBuddy.CodeBuddy.application.services.RequestService;
import com.CodeBuddy.CodeBuddy.application.services.StudentService;
import com.CodeBuddy.CodeBuddy.domain.Request;
import com.CodeBuddy.CodeBuddy.domain.RequestState;
import com.CodeBuddy.CodeBuddy.domain.Users.Mentor;
import com.CodeBuddy.CodeBuddy.domain.Users.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/test")
public class TestController {
    private final MentorService mentorService;
    private final StudentService studentService;
    private final RequestService requestService;

    private final RequestRepository requestRepository;
    @Autowired
    public TestController(MentorService mentorService, StudentService studentService, RequestService requestService, RequestRepository requestRepository) {
        this.mentorService = mentorService;
        this.studentService = studentService;
        this.requestService = requestService;
        this.requestRepository = requestRepository;
    }
    @PostMapping("/request")
    public ResponseEntity<Request> createRequest(@RequestBody Request request){
        requestService.saveRequest(request);
        return new ResponseEntity<>(request, HttpStatus.CREATED);
    }

    @PostMapping("/mentor")
    public ResponseEntity<Mentor> createMentor(@RequestBody Mentor mentor){
        mentorService.saveMentor(mentor);
        return new ResponseEntity<>(mentor, HttpStatus.CREATED);
    }

    @PostMapping("/student")
    public ResponseEntity<Student> createStudent(@RequestBody Student student){
        studentService.saveStudent(student);
        return new ResponseEntity<>(student, HttpStatus.CREATED);
    }

    @GetMapping("/request/{mentor}/{studentId}")
    public Request createRequest(@PathVariable Long mentor, @PathVariable Long studentId) {
        return studentService.createRequestForMentor(mentor,studentId,"description");
    }

    @PutMapping("/{id}")
    public Request updateRequest(@PathVariable("id") Long requestId){
        Optional<Request> request = requestService.getRequestById(requestId);
        request.get().setRequestState(RequestState.SEND);
        requestService.saveRequest(request.get());
        return request.get();
    }


    @GetMapping("/student/{id}")
    public ResponseEntity<Student> getStudent(@PathVariable("id") Long id){
        if (studentService.getStudentById(id).isPresent()){
            return ResponseEntity.ok(studentService.getStudentById(id).get());
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/mentor/{id}")
    public Mentor getMentor(@PathVariable("id") Long id){
        return mentorService.getMentorById(id).get();
    }



}
