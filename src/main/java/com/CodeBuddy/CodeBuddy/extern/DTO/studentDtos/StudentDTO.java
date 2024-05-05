package com.CodeBuddy.CodeBuddy.extern.DTO.studentDtos;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class StudentDTO {

    private String firstName;

    private String lastName;

    private String email;

    private String telegram;

    private List<Long> posts = new ArrayList<>();

    private List<Long> comments = new ArrayList<>();

    private List<Long> requests = new ArrayList<>();

    private String description;

    private List<Long> acceptedMentor = new ArrayList<>();

    private String photoUrl;

}
