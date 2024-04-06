package com.CodeBuddy.CodeBuddy.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Data
@Entity
@Table(name = "Mentor")
public class Mentor {
    @Id
    @Column(unique = true,nullable = false)
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

    @OneToMany(mappedBy = "mentor")
    private List<Request> requests = new ArrayList<>();


}
