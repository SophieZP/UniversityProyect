package SophieZP.demo.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "resources") // We map the list 'Resources'
@Data
public class StudyResource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private ResourceType type;

    @Column(name = "file_url")
    private String fileUrl;

    @Column(name = "local_path")
    private String localPath;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    public enum ResourceType {
        SYLLABUS,
        BOOK,
        NOTES
    }
}