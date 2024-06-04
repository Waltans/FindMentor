package com.codeBuddy.codeBuddy.domain;

import com.codeBuddy.codeBuddy.domain.Users.Student;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

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

    @Setter(AccessLevel.NONE)
    private Integer countOfLikes = 0;

    /**
     * Дата создания поста
     */
    private LocalDateTime localDateTime;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @ManyToOne
    private Student student;

    @ElementCollection
    @Setter(AccessLevel.NONE)
    private List<String> urlPhoto = new ArrayList<>(3);


    public void setUrlPhoto(List<String> urlPhoto) {
        if (urlPhoto.size() <= 3) {
            this.urlPhoto.removeAll(urlPhoto);
            this.urlPhoto.addAll(urlPhoto);
        }
    }

    public void setCountOfLikes() {
        this.countOfLikes++;
    }
}
