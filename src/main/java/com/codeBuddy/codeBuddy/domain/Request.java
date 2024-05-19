package com.codeBuddy.codeBuddy.domain;

import com.codeBuddy.codeBuddy.domain.Users.Mentor;
import com.codeBuddy.codeBuddy.domain.Users.Student;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String description;

    @Enumerated(EnumType.STRING)
    private RequestState requestState;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;

    @ManyToOne
    @JoinColumn(name = "mentor_id")
    private Mentor mentor;
}
