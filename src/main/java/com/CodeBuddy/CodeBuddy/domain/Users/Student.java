package com.CodeBuddy.CodeBuddy.domain.Users;

import com.CodeBuddy.CodeBuddy.domain.Comment;
import com.CodeBuddy.CodeBuddy.domain.Post;
import com.CodeBuddy.CodeBuddy.domain.Request;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Student")
@Data
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstName;
    private String lastName;
    private String password;
    private String email;
    private String telegram;

    @OneToMany(mappedBy = "student", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Post> posts = new ArrayList<>();

    @OneToMany(mappedBy = "student", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "student", fetch = FetchType.LAZY)
    private List<Request> requests = new ArrayList<>();

    private String description;

    /**
     * Список менторов, которые приняли заявку у ученика
     */
    @ManyToMany()
    @JoinTable(
            name = "student_mentor",
            joinColumns = @JoinColumn(name = "student_id"),
            inverseJoinColumns = @JoinColumn(name = "mentor_id")
    )
    private List<Mentor> acceptedMentor = new ArrayList<>();

    private String photoUrl;

}
