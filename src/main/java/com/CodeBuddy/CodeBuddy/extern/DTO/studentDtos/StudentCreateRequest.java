package com.CodeBuddy.CodeBuddy.extern.DTO.studentDtos;


import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class StudentCreateRequest {

    @NotEmpty
    @NotNull
    String description;
}
