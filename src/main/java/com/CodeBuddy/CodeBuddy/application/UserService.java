package com.CodeBuddy.CodeBuddy.application;


import com.CodeBuddy.CodeBuddy.application.repository.MentorRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserService {

    public final MentorRepository mentorRepository;

    public UserService(MentorRepository mentorRepository) {

        this.mentorRepository = mentorRepository;
    }


}
