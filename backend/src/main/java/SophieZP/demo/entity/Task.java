package SophieZP.demo.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "tasks")
@Data
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private String description;

    @Column(name = "due_date", nullable = false)
    private LocalDateTime dueDate;

    @Enumerated(EnumType.STRING) //Saves the name od the enumerated as text in the DB
    @Column(nullable = false)
    private TaskType type;

    @Column(name = "is_completed")
    private Boolean isCompleted = false;

    @Column(name = "notification_time")
    private LocalDateTime notificationTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    // We define the types of tasks right here
    public enum TaskType {
        HOMEWORK,
        EXAM,
        EVENT
    }
}