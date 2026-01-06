package SophieZP.demo.controller;

import SophieZP.demo.dto.ApiResponse;
import SophieZP.demo.entity.Subject;
import SophieZP.demo.service.SubjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * Controlador para la gestión de materias/asignaturas.
 * Proporciona endpoints para crear y obtener materias.
 */
@RestController
@RequestMapping("/api/subjects")
public class SubjectController {

    @Autowired
    private SubjectService subjectService;

    /**
     * Crear una nueva materia para un usuario.
     * POST /api/subjects/user/{userId}
     */
    @PostMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<Subject>> createSubject(@PathVariable Long userId, @RequestBody Subject subject) {
        try {
            Subject createdSubject = subjectService.createSubject(userId, subject);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(ApiResponse.success(createdSubject, "Materia creada exitosamente"));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage(), "CREATE_SUBJECT_ERROR"));
        }
    }

    /**
     * Obtener todas las materias de un usuario.
     * GET /api/subjects/user/{userId}
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<Subject>>> getSubjectsByUser(@PathVariable Long userId) {
        try {
            List<Subject> subjects = subjectService.getSubjectByUserId(userId);
            return ResponseEntity.ok(ApiResponse.success(subjects, "Materias obtenidas"));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage(), "GET_SUBJECTS_ERROR"));
        }
    }
}