package com.codeBuddy.codeBuddy.extern.Dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CommentDTO {

    private Long id;

    private String content;

    private LocalDateTime date;

    private Long student;

    private Long mentor;
}
