package com.codeBuddy.codeBuddy.extern.Dto.postDtos;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class PostDTO {

    private Long id;

    private String description;

    private Integer countOfLikes = 0;

    private LocalDateTime localDateTime;

    private List<CommentUnderPostDto> comments;

    private Long studentId;

    private List<String> urlPhoto;
}
