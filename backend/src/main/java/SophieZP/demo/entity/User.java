package SophieZP.demo.entity;

import jakarta.persistence.*;
import lombok.Data; // Lombok saves us to write Getters y Setters
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "users")
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "university_name")
    private String universityName;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // Connection: User have many Subjecs
    // "mappedBy" Refers to user field in class Subject
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @com.fasterxml.jackson.annotation.JsonIgnore
    private List<Subject> subjects;

    @PrePersist // Before saving asign the current date
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}