package com.codeBuddy.codeBuddy.extern.Dto.keywordDtos;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreatedKeywordDto {
    @NotNull
    private String keyword;
}
