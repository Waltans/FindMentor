package com.codeBuddy.codeBuddy.extern.Dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class KeywordDTO {

    @NotNull
    private String keyword;

    private List<Long> mentorsId = new ArrayList<>();
}
