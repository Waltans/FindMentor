package com.codeBuddy.codeBuddy.extern.Dto.studentDtos;


import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;

@Data
public class StudentUpdateInfoDTO {

    @Email
    private String email;

    @Pattern(regexp = "(^@)([a-zA-Z0-9])")
    private String telegram;

    private String description;
}


