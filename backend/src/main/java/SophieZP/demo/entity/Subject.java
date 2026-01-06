package SophieZP.demo.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "subjects")
@Data
public class Subject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "teacher_name")
    private String teacherName;

    @Column(name = "min_passing_grade")
    private Double minPassingGrade;

    @Column(name = "color_code")
    private String colorCode;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    //Connection: many Subjects belong to a single user

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false) //Foreign Key
    private User user;

    @PrePersist
    protected void onCreate(){
        createdAt = LocalDateTime.now();
    }
}
