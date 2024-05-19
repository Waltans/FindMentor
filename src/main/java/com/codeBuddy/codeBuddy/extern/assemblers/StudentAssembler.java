package com.codeBuddy.codeBuddy.extern.assemblers;

import com.codeBuddy.codeBuddy.domain.Users.Student;
import com.codeBuddy.codeBuddy.extern.Dto.studentDtos.CreateStudentDTO;
import com.codeBuddy.codeBuddy.extern.Dto.studentDtos.StudentDtoWithContact;
import com.codeBuddy.codeBuddy.extern.Dto.studentDtos.StudentDtoWithoutContact;
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

    public StudentDtoWithContact convertToDtoWithContact(Student student) {
        return modelMapper.map(student, StudentDtoWithContact.class);
    }
}
