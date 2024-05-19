package com.codeBuddy.codeBuddy.extern.Dto.postDtos;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CreatedPostDto {
    Long postId;
    Long creatorId;
    String description;
    List<String> urlsPhoto;
}
