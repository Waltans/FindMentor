package com.codeBuddy.codeBuddy.extern.controllers;

import com.codeBuddy.codeBuddy.application.services.KeywordService;
import com.codeBuddy.codeBuddy.application.services.MentorService;
import com.codeBuddy.codeBuddy.domain.RequestState;
import com.codeBuddy.codeBuddy.domain.Users.Mentor;
import com.codeBuddy.codeBuddy.extern.Dto.RequestDTO;
import com.codeBuddy.codeBuddy.extern.Dto.keywordDtos.GetKeywordsAndIdDto;
import com.codeBuddy.codeBuddy.extern.Dto.mentorDtos.MentorAndKeywordsDto;
import com.codeBuddy.codeBuddy.extern.Dto.mentorDtos.MentorDtoWithContact;
import com.codeBuddy.codeBuddy.extern.Dto.mentorDtos.MentorDtoWithoutContact;
import com.codeBuddy.codeBuddy.extern.Dto.mentorDtos.MentorUpdateInfoDTO;
import com.codeBuddy.codeBuddy.extern.Dto.studentDtos.CreateStudentDTO;
import com.codeBuddy.codeBuddy.extern.Dto.studentDtos.UpdateSecurityStudent;
import com.codeBuddy.codeBuddy.extern.assemblers.KeywordAssembler;
import com.codeBuddy.codeBuddy.extern.assemblers.MentorAssembler;
import com.codeBuddy.codeBuddy.extern.assemblers.RequestAssembler;
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
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("mentors")
public class MentorController {

    private final MentorService mentorService;
    private final MentorAssembler mentorAssembler;
    private final KeywordAssembler keywordAssembler;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final KeywordService keywordService;
    private final RequestAssembler requestAssembler;

    @Autowired
    public MentorController(MentorService mentorService, MentorAssembler mentorAssembler, KeywordAssembler keywordAssembler, BCryptPasswordEncoder bCryptPasswordEncoder, KeywordService keywordService, RequestAssembler requestAssembler) {
        this.mentorService = mentorService;
        this.mentorAssembler = mentorAssembler;
        this.keywordAssembler = keywordAssembler;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.keywordService = keywordService;
        this.requestAssembler = requestAssembler;
    }

    @PostMapping()
    public ResponseEntity<CreateStudentDTO> create(@RequestBody @Valid CreateStudentDTO dto) {
        Mentor mentor = Mentor.builder().firstName(dto.getFirstName()).lastName(dto.getLastName())
                .email(dto.getEmail()).password(dto.getPassword()).build();
        if (dto.getPassword().equals(dto.getRepeatPassword())) {
            mentorService.saveMentor(mentor);
            return new ResponseEntity<>(dto, HttpStatus.CREATED);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @GetMapping("{id}")
    public ResponseEntity<MentorDtoWithoutContact> getMentor(@PathVariable Long id) {
        Optional<Mentor> optionalMentor = mentorService.getMentorById(id);
        if (optionalMentor.isPresent()) {
            Mentor mentor = optionalMentor.get();
            return new ResponseEntity<>(mentorAssembler.convertToDtoWithoutContact(mentor), HttpStatus.FOUND);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("accounts")
    public ResponseEntity<MentorDtoWithContact> profile(@AuthenticationPrincipal UserDetails userDetails) {
        Optional<Mentor> mentor = mentorService.findMentorByEmail(userDetails.getUsername());
        if (mentor.isPresent()) {
            MentorDtoWithContact mentorDTO = mentorAssembler.convertToDtoWithContact(mentor.get());
            return new ResponseEntity<>(mentorDTO, HttpStatus.OK);
        }
        return ResponseEntity.notFound().build();
    }


    @PutMapping("accounts/settings")
    public ResponseEntity<Void> updateMentorInformation(@AuthenticationPrincipal UserDetails userDetails,
                                                        @RequestBody MentorUpdateInfoDTO mentorDTO) {
        Optional<Mentor> optionalMentor = mentorService.findMentorByEmail(userDetails.getUsername());
        if (optionalMentor.isPresent()) {
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
    public ResponseEntity<MentorAndKeywordsDto> getAllMentor() {
        List<Mentor> mentorList = mentorService.getAllMentors();
        return getMentorAndKeywordsDtoResponseEntity(mentorList);
    }


    @GetMapping("keywords")
    public ResponseEntity<MentorAndKeywordsDto> getAllMentorsByKeywords(@RequestParam("keywordsId") List<Long> keywordsId) {
        List<Mentor> mentorList = mentorService.getMentorsByKeywords(keywordsId);
        return getMentorAndKeywordsDtoResponseEntity(mentorList);
    }

    @PutMapping("accounts/keywords")
    public ResponseEntity<?> changeKeywords(@AuthenticationPrincipal UserDetails userDetails,
                                            @RequestParam("keywordId") List<Long> keywordsId) {
        Optional<Mentor> optionalMentor = mentorService.findMentorByEmail(userDetails.getUsername());
        if (optionalMentor.isPresent()) {
            mentorService.changeKeywords(optionalMentor.get().getId(), keywordsId);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().build();
    }

    @GetMapping("requests")
    public ResponseEntity<List<RequestDTO>> getAllRequests(@AuthenticationPrincipal UserDetails userDetails) {
        Optional<Mentor> optionalMentor = mentorService.findMentorByEmail(userDetails.getUsername());

        return optionalMentor.map(mentor -> {
                    List<RequestDTO> list = mentor.getRequests().stream()
                            .map(requestAssembler::mapToRequestDTO).toList();
                    return new ResponseEntity<>(list, HttpStatus.OK);
                })
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @PutMapping("requests/{requestId}")
    public ResponseEntity<?> respondToRequest(@AuthenticationPrincipal UserDetails userDetails
            , @RequestParam("request") String stringRequestState, @PathVariable Long requestId) {
        Optional<Mentor> optionalMentor = mentorService.findMentorByEmail(userDetails.getUsername());
        if (optionalMentor.isPresent()) {
            RequestState requestState = RequestState.valueOf(stringRequestState);
            mentorService.answerToRequest(requestId, requestState);
            return new ResponseEntity<>(HttpStatus.OK);
        } else
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    private ResponseEntity<MentorAndKeywordsDto> getMentorAndKeywordsDtoResponseEntity(List<Mentor> mentorList) {
        List<MentorDtoWithoutContact> mentors = mentorList.stream().map(mentorAssembler::convertToDtoWithoutContact).toList();
        List<GetKeywordsAndIdDto> keywords = keywordService.getAllKeywords().stream().map(keywordAssembler::mapToGetKeywordAndIdDTO).toList();
        MentorAndKeywordsDto mentorAndKeywordsDto = new MentorAndKeywordsDto(mentors, keywords);
        return new ResponseEntity<>(mentorAndKeywordsDto, HttpStatus.OK);
    }
}
