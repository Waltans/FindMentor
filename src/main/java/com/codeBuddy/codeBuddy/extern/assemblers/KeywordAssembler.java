package com.codeBuddy.codeBuddy.extern.assemblers;

import com.codeBuddy.codeBuddy.application.services.MentorService;
import com.codeBuddy.codeBuddy.domain.Keyword;
import com.codeBuddy.codeBuddy.extern.Dto.keywordDtos.CreatedKeywordDto;
import com.codeBuddy.codeBuddy.extern.Dto.keywordDtos.GetKeywordsAndIdDto;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class KeywordAssembler {

    private final ModelMapper modelMapper;
    private final MentorService mentorService;


    @Autowired
    public KeywordAssembler(ModelMapper modelMapper, MentorService mentorService) {
        this.modelMapper = modelMapper;
        this.mentorService = mentorService;
    }

    public GetKeywordsAndIdDto mapToGetKeywordAndIdDTO(Keyword keyword) {
        return modelMapper.map(keyword, GetKeywordsAndIdDto.class);

    }

    public Keyword mapToKeyword(CreatedKeywordDto createdKeywordDto) {
        return modelMapper.map(createdKeywordDto, Keyword.class);

    }
}
