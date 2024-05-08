package com.CodeBuddy.CodeBuddy.extern.DTO;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class KeywordDTO {

    @NotNull
    private Long id;

    @NotNull
    private String keyword;

    private List<Long> mentorsId = new ArrayList<>();
}
