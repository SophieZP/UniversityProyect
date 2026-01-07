package SophieZP.demo.repository;

import SophieZP.demo.entity.Evaluation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EvaluationRepository extends JpaRepository<Evaluation, Long> {

    /**
     * Encuentra todas las evaluaciones de una materia específica
     */
    List<Evaluation> findBySubjectId(Long subjectId);

    /**
     * Encuentra todas las evaluaciones de una materia ordenadas por porcentaje
     */
    List<Evaluation> findBySubjectIdOrderByPercentageDesc(Long subjectId);
}