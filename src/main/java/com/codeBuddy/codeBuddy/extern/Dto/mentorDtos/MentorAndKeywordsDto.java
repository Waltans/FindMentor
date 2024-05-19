package com.codeBuddy.codeBuddy.extern.Dto.mentorDtos;

import com.codeBuddy.codeBuddy.extern.Dto.keywordDtos.GetKeywordsAndIdDto;
import lombok.Data;

import java.util.List;

@Data
public class MentorAndKeywordsDto {
    private List<MentorDtoWithoutContact> mentors;
    private List<GetKeywordsAndIdDto> keywords;

    public MentorAndKeywordsDto(List<MentorDtoWithoutContact> mentors, List<GetKeywordsAndIdDto> keywords) {
        this.mentors = mentors;
        this.keywords = keywords;
    }
}
