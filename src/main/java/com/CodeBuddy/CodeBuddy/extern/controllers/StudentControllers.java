package com.CodeBuddy.CodeBuddy.extern.controllers;


import com.CodeBuddy.CodeBuddy.application.services.MentorService;
import com.CodeBuddy.CodeBuddy.application.services.RequestService;
import com.CodeBuddy.CodeBuddy.application.services.StudentService;
import com.CodeBuddy.CodeBuddy.domain.Post;
import com.CodeBuddy.CodeBuddy.domain.Request;
import com.CodeBuddy.CodeBuddy.domain.RequestState;
import com.CodeBuddy.CodeBuddy.domain.Users.Mentor;
import com.CodeBuddy.CodeBuddy.domain.Users.Student;
import com.CodeBuddy.CodeBuddy.extern.DTO.postDtos.CreatePostDTO;
import com.CodeBuddy.CodeBuddy.extern.DTO.postDtos.CreatedPostDto;
import com.CodeBuddy.CodeBuddy.extern.DTO.studentDtos.*;
import com.CodeBuddy.CodeBuddy.extern.assemblers.MentorAssembler;
import com.CodeBuddy.CodeBuddy.extern.assemblers.StudentAssembler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final MentorService mentorService;
    private final RequestService requestService;
    private final StudentAssembler assembler;
    private final MentorAssembler mentorAssembler;

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
        log.info("Get student with id {}", id);
        Optional<Student> student = studentService.getStudentById(id);
        if (student.isPresent()) {
            StudentDtoWithoutContact studentDTOWithoutContact = assembler.convertToDtoWithoutContact(student.get());
            return new ResponseEntity<>(studentDTOWithoutContact, HttpStatus.OK);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("accounts")
    public ResponseEntity<StudentDtoWithContact> profile(@AuthenticationPrincipal UserDetails userDetails) {
        Optional<Student> student = studentService.findStudentByEmail(userDetails.getUsername());
        if (student.isPresent()) {
            StudentDtoWithContact studentDTOWithContact = assembler.convertToDtoWithContact(student.get());
            return new ResponseEntity<>(studentDTOWithContact, HttpStatus.OK);
        }
        return ResponseEntity.notFound().build();
    }


    @PutMapping("accounts/settings")
    public ResponseEntity<Void> updateStudentInformation(@AuthenticationPrincipal UserDetails userDetails, @Valid @RequestBody StudentUpdateInfoDTO updateInfoDTO) {
        if (updateInfoDTO == null) {
            return ResponseEntity.badRequest().build();
        }
        Optional<Student> optionalStudent = studentService.findStudentByEmail(userDetails.getUsername());
        if (optionalStudent.isPresent()) {
            studentService.updateInformation(optionalStudent.get(), updateInfoDTO.getEmail(),
                    updateInfoDTO.getTelegram(), updateInfoDTO.getDescription());
            return ResponseEntity.ok().build();
        } else {
            log.info("Student with id {} not found", userDetails.getUsername());
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("accounts/photo")
    public ResponseEntity<Void> updatePhoto(@AuthenticationPrincipal UserDetails userDetails, @RequestParam("image") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        Optional<Student> studentById = studentService.findStudentByEmail(userDetails.getUsername());
        if (studentById.isPresent()) {
            File tempFile = File.createTempFile("temp", null);
            file.transferTo(tempFile);
            studentService.updatePhotoStudent(tempFile, studentById.get());
            return ResponseEntity.ok().build();
        } else return ResponseEntity.notFound().build();
    }

    @PutMapping("accounts/security")
    public ResponseEntity<Void> updatePassword(@AuthenticationPrincipal UserDetails userDetails,
                                               @Valid @RequestBody UpdateSecurityStudent securityStudent) {
        Optional<Student> studentById = studentService.findStudentByEmail(userDetails.getUsername());
        if (studentById.isPresent()) {
            if (bCryptPasswordEncoder.matches(securityStudent.getPassword(), studentById.get().getPassword())) {
                studentService.updateSecurity(studentById.get(), securityStudent.getNewPassword(),
                        securityStudent.getEmail());
                return ResponseEntity.ok().build();
            }
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.notFound().build();
    }


    @PostMapping("requests/mentors/{mentorId}")
    public ResponseEntity<RequestDTO> sendRequest(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long mentorId,
                                                  @Valid @RequestBody StudentCreateRequest request) {
        Optional<Student> student = studentService.findStudentByEmail(userDetails.getUsername());
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


    @PostMapping("posts")
    public ResponseEntity<?> createPost(@AuthenticationPrincipal UserDetails userDetails,
                                        @Valid @ModelAttribute CreatePostDTO postDTO) throws IOException {
        Optional<Student> student = studentService.findStudentByEmail(userDetails.getUsername());
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
                Post post = studentService.createPost(student.get().getId(), postDTO.getDescription(), fileList);
                CreatedPostDto createPostDTO = CreatedPostDto.builder()
                        .creatorId(student.get().getId())
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


    @DeleteMapping("requests/{requestId}")
    public ResponseEntity<Void> cancelRequest(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long requestId) {
        Optional<Student> student = studentService.findStudentByEmail(userDetails.getUsername());
        Optional<Request> request = requestService.getRequestById(requestId);
        if (student.isPresent() && request.isPresent()) {
            studentService.cancelRequest(request.get(), student.get());
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("{id}/mentor/{mentorId}")
    public ResponseEntity<?> getMentor(@PathVariable Long id, @PathVariable Long mentorId) {
        Optional<Student> student = studentService.getStudentById(id);
        Optional<Mentor> mentor = mentorService.getMentorById(mentorId);
        if (student.isPresent() && mentor.isPresent()) {
            Mentor mentorDto = mentor.get();
            RequestState request = requestService.getRequestByMentorAndStudent(mentorId, id);
            if (!request.equals(RequestState.ACCEPTED)) {
                return new ResponseEntity<>(mentorAssembler.convertToDtoWithoutContact(mentorDto), HttpStatus.OK);
            }
            return new ResponseEntity<>(mentorAssembler.convertToDtoWithContact(mentorDto), HttpStatus.OK);
        }
        return ResponseEntity.notFound().build();
    }
}



