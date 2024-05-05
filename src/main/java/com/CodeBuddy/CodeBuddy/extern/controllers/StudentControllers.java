package com.CodeBuddy.CodeBuddy.extern.controllers;


import com.CodeBuddy.CodeBuddy.application.services.MentorService;
import com.CodeBuddy.CodeBuddy.application.services.RequestService;
import com.CodeBuddy.CodeBuddy.application.services.StudentService;
import com.CodeBuddy.CodeBuddy.domain.Request;
import com.CodeBuddy.CodeBuddy.domain.Users.Mentor;
import com.CodeBuddy.CodeBuddy.domain.Users.Student;
import com.CodeBuddy.CodeBuddy.extern.DTO.studentDtos.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.util.Optional;

@RestController
@RequestMapping("students")
@Slf4j
@RequiredArgsConstructor
public class StudentControllers {

    private final StudentService studentService;
    //    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final MentorService mentorService;
    private final RequestService requestService;


    @PostMapping
    public ResponseEntity<Student> createStudent(@Valid @RequestBody CreateStudentDTO studentDTO) {
        Student student = new Student();
        student.setFirstName(studentDTO.getName());
        student.setLastName(studentDTO.getLastName());
        student.setEmail(studentDTO.getEmail());
        if (studentDTO.getPassword().equals(studentDTO.getRepeatPassword())) {
            student.setPassword(studentDTO.getPassword());
            studentService.saveStudent(student);
            return new ResponseEntity<>(student, HttpStatus.CREATED);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @GetMapping("{id}")
    public ResponseEntity<Student> getStudent(@PathVariable("id") Long id) {
        return studentService.getStudentById(id).map(value ->
                        new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }


    @PutMapping("{id}/settings")
    public ResponseEntity<Void> updateStudentInformation(@PathVariable Long id, @Valid @RequestBody StudentUpdateInfoDTO updateInfoDTO) {
        if (updateInfoDTO == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Optional<Student> optionalStudent = studentService.getStudentById(id);
        if (optionalStudent.isPresent()) {
            studentService.updateInformation(optionalStudent.get(), updateInfoDTO.getEmail(),
                    updateInfoDTO.getTelegram(), updateInfoDTO.getDescription());
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("{id}/photo")
    public ResponseEntity<Void> updatePhoto(@PathVariable Long id, @RequestParam("image") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        Optional<Student> studentById = studentService.getStudentById(id);
        if (studentById.isPresent()) {
            File tempFile = File.createTempFile("temp", null);
            file.transferTo(tempFile);
            studentService.updatePhotoStudent(tempFile, studentById.get());
            return ResponseEntity.ok().build();
        } else return ResponseEntity.notFound().build();
    }

//    @PutMapping("{id}/security")
//    public ResponseEntity<Void> updatePassword(@PathVariable Long id,
//                                               @Valid @RequestBody UpdateSecurityStudent securityStudent) {
//
//        Optional<Student> studentById = studentService.getStudentById(id);
//        if (studentById.isPresent()) {
//            if (bCryptPasswordEncoder.matches(securityStudent.getPassword(), studentById.get().getPassword())) {
//                studentService.updateSecurity(studentById.get(), securityStudent.getNewPassword(),
//                        securityStudent.getEmail());
//                return ResponseEntity.ok().build();
//            }
//            return ResponseEntity.badRequest().build();
//        }
//        return ResponseEntity.notFound().build();
//    }


    @PostMapping("{id}/requests/mentors/{mentorId}")
    public ResponseEntity<RequestDTO> sendRequest(@PathVariable Long id, @PathVariable Long mentorId,
                                                  @RequestBody StudentCreateRequest request) {
        Optional<Student> student = studentService.getStudentById(id);
        Optional<Mentor> mentor = mentorService.getMentorById(mentorId);
        if (student.isPresent() && mentor.isPresent()) {
            if (request.getDescription() != null) {
                Request request1 = studentService.createRequestForMentor(mentor.get(), student.get(), request.getDescription());
                RequestDTO requestDTO = new RequestDTO(request1.getId(), request1.getRequestState(),
                        request1.getMentor().getId(), request1.getStudent().getId(), request1.getDescription());
                return new ResponseEntity<>((requestDTO), HttpStatus.CREATED);
            }
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.notFound().build();
    }


    @PostMapping("{id}/posts")
    public ResponseEntity<Void> createPost(@PathVariable Long id, @RequestBody CreatePostDTO postDTO) {
        Optional<Student> student = studentService.getStudentById(id);
        if (student.isPresent()) {
            if (postDTO.getDescription() != null) {
                studentService.createPost(id, postDTO.getDescription(), postDTO.getFiles());
                return ResponseEntity.ok().build();
            }
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.notFound().build();
    }


    @DeleteMapping("{id}/requests/{requestId}")
    public ResponseEntity<Void> cancelRequest(@PathVariable Long id, @PathVariable Long requestId) {
        Optional<Student> student = studentService.getStudentById(id);
        Optional<Request> request = requestService.getRequestById(requestId);
        if (student.isPresent() && request.isPresent()) {
            studentService.cancelRequest(request.get(), student.get());
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

//    TODO Нужны DTO с контактами и без для ментора
//    @GetMapping("{id}/mentor/{mentorId}")
//    public ResponseEntity<?> getMentor(@PathVariable Long id, @PathVariable Long mentorId) {
//        Optional<Student> student = studentService.getStudentById(id);
//        Optional<Mentor> mentor = mentorService.getMentorById(mentorId);
//        if (student.isPresent() && mentor.isPresent()) {
//            if (requestService.getRequestByMentorAndStudent(mentorId, id).equals(RequestState.ACCEPTED)) {
//                return new ResponseEntity<>.ok(MentorDto, HttpStatus.OK);
//            }
//            return new ResponseEntity<>.ok(MentorDtoWithoutContact, HttpStatus.OK);
//        }
//        return ResponseEntity.notFound().build();
//    }
}



