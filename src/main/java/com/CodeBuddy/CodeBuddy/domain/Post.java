package com.CodeBuddy.CodeBuddy.domain;

import com.CodeBuddy.CodeBuddy.domain.Users.Student;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String description;

    private Long countOfLikes;

    /**
     * Дата создания поста
     */
    private LocalDateTime localDateTime;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @ManyToOne
    private Student student;

    @ElementCollection
    private List<String> urlPhoto = new ArrayList<>(3);

}
