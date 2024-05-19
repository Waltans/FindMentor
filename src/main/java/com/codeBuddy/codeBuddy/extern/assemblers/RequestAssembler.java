package com.codeBuddy.codeBuddy.extern.assemblers;

import com.codeBuddy.codeBuddy.domain.Request;
import com.codeBuddy.codeBuddy.extern.Dto.RequestDTO;
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
