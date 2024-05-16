package com.CodeBuddy.CodeBuddy.extern.assemblers;

import com.CodeBuddy.CodeBuddy.application.services.MentorService;
import com.CodeBuddy.CodeBuddy.domain.Keyword;
import com.CodeBuddy.CodeBuddy.extern.DTO.KeywordDTO;
import com.CodeBuddy.CodeBuddy.extern.DTO.keywordDtos.CreatedKeywordDto;
import com.CodeBuddy.CodeBuddy.extern.DTO.keywordDtos.GetKeywordsAndIdDto;
import com.CodeBuddy.CodeBuddy.extern.DTO.keywordDtos.GetKeywordsDto;
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
