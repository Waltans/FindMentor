package com.codeBuddy.codeBuddy.extern.Dto.mentorDtos;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;
import java.util.ArrayList;
import java.util.List;

@Data
public class MentorUpdateInfoDTO {

    @Email
    private String email;

    @Pattern(regexp = "(^@)([a-zA-Z0-9])")
    private String telegram;

    private String description;

    private List<String> keywords = new ArrayList<>();
}
