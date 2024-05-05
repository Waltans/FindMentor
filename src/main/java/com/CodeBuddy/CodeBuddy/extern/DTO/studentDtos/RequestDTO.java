package com.CodeBuddy.CodeBuddy.extern.DTO.studentDtos;


import com.CodeBuddy.CodeBuddy.domain.RequestState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class RequestDTO {

    private Long id;

    private RequestState requestState;
    private Long mentorId;
    private Long studentId;
    private String description;
}
