package com.CodeBuddy.CodeBuddy.extern.DTO.studentDtos;


import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;

@Data
public class StudentUpdateInfoDTO {

    @Email
    String email;

    @Pattern(regexp = "(^@)([a-zA-Z0-9])")
    String telegram;

    String description;
}


