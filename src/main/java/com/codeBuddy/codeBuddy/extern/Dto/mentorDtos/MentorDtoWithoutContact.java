package com.codeBuddy.codeBuddy.extern.Dto.mentorDtos;

import com.codeBuddy.codeBuddy.extern.Dto.keywordDtos.GetKeywordsDto;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class MentorDtoWithoutContact {
    private Long id;

    private String firstName;

    private String lastName;

    private String description;

    private String photoUrl;

    private List<GetKeywordsDto> keywords = new ArrayList<>();
}
