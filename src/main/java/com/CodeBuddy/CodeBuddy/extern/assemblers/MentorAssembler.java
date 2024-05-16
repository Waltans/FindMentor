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


    @Autowired
    public MentorAssembler(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public MentorDtoWithoutContact convertToDtoWithoutContact(Mentor mentor) {
        return modelMapper.map(mentor, MentorDtoWithoutContact.class);

    }

    public MentorDtoWithContact convertToDtoWithContact(Mentor mentor) {
        return modelMapper.map(mentor, MentorDtoWithContact.class);

    }

}
