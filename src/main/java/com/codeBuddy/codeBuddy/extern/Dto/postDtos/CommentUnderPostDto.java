package com.codeBuddy.codeBuddy.extern.Dto.postDtos;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentUnderPostDto {

    private String content;

    private LocalDateTime date;

    private String photoUrl;

    private Long student;

    private Long mentor;
}
