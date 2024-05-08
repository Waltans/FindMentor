package com.CodeBuddy.CodeBuddy.extern.assemblers;

import com.CodeBuddy.CodeBuddy.application.services.MentorService;
import com.CodeBuddy.CodeBuddy.domain.Keyword;
import com.CodeBuddy.CodeBuddy.extern.DTO.KeywordDTO;
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

    public KeywordDTO mapToKeywordDTO(Keyword keyword) {
        KeywordDTO keywordDTO = modelMapper.map(keyword, KeywordDTO.class);
        mapToKeywordIdList(keywordDTO, keyword);
        return keywordDTO;

    }

    public Keyword mapToKeyword(KeywordDTO keywordDTO) {
        Keyword keyword = modelMapper.map(keywordDTO, Keyword.class);
        mapToKeywordList(keyword, keywordDTO);
        return keyword;

    }

    private void mapToKeywordIdList(KeywordDTO keywordDTO, Keyword keyword){
        keyword.getMentors().forEach(e -> keywordDTO.getMentorsId().add(e.getId()));
    }

    private void mapToKeywordList(Keyword keyword, KeywordDTO keywordDTO){
        keywordDTO.getMentorsId().forEach(e -> keyword.getMentors().add(mentorService.getMentorById(e).get()));
    }
}
