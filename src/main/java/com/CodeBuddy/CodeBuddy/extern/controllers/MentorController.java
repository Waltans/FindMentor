package com.CodeBuddy.CodeBuddy.extern.controllers;

import com.CodeBuddy.CodeBuddy.application.services.MentorService;
import com.CodeBuddy.CodeBuddy.domain.Users.Mentor;
import com.CodeBuddy.CodeBuddy.extern.DTO.MentorDTO;
import com.CodeBuddy.CodeBuddy.extern.assemblers.MentorAssembler;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/mentor")
public class MentorController {

    private final MentorService mentorService;
    private final MentorAssembler mentorAssembler;

    @Autowired
    public MentorController(MentorService mentorService, MentorAssembler mentorAssembler) {
        this.mentorService = mentorService;
        this.mentorAssembler = mentorAssembler;
    }

    @PostMapping("/create")
    public ResponseEntity<String> create(@RequestBody @Valid MentorDTO mentorDTO){
        if(mentorService.saveMentor(mentorAssembler.mapToMentor(mentorDTO)))
            return ResponseEntity.ok().build();
        return ResponseEntity.badRequest().body("Пользователя с таким ID уже существует");
    }

    @GetMapping("/{id}")
    public ResponseEntity<MentorDTO> getMentorById(@PathVariable Long id){
        Optional<Mentor> optionalMentor = mentorService.getMentorById(id);
        if (optionalMentor.isPresent()) {
            Mentor mentor = optionalMentor.get();
            mentor.setEmail(null);
            mentor.setTelegram(null);
            return new ResponseEntity<>(mentorAssembler.mapToMentorDTO(mentor), HttpStatus.FOUND);
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/update-em-tg/{id}")
    public ResponseEntity<String> updateEmailAndTelegram(@PathVariable("id") Long id,
                                                       @RequestParam("email") String email,
                                                       @RequestParam("telegram")  String telegram){

        if(mentorService.updateEmailAndTelegram(id, email, telegram))
            return ResponseEntity.ok().build();
        return ResponseEntity.badRequest().body("Пользователя с таким ID уже существует");
    }

    @PutMapping("/update-photo/{id}")
    public ResponseEntity<String> updatePhoto(@PathVariable("id") Long id,
                                              @RequestParam("image") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        File tempFile = File.createTempFile("temp", null);
        file.transferTo(tempFile);
        mentorService.updatePhoto(id, tempFile);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{page}")
    public ResponseEntity<Page<MentorDTO>> getAllMentor(@PathVariable("page") int page){
        List<Mentor> mentorList = mentorService.getAllMentors();
        List<MentorDTO> mentorDTOList = mentorList.stream().map(mentorAssembler::mapToMentorDTO).toList();
        mentorList.forEach(mentor -> {
            mentor.setTelegram(null);
            mentor.setEmail(null);
        });

        Pageable pageable = PageRequest.of(page, mentorDTOList.size());
        Page<MentorDTO> mentorPage = new PageImpl<>(mentorDTOList, pageable, mentorDTOList.size());

        return new ResponseEntity<>(mentorPage, HttpStatus.OK);
    }


}
