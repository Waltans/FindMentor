package com.CodeBuddy.CodeBuddy.extern.controllers;


import com.CodeBuddy.CodeBuddy.application.services.MentorService;
import com.CodeBuddy.CodeBuddy.application.services.RequestService;
import com.CodeBuddy.CodeBuddy.application.services.StudentService;
import com.CodeBuddy.CodeBuddy.domain.Post;
import com.CodeBuddy.CodeBuddy.domain.Request;
import com.CodeBuddy.CodeBuddy.domain.RequestState;
import com.CodeBuddy.CodeBuddy.domain.Users.Mentor;
import com.CodeBuddy.CodeBuddy.domain.Users.Student;
import com.CodeBuddy.CodeBuddy.extern.DTO.PostDtos.CreatePostDTO;
import com.CodeBuddy.CodeBuddy.extern.DTO.PostDtos.CreatedPostDto;
import com.CodeBuddy.CodeBuddy.extern.DTO.studentDtos.*;
import com.CodeBuddy.CodeBuddy.extern.assambler.StudentAssembler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
    private final StudentAssembler assembler;

    @PostMapping
    public ResponseEntity<CreateStudentDTO> createStudent(@Valid @RequestBody CreateStudentDTO studentDTO) {
        Student student = new Student();
        student.setFirstName(studentDTO.getFirstName());
        student.setLastName(studentDTO.getLastName());
        student.setEmail(studentDTO.getEmail());
        if (studentDTO.getPassword().equals(studentDTO.getRepeatPassword())) {
            student.setPassword(studentDTO.getPassword());
            studentService.saveStudent(student);
            CreateStudentDTO createStudentDTO = assembler.convertToDto(student);
            return new ResponseEntity<>(createStudentDTO, HttpStatus.CREATED);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }


    @GetMapping("{id}")
    public ResponseEntity<StudentDtoWithoutContact> getStudent(@PathVariable("id") Long id) {
        Optional<Student> student = studentService.getStudentById(id);
        if (student.isPresent()) {
            StudentDtoWithoutContact studentDTOWithoutContact = assembler.convertToDtoWithoutContact(student.get());
            return new ResponseEntity<>(studentDTOWithoutContact, HttpStatus.OK);
        }
        return ResponseEntity.notFound().build();
    }
//      TODO
//    @GetMapping("profile")
//    public ResponseEntity<StudentDtoWithoutContact> profile() {
//        Optional<Student> student = studentService.getStudentById(id);
//        if (student.isPresent()) {
//            StudentDtoWithoutContact studentDTOWithoutContact = assembler.convertToDtoWithoutContact(student.get());
//            return new ResponseEntity<>(studentDTOWithoutContact, HttpStatus.OK);
//        }
//        return ResponseEntity.notFound().build();
//    }


    @PutMapping("{id}/settings")
    public ResponseEntity<Void> updateStudentInformation(@PathVariable Long id, @Valid @RequestBody StudentUpdateInfoDTO updateInfoDTO) {
        if (updateInfoDTO == null) {
            return ResponseEntity.badRequest().build();
        }
        Optional<Student> optionalStudent = studentService.getStudentById(id);
        if (optionalStudent.isPresent()) {
            studentService.updateInformation(optionalStudent.get(), updateInfoDTO.getEmail(),
                    updateInfoDTO.getTelegram(), updateInfoDTO.getDescription());
            return ResponseEntity.ok().build();
        } else {
            log.info("Student with id {} not found", id);
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
                                                  @Valid @RequestBody StudentCreateRequest request) {
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
    public ResponseEntity<?> createPost(@PathVariable Long id, @Valid @ModelAttribute CreatePostDTO postDTO) throws IOException {
        Optional<Student> student = studentService.getStudentById(id);
        if (student.isPresent()) {
            if (postDTO.getDescription() != null) {
                List<File> fileList = new ArrayList<>(3);
                if (postDTO.getFiles() != null && postDTO.getFiles().size() <= 3) {
                    for (MultipartFile file : postDTO.getFiles()) {
                        File tempFile = File.createTempFile("temp", null);
                        file.transferTo(tempFile);
                        fileList.add(tempFile);
                    }
                } else {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Количество файлов должно быть не более 3");
                }
                Post post = studentService.createPost(id, postDTO.getDescription(), fileList);
                CreatedPostDto createPostDTO = CreatedPostDto.builder()
                        .creatorId(id)
                        .description(postDTO.getDescription())
                        .postId(post.getId())
                        .urlsPhoto(post.getUrlPhoto())
                        .build();
                return new ResponseEntity<>(createPostDTO, HttpStatus.CREATED);
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

//    //    TODO Нужны DTO с контактами и без для ментора
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



