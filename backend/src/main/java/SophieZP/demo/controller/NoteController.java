package SophieZP.demo.controller;

import SophieZP.demo.dto.ApiResponse;
import SophieZP.demo.entity.Note;
import SophieZP.demo.entity.Subject;
import SophieZP.demo.entity.User;
import SophieZP.demo.repository.NoteRepository;
import SophieZP.demo.repository.SubjectRepository;
import SophieZP.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

/**
 * Controlador para la gestión de notas.
 * Proporciona endpoints para crear, obtener, actualizar y eliminar notas.
 */
@RestController
@RequestMapping("/api/notes")
public class NoteController {

    @Autowired
    private NoteRepository noteRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Crear una nueva nota.
     * POST /api/notes/subject/{subjectId}/user/{userId}
     */
    @PostMapping("/subject/{subjectId}/user/{userId}")
    public ResponseEntity<ApiResponse<Note>> createNote(@PathVariable Long subjectId, @PathVariable Long userId, @RequestBody Note note) {
        try {
            Optional<Subject> subject = subjectRepository.findById(subjectId);
            Optional<User> user = userRepository.findById(userId);

            if (!subject.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Materia con ID " + subjectId + " no encontrada", "SUBJECT_NOT_FOUND"));
            }
            if (!user.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Usuario con ID " + userId + " no encontrado", "USER_NOT_FOUND"));
            }

            note.setSubject(subject.get());
            note.setUser(user.get());
            Note savedNote = noteRepository.save(note);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(savedNote, "Nota creada exitosamente"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error al guardar la nota: " + e.getMessage(), "CREATE_NOTE_ERROR"));
        }
    }

    /**
     * Obtener todas las notas de una materia.
     * GET /api/notes/subject/{subjectId}
     */
    @GetMapping("/subject/{subjectId}")
    public ResponseEntity<ApiResponse<List<Note>>> getNotesBySubject(@PathVariable Long subjectId) {
        try {
            List<Note> notes = noteRepository.findBySubjectId(subjectId);
            return ResponseEntity.ok(ApiResponse.success(notes, "Notas obtenidas"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage(), "GET_NOTES_ERROR"));
        }
    }

    /**
     * Obtener todas las notas de un usuario.
     * GET /api/notes/user/{userId}
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<Note>>> getNotesByUser(@PathVariable Long userId) {
        try {
            List<Note> notes = noteRepository.findByUserId(userId);
            return ResponseEntity.ok(ApiResponse.success(notes));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage(), "GET_NOTES_ERROR"));
        }
    }

    /**
     * Obtener notas de una materia y un usuario específicos.
     * GET /api/notes/subject/{subjectId}/user/{userId}
     */
    @GetMapping("/subject/{subjectId}/user/{userId}")
    public ResponseEntity<ApiResponse<List<Note>>> getNotesBySubjectAndUser(@PathVariable Long subjectId, @PathVariable Long userId) {
        try {
            List<Note> notes = noteRepository.findBySubjectIdAndUserId(subjectId, userId);
            return ResponseEntity.ok(ApiResponse.success(notes));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage(), "GET_NOTES_ERROR"));
        }
    }

    /**
     * Obtener una nota específica por ID.
     * GET /api/notes/{noteId}
     */
    @GetMapping("/{noteId}")
    public ResponseEntity<ApiResponse<Note>> getNoteById(@PathVariable Long noteId) {
        try {
            Optional<Note> note = noteRepository.findById(noteId);
            if (note.isPresent()) {
                return ResponseEntity.ok(ApiResponse.success(note.get()));
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Nota no encontrada", "NOTE_NOT_FOUND"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage(), "GET_NOTE_ERROR"));
        }
    }

    /**
     * Actualizar una nota.
     * PUT /api/notes/{noteId}
     */
    @PutMapping("/{noteId}")
    public ResponseEntity<ApiResponse<Note>> updateNote(@PathVariable Long noteId, @RequestBody Note noteDetails) {
        try {
            Optional<Note> note = noteRepository.findById(noteId);
            if (note.isPresent()) {
                Note existingNote = note.get();
                existingNote.setTitle(noteDetails.getTitle());
                existingNote.setContent(noteDetails.getContent());
                existingNote.setColorCode(noteDetails.getColorCode());
                Note updatedNote = noteRepository.save(existingNote);
                return ResponseEntity.ok(ApiResponse.success(updatedNote, "Nota actualizada"));
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Nota no encontrada", "NOTE_NOT_FOUND"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage(), "UPDATE_NOTE_ERROR"));
        }
    }

    /**
     * Eliminar una nota.
     * DELETE /api/notes/{noteId}
     */
    @DeleteMapping("/{noteId}")
    public ResponseEntity<ApiResponse<Object>> deleteNote(@PathVariable Long noteId) {
        try {
            Optional<Note> note = noteRepository.findById(noteId);
            if (note.isPresent()) {
                noteRepository.deleteById(noteId);
                return ResponseEntity.ok(ApiResponse.success(null, "Nota eliminada correctamente"));
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Nota no encontrada", "NOTE_NOT_FOUND"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage(), "DELETE_NOTE_ERROR"));
        }
    }
}
