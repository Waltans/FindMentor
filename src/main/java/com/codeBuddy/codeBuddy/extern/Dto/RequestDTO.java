package com.codeBuddy.codeBuddy.extern.Dto;


import com.codeBuddy.codeBuddy.domain.RequestState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RequestDTO {

    private Long id;

    private RequestState requestState;
    private Long mentorId;
    private Long studentId;
    private String description;
}
