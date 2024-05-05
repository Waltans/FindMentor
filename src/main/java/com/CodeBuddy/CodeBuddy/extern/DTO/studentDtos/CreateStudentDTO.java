package com.CodeBuddy.CodeBuddy.extern.DTO.studentDtos;


import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
public class CreateStudentDTO {

    @NotEmpty
    @NotNull
    String name;
    @NotEmpty
    @NotNull
    String lastName;

    @Email
    @NotNull
    @NotEmpty
    String email;

    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$")
    @NotNull
    @NotEmpty
    String password;

    String repeatPassword;
}
