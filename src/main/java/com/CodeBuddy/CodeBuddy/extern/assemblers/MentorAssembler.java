package com.CodeBuddy.CodeBuddy.extern.assemblers;

import com.CodeBuddy.CodeBuddy.application.services.KeywordService;
import com.CodeBuddy.CodeBuddy.application.services.MentorService;
import com.CodeBuddy.CodeBuddy.application.services.RequestService;
import com.CodeBuddy.CodeBuddy.application.services.StudentService;
import com.CodeBuddy.CodeBuddy.domain.Users.Mentor;
import com.CodeBuddy.CodeBuddy.extern.DTO.MentorDTO;
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

    public MentorDTO mapToMentorDTO(Mentor mentor) {
        MentorDTO mentorDTO = modelMapper.map(mentor, MentorDTO.class);
        mapToMentorIdList(mentorDTO, mentor);
        return mentorDTO;

    }

    public Mentor mapToMentor(MentorDTO mentorDTO) {
        Mentor mentor = modelMapper.map(mentorDTO, Mentor.class);
        mapToMentorList(mentor, mentorDTO);
        return mentor;

    }

    private void mapToMentorIdList(MentorDTO mentorDTO, Mentor mentor){
        mentor.getKeywords().forEach(e -> mentorDTO.getKeywords().add(e.getId()));
        mentor.getAcceptedStudent().forEach(e -> mentorDTO.getAcceptedStudent().add(e.getId()));
        mentor.getRequests().forEach(e -> mentorDTO.getRequests().add(e.getId()));
    }

    private void mapToMentorList(Mentor mentor, MentorDTO mentorDTO){
        mentorDTO.getKeywords().forEach(e -> mentor.getKeywords().add(keywordService.getById(e).get()));
        mentorDTO.getRequests().forEach(e -> mentor.getRequests().add(requestService.getRequestById(e).get()));
        mentorDTO.getAcceptedStudent().forEach(e -> mentor.getAcceptedStudent().add(studentService.getStudentById(e).get()));
    }
}
