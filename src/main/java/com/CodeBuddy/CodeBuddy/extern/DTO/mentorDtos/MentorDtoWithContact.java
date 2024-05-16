package com.CodeBuddy.CodeBuddy.extern.DTO.mentorDtos;

import com.CodeBuddy.CodeBuddy.extern.DTO.keywordDtos.GetKeywordsAndIdDto;
import com.CodeBuddy.CodeBuddy.extern.DTO.keywordDtos.GetKeywordsDto;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class MentorDtoWithContact {
    private Long id;

    private String firstName;

    private String lastName;

    private String description;

    private String email;

    private String telegram;

    private String photoUrl;

    private List<GetKeywordsDto> keywords = new ArrayList<>();
}
