package com.CodeBuddy.CodeBuddy.extern.DTO.PostDtos;


import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
public class CreatePostDTO {

    Long creatorId;

    @NotNull
    @NotEmpty
    String description;

    @Size(max = 3)
    List<MultipartFile> files;
}
