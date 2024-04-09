package com.CodeBuddy.CodeBuddy.domain.Users;

import com.CodeBuddy.CodeBuddy.domain.Request;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Data
@Entity
@Table(name = "Mentor")
public class Mentor {
    @Id
    @Column(unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;

    private String lastName;

    @Column(length = 60)
    private String password;

    /**
     * Количество консультаций
     */
    private int countConsultation;

    private String email;

    private String telegram;

    private String description;

    @ElementCollection
    private Set<String> keyword;

    @OneToMany(mappedBy = "mentor",fetch = FetchType.LAZY)
    private List<Request> requests = new ArrayList<>();
    /**
     * Список учеников, у которых принята заявка ментором
     */
    @ManyToMany()
    @JoinTable(
            name = "student_mentor",
            joinColumns = @JoinColumn(name = "mentor_id"),
            inverseJoinColumns = @JoinColumn(name = "student_id")
    )
    private List<Student> acceptedStudent = new ArrayList<>();

}
