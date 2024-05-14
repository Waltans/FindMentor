package com.CodeBuddy.CodeBuddy.extern.assemblers;

import com.CodeBuddy.CodeBuddy.application.services.KeywordService;
import com.CodeBuddy.CodeBuddy.application.services.MentorService;
import com.CodeBuddy.CodeBuddy.application.services.RequestService;
import com.CodeBuddy.CodeBuddy.application.services.StudentService;
import com.CodeBuddy.CodeBuddy.domain.Users.Mentor;
import com.CodeBuddy.CodeBuddy.extern.DTO.MentorDTO;
import com.CodeBuddy.CodeBuddy.extern.DTO.mentorDtos.MentorDtoWithContact;
import com.CodeBuddy.CodeBuddy.extern.DTO.mentorDtos.MentorDtoWithoutContact;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MentorAssembler {


    private final ModelMapper modelMapper;
    private final MentorService mentorService;
    private final KeywordService keywordService;
    private final RequestService requestService;
    private final StudentService studentService;


    @Autowired
    public MentorAssembler(ModelMapper modelMapper, MentorService mentorService, KeywordService keywordService, RequestService requestService, StudentService studentService) {
        this.modelMapper = modelMapper;
        this.mentorService = mentorService;
        this.keywordService = keywordService;
        this.requestService = requestService;
        this.studentService = studentService;
    }

    public MentorDtoWithoutContact convertToDtoWithoutContact(Mentor mentor) {
        MentorDtoWithoutContact mentorDTO = modelMapper.map(mentor, MentorDtoWithoutContact.class);
        return mentorDTO;

    }

    public MentorDtoWithContact convertToDtoWithContact(Mentor mentor) {
        MentorDtoWithContact mentorDTO = modelMapper.map(mentor, MentorDtoWithContact.class);
        return mentorDTO;

    }

    private void mapToKeywordIdList(MentorDTO mentorDTO, Mentor mentor){
        mentor.getKeywords().forEach(e -> mentorDTO.getKeywords().add(e.getId()));
    }

    private void mapToKeywordList(Mentor mentor, MentorDTO mentorDTO){
        mentorDTO.getKeywords().forEach(e -> mentor.getKeywords().add(keywordService.getById(e).get()));
        mentorDTO.getRequests().forEach(e -> mentor.getRequests().add(requestService.getRequestById(e).get()));
        mentorDTO.getAcceptedStudent().forEach(e -> mentor.getAcceptedStudent().add(studentService.getStudentById(e).get()));
    }
}
