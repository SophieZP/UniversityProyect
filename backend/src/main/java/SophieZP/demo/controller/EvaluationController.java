package SophieZP.demo.controller;

import SophieZP.demo.entity.Evaluation;
import SophieZP.demo.entity.Subject;
import SophieZP.demo.repository.EvaluationRepository;
import SophieZP.demo.repository.SubjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/evaluations")
@CrossOrigin(origins = "*")
public class EvaluationController {

    @Autowired
    private EvaluationRepository evaluationRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    /**
     * Crear una nueva calificación para una materia
     * POST /api/evaluations/subject/{subjectId}
     */
    @PostMapping("/subject/{subjectId}")
    public ResponseEntity<Map<String, Object>> createEvaluation(
            @PathVariable Long subjectId,
            @RequestBody Map<String, Object> evaluationData) {

        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new RuntimeException("Materia no encontrada"));

        Evaluation evaluation = new Evaluation();
        evaluation.setName((String) evaluationData.get("name"));

        // Convertir a BigDecimal
        Object percentageObj = evaluationData.get("percentage");
        BigDecimal percentage = percentageObj instanceof Number
                ? BigDecimal.valueOf(((Number) percentageObj).doubleValue())
                : new BigDecimal(percentageObj.toString());
        evaluation.setPercentage(percentage);

        Object gradeObj = evaluationData.get("obtainedGrade");
        BigDecimal grade = gradeObj instanceof Number
                ? BigDecimal.valueOf(((Number) gradeObj).doubleValue())
                : new BigDecimal(gradeObj.toString());
        evaluation.setObtainedGrade(grade);

        evaluation.setSubject(subject);

        Evaluation savedEvaluation = evaluationRepository.save(evaluation);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Calificación guardada correctamente");
        response.put("data", savedEvaluation);

        return ResponseEntity.status(201).body(response);
    }

    /**
     * Obtener todas las calificaciones de una materia
     * GET /api/evaluations/subject/{subjectId}
     */
    @GetMapping("/subject/{subjectId}")
    public ResponseEntity<Map<String, Object>> getEvaluationsBySubject(@PathVariable Long subjectId) {
        List<Evaluation> evaluations = evaluationRepository.findBySubjectId(subjectId);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", evaluations);

        return ResponseEntity.ok(response);
    }

    /**
     * Eliminar una calificación
     * DELETE /api/evaluations/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteEvaluation(@PathVariable Long id) {
        if (!evaluationRepository.existsById(id)) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Calificación no encontrada");
            return ResponseEntity.status(404).body(errorResponse);
        }

        evaluationRepository.deleteById(id);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Calificación eliminada correctamente");

        return ResponseEntity.ok(response);
    }

    /**
     * Actualizar una calificación
     * PUT /api/evaluations/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateEvaluation(
            @PathVariable Long id,
            @RequestBody Map<String, Object> evaluationData) {

        Evaluation evaluation = evaluationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Calificación no encontrada"));

        if (evaluationData.containsKey("name")) {
            evaluation.setName((String) evaluationData.get("name"));
        }

        if (evaluationData.containsKey("percentage")) {
            Object percentageObj = evaluationData.get("percentage");
            BigDecimal percentage = percentageObj instanceof Number
                    ? BigDecimal.valueOf(((Number) percentageObj).doubleValue())
                    : new BigDecimal(percentageObj.toString());
            evaluation.setPercentage(percentage);
        }

        if (evaluationData.containsKey("obtainedGrade")) {
            Object gradeObj = evaluationData.get("obtainedGrade");
            BigDecimal grade = gradeObj instanceof Number
                    ? BigDecimal.valueOf(((Number) gradeObj).doubleValue())
                    : new BigDecimal(gradeObj.toString());
            evaluation.setObtainedGrade(grade);
        }

        Evaluation updatedEvaluation = evaluationRepository.save(evaluation);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Calificación actualizada correctamente");
        response.put("data", updatedEvaluation);

        return ResponseEntity.ok(response);
    }
}