package com.CodeBuddy.CodeBuddy.extern.assemblers;

import com.CodeBuddy.CodeBuddy.domain.Request;
import com.CodeBuddy.CodeBuddy.extern.DTO.RequestDTO;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class RequestAssembler {


    public RequestDTO mapToRequestDTO(Request request) {
        return RequestDTO.builder()
                .requestState(request.getRequestState())
                .description(request.getDescription())
                .id(request.getId())
                .mentorId(request.getMentor().getId())
                .studentId(request.getStudent().getId())
                .build();
    }
}
