package com.CodeBuddy.CodeBuddy.extern.DTO;


import com.CodeBuddy.CodeBuddy.domain.RequestState;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RequestDTO {

    private Long id;

    private RequestState requestState;
    private Long mentorId;
    private Long studentId;
    private String description;
}
