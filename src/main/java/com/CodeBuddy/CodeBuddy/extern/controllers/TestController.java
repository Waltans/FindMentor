package com.CodeBuddy.CodeBuddy.extern.controllers;

import com.CodeBuddy.CodeBuddy.application.repository.RequestRepository;
import com.CodeBuddy.CodeBuddy.application.services.KeywordService;
import com.CodeBuddy.CodeBuddy.application.services.MentorService;
import com.CodeBuddy.CodeBuddy.application.services.RequestService;
import com.CodeBuddy.CodeBuddy.application.services.StudentService;
import com.CodeBuddy.CodeBuddy.domain.Keyword;
import com.CodeBuddy.CodeBuddy.domain.Request;
import com.CodeBuddy.CodeBuddy.domain.RequestState;
import com.CodeBuddy.CodeBuddy.domain.Users.Mentor;
import com.CodeBuddy.CodeBuddy.domain.Users.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/test")
public class TestController {
    private final MentorService mentorService;
    private final StudentService studentService;
    private final RequestService requestService;

    private final RequestRepository requestRepository;

    private final KeywordService keywordService;

    @Autowired
    public TestController(MentorService mentorService, StudentService studentService, RequestService requestService, RequestRepository requestRepository, KeywordService keywordService) {
        this.mentorService = mentorService;
        this.studentService = studentService;
        this.requestService = requestService;
        this.requestRepository = requestRepository;
        this.keywordService = keywordService;
    }

    @PostMapping("/request")
    public ResponseEntity<Request> createRequest(@RequestBody Request request) {
        requestService.saveRequest(request);
        return new ResponseEntity<>(request, HttpStatus.CREATED);
    }

    @PostMapping("/mentor")
    public ResponseEntity<Mentor> createMentor(@RequestBody Mentor mentor) {
        mentorService.saveMentor(mentor);
        return new ResponseEntity<>(mentor, HttpStatus.CREATED);
    }

    @PostMapping("/student")
    public ResponseEntity<Student> createStudent(@RequestBody Student student) {
        studentService.saveStudent(student);
        return new ResponseEntity<>(student, HttpStatus.CREATED);
    }

    @GetMapping("/request/{mentor}/{studentId}")
    public Request createRequest(@PathVariable Long mentor, @PathVariable Long studentId) {
        return studentService.createRequestForMentor(mentor, studentId, "description");
    }

    @PutMapping("/{id}")
    public Request updateRequest(@PathVariable("id") Long requestId) {
        Optional<Request> request = requestService.getRequestById(requestId);
        request.get().setRequestState(RequestState.SEND);
        requestService.saveRequest(request.get());
        return request.get();
    }


    @GetMapping("/student/{id}")
    public ResponseEntity<Student> getStudent(@PathVariable("id") Long id) {
        if (studentService.getStudentById(id).isPresent()) {
            return ResponseEntity.ok(studentService.getStudentById(id).get());
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/mentor/{id}")
    public Mentor getMentor(@PathVariable("id") Long id) {
        return mentorService.getMentorById(id).get();
    }

    @PostMapping("/updatePhoto")
    public ResponseEntity<Void> updatePhotoStudent(@RequestParam("image") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        File tempFile = File.createTempFile("temp", null);
        file.transferTo(tempFile);
        studentService.UpdatePhotoStudent(tempFile, 1L);
        return ResponseEntity.ok().build();
    }

    @RequestMapping("/keyword")
    public ResponseEntity<Void> createKeyword(@RequestBody Keyword keyword){
        keywordService.addKeyword(keyword);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/mentor/{mentorId}/{keywordId}")
    public ResponseEntity<Void> addKeyword(@PathVariable("mentorId") Long mentorId,
                                           @PathVariable("keywordId") Long keywordId){
        mentorService.addKeyword(mentorId, keywordId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/mentor/{mentorId}/{keywordId}")
    public ResponseEntity<Void> deleteKeyword(@PathVariable("mentorId") Long mentorId,
                                           @PathVariable("keywordId") Long keywordId){
        mentorService.removeKeyword(mentorId, keywordId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/mentor/keywords")
    public ResponseEntity<Page<Mentor>> getMentorsByKeywords(@RequestBody List<Long> keywordsId){
        Page<Mentor> mentorPage = mentorService.getMentorsByKeywords(keywordsId, PageRequest.of(0, 2));
        return new ResponseEntity<>(mentorPage, HttpStatus.OK);
    }

}
