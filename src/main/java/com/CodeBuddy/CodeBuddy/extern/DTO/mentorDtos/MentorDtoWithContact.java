package com.CodeBuddy.CodeBuddy.extern.DTO.mentorDtos;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class MentorDtoWithContact {

    private String firstName;

    private String lastName;

    private String description;

    private String photoUrl;

    private List<Long> keywords = new ArrayList<>();
}
