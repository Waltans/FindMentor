package com.CodeBuddy.CodeBuddy.domain;

import com.CodeBuddy.CodeBuddy.domain.Users.Mentor;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
public class Keyword {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String keyword;

    @ManyToMany(mappedBy = "keywords")
    private List<Mentor> mentors = new ArrayList<>();

}
