package com.codeBuddy.codeBuddy.domain.Users;

import com.codeBuddy.codeBuddy.domain.Comment;
import com.codeBuddy.codeBuddy.domain.Keyword;
import com.codeBuddy.codeBuddy.domain.Request;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Mentor")
public class Mentor implements UserDetails {
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

    /**
     * Список ключевых слов
     */
    @ManyToMany
    @JoinTable(
            name = "keyword_mentor",
            joinColumns = @JoinColumn(name = "mentor_id"),
            inverseJoinColumns = @JoinColumn(name = "keyword_id")
    )
    private List<Keyword> keywords = new ArrayList<>();

    @OneToMany(mappedBy = "mentor", fetch = FetchType.LAZY)
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

    @OneToMany(mappedBy = "mentor",fetch = FetchType.LAZY, cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    /**
     * Ссылка на фотографию
     */
    private String urlPhoto;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return AuthorityUtils.createAuthorityList("ROLE_MENTOR");
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
