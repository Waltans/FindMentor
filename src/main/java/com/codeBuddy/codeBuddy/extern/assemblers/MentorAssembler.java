package com.codeBuddy.codeBuddy.extern.assemblers;

import com.codeBuddy.codeBuddy.domain.Users.Mentor;
import com.codeBuddy.codeBuddy.extern.Dto.mentorDtos.MentorDtoWithContact;
import com.codeBuddy.codeBuddy.extern.Dto.mentorDtos.MentorDtoWithoutContact;
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
