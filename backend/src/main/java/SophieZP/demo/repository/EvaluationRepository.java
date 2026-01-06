package SophieZP.demo.repository;

import SophieZP.demo.entity.Evaluation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EvaluationRepository extends JpaRepository<Evaluation, Long> {
    List<Evaluation> findBySubjectId(Long subjectId);
}