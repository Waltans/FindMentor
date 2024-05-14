package com.CodeBuddy.CodeBuddy.extern.DTO.mentorDtos;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class MentorDtoWithoutContact {

    private String firstName;

    private String lastName;

    private String description;

    private String email;

    private String telegram;

    private String photoUrl;

    private List<Long> keywords = new ArrayList<>();
}
