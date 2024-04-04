package com.CodeBuddy.CodeBuddy.domain;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String content;
    private Long userId;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;
}
