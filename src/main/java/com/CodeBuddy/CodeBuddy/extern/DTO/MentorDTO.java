package com.CodeBuddy.CodeBuddy.extern.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import org.checkerframework.common.aliasing.qual.Unique;

import java.util.ArrayList;
import java.util.List;

@Data
public class MentorDTO {

    @NotNull(message = "Поле итендификатора не может быть пустым")
    private Long id;

    @NotNull(message = "Поле имени не может быть пустым")
    private String firstName;

    @NotNull(message = "Поле фамилии не может быть пустым")
    private String lastName;

    @NotNull(message = "Поле пароля не может быть пустым")
    private String password;

    private int countConsultation = 0;

    @NotNull
    @Email
    private String email;

    private String telegram;

    private String description;

    private List<Long> keywords = new ArrayList<>();

    private List<Long> requests = new ArrayList<>();

    private List<Long> acceptedStudent = new ArrayList<>();

    private String urlPhoto;
}
