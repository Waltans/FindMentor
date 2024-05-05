package com.CodeBuddy.CodeBuddy.extern.DTO.studentDtos;


import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.File;
import java.util.List;

@Data
public class CreatePostDTO {


    @NotNull
    @NotEmpty
    String description;

    List<File> files;
}
