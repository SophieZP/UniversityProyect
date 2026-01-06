package SophieZP.demo.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Entity
@Table(name = "evaluations")
@Data
public class Evaluation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name; // Ex: Exam1

    @Column(nullable = false)
    private BigDecimal percentage; // We use BIGDECIMAL for exact decimals

    @Column(name = "obtained_grade")
    private BigDecimal obtainedGrade; // The grade

    // The connection: Many evaluations belong to a single Subject
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;
}