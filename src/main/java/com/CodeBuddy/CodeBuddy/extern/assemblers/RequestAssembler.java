package com.CodeBuddy.CodeBuddy.extern.assemblers;

import com.CodeBuddy.CodeBuddy.domain.Request;
import com.CodeBuddy.CodeBuddy.extern.DTO.studentDtos.RequestDTO;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class RequestAssembler {

    private final ModelMapper modelMapper;

    public RequestAssembler(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public RequestDTO mapToRequestDTO(Request request) {
        RequestDTO requestDTO = modelMapper.map(request, RequestDTO.class);
        requestDTO.setMentorId(request.getMentor().getId());
        requestDTO.setStudentId(request.getStudent().getId());
        return requestDTO;
    }
}
