package com.CodeBuddy.CodeBuddy.extern.controllers;

import com.CodeBuddy.CodeBuddy.application.services.MentorService;
import com.CodeBuddy.CodeBuddy.domain.RequestState;
import com.CodeBuddy.CodeBuddy.domain.Users.Mentor;
import com.CodeBuddy.CodeBuddy.extern.DTO.mentorDtos.MentorDtoWithContact;
import com.CodeBuddy.CodeBuddy.extern.DTO.mentorDtos.MentorDtoWithoutContact;
import com.CodeBuddy.CodeBuddy.extern.DTO.mentorDtos.MentorUpdateInfoDTO;
import com.CodeBuddy.CodeBuddy.extern.DTO.studentDtos.CreateStudentDTO;
import com.CodeBuddy.CodeBuddy.extern.DTO.studentDtos.UpdateSecurityStudent;
import com.CodeBuddy.CodeBuddy.extern.assemblers.MentorAssembler;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("mentors")
public class MentorController {

    private final MentorService mentorService;
    private final MentorAssembler assembler;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public MentorController(MentorService mentorService, MentorAssembler assembler, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.mentorService = mentorService;
        this.assembler = assembler;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @PostMapping()
    public ResponseEntity<CreateStudentDTO> create(@RequestBody @Valid CreateStudentDTO dto){
        Mentor mentor = Mentor.builder().firstName(dto.getFirstName()).lastName(dto.getLastName())
                        .email(dto.getEmail()).password(dto.getPassword()).build();
        if(dto.getPassword().equals(dto.getRepeatPassword())){
            mentorService.saveMentor(mentor);
            return new ResponseEntity<>(dto, HttpStatus.CREATED);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @GetMapping("{id}")
    public ResponseEntity<MentorDtoWithoutContact> getMentor(@PathVariable Long id){
        Optional<Mentor> optionalMentor = mentorService.getMentorById(id);
        if (optionalMentor.isPresent()) {
            Mentor mentor = optionalMentor.get();
            return new ResponseEntity<>(assembler.convertToDtoWithoutContact(mentor), HttpStatus.FOUND);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/accounts")
    public ResponseEntity<MentorDtoWithContact> profile(@AuthenticationPrincipal UserDetails userDetails) {
        Optional<Mentor> mentor = mentorService.findMentorByEmail(userDetails.getUsername());
        if (mentor.isPresent()) {
            MentorDtoWithContact mentorDTO = assembler.convertToDtoWithContact(mentor.get());
            return new ResponseEntity<>(mentorDTO, HttpStatus.OK);
        }
        return ResponseEntity.notFound().build();
    }


    @PutMapping("accounts/settings")
    public ResponseEntity<Void> updateMentorInformation(@AuthenticationPrincipal UserDetails userDetails,
                                                          @RequestBody MentorUpdateInfoDTO mentorDTO){
        Optional<Mentor> optionalMentor = mentorService.findMentorByEmail(userDetails.getUsername());
        if(optionalMentor.isPresent()){
            mentorService.updateInformation(optionalMentor.get(), mentorDTO.getEmail(), mentorDTO.getTelegram(),
                    mentorDTO.getDescription(), mentorDTO.getKeywords());
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("accounts/photo")
    public ResponseEntity<String> updatePhoto(@AuthenticationPrincipal UserDetails userDetails,
                                              @RequestParam("image") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        Optional<Mentor> optionalMentor = mentorService.findMentorByEmail(userDetails.getUsername());
        if (optionalMentor.isPresent()) {
            File tempFile = File.createTempFile("temp", null);
            file.transferTo(tempFile);
            mentorService.updatePhoto(optionalMentor.get().getId(), tempFile);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("accounts/security")
    public ResponseEntity<Void> updatePassword(@AuthenticationPrincipal UserDetails userDetails,
                                               @Valid @RequestBody UpdateSecurityStudent securityStudent) {
        Optional<Mentor> optionalMentor = mentorService.findMentorByEmail(userDetails.getUsername());
        if (optionalMentor.isPresent()) {
            if (bCryptPasswordEncoder.matches(securityStudent.getPassword(), optionalMentor.get().getPassword())) {
                mentorService.updateSecurity(optionalMentor.get(), securityStudent.getNewPassword(),
                        securityStudent.getEmail());
                return ResponseEntity.ok().build();
            }
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping
    public ResponseEntity<List<MentorDtoWithoutContact>> getAllMentor(){
        List<Mentor> mentorList = mentorService.getAllMentors();
        return new ResponseEntity<>(mentorList.stream().map(assembler::convertToDtoWithoutContact).toList(), HttpStatus.OK);
    }


    @GetMapping("keywords")
    public ResponseEntity<List<MentorDtoWithoutContact>> getAllMentorsByKeywords(@RequestParam("keywordsId") List<Long> keywordsId){
        List<Mentor> mentorList = mentorService.getMentorsByKeywords(keywordsId);
        return new ResponseEntity<>(mentorList.stream().map(assembler::convertToDtoWithoutContact).toList(), HttpStatus.OK);
    }

    @PutMapping("requests")
    public ResponseEntity<String> respondToRequest(@AuthenticationPrincipal UserDetails userDetails,
                                                   Long requestId, @RequestParam("request") String stringRequestState){
        Optional<Mentor> optionalMentor = mentorService.findMentorByEmail(userDetails.getUsername());
        if (optionalMentor.isPresent() && Objects.equals(optionalMentor.get().getId(), requestId)){
            try {
                RequestState requestState = RequestState.valueOf(stringRequestState);
                mentorService.answerToRequest(requestId, requestState);
            } catch (IllegalArgumentException e) {
                return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<>(HttpStatus.OK);
        }
        else
            return new ResponseEntity<>(HttpStatus.METHOD_NOT_ALLOWED);
    }



}
