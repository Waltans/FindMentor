package com.CodeBuddy.CodeBuddy.extern.assemblers;

import com.CodeBuddy.CodeBuddy.domain.Users.Student;
import com.CodeBuddy.CodeBuddy.extern.DTO.studentDtos.CreateStudentDTO;
import com.CodeBuddy.CodeBuddy.extern.DTO.studentDtos.StudentDtoWithoutContact;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class StudentAssembler {

    private final ModelMapper modelMapper = new ModelMapper();

    public CreateStudentDTO convertToDto(Student student) {
        return modelMapper.map(student, CreateStudentDTO.class);
    }

    public StudentDtoWithoutContact convertToDtoWithoutContact(Student student) {
        return modelMapper.map(student, StudentDtoWithoutContact.class);
    }
}
