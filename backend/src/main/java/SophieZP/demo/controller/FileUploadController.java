package SophieZP.demo.controller;

import SophieZP.demo.dto.ApiResponse;
import SophieZP.demo.entity.FileUpload;
import SophieZP.demo.entity.Subject;
import SophieZP.demo.entity.User;
import SophieZP.demo.repository.FileUploadRepository;
import SophieZP.demo.repository.SubjectRepository;
import SophieZP.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

/**
 * Controlador para la gestión de carga de archivos.
 * Proporciona endpoints para subir, obtener y eliminar archivos.
 */
@RestController
@RequestMapping("/api/files")
public class FileUploadController {

    @Autowired
    private FileUploadRepository fileUploadRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Subir un nuevo archivo.
     * POST /api/files/subject/{subjectId}/user/{userId}
     */
    @PostMapping("/subject/{subjectId}/user/{userId}")
    public ResponseEntity<ApiResponse<FileUpload>> uploadFile(@PathVariable Long subjectId, @PathVariable Long userId, @RequestBody FileUpload fileUpload) {
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

            fileUpload.setSubject(subject.get());
            fileUpload.setUser(user.get());
            FileUpload savedFile = fileUploadRepository.save(fileUpload);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(savedFile, "Archivo guardado exitosamente"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error al guardar el archivo: " + e.getMessage(), "UPLOAD_FILE_ERROR"));
        }
    }

    /**
     * Obtener todos los archivos de una materia.
     * GET /api/files/subject/{subjectId}
     */
    @GetMapping("/subject/{subjectId}")
    public ResponseEntity<ApiResponse<List<FileUpload>>> getFilesBySubject(@PathVariable Long subjectId) {
        try {
            List<FileUpload> files = fileUploadRepository.findBySubjectId(subjectId);
            return ResponseEntity.ok(ApiResponse.success(files));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage(), "GET_FILES_ERROR"));
        }
    }

    /**
     * Obtener todos los archivos subidos por un usuario.
     * GET /api/files/user/{userId}
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<FileUpload>>> getFilesByUser(@PathVariable Long userId) {
        try {
            List<FileUpload> files = fileUploadRepository.findByUserId(userId);
            return ResponseEntity.ok(ApiResponse.success(files));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage(), "GET_FILES_ERROR"));
        }
    }

    /**
     * Obtener archivos de una materia subidos por un usuario.
     * GET /api/files/subject/{subjectId}/user/{userId}
     */
    @GetMapping("/subject/{subjectId}/user/{userId}")
    public ResponseEntity<ApiResponse<List<FileUpload>>> getFilesBySubjectAndUser(@PathVariable Long subjectId, @PathVariable Long userId) {
        try {
            List<FileUpload> files = fileUploadRepository.findBySubjectIdAndUserId(subjectId, userId);
            return ResponseEntity.ok(ApiResponse.success(files));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage(), "GET_FILES_ERROR"));
        }
    }

    /**
     * Obtener un archivo específico por ID.
     * GET /api/files/{fileId}
     */
    @GetMapping("/{fileId}")
    public ResponseEntity<ApiResponse<FileUpload>> getFileById(@PathVariable Long fileId) {
        try {
            Optional<FileUpload> file = fileUploadRepository.findById(fileId);
            if (file.isPresent()) {
                return ResponseEntity.ok(ApiResponse.success(file.get()));
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Archivo no encontrado", "FILE_NOT_FOUND"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage(), "GET_FILE_ERROR"));
        }
    }

    /**
     * Actualizar metadatos del archivo.
     * PUT /api/files/{fileId}
     */
    @PutMapping("/{fileId}")
    public ResponseEntity<ApiResponse<FileUpload>> updateFile(@PathVariable Long fileId, @RequestBody FileUpload fileDetails) {
        try {
            Optional<FileUpload> file = fileUploadRepository.findById(fileId);
            if (file.isPresent()) {
                FileUpload existingFile = file.get();
                existingFile.setDescription(fileDetails.getDescription());
                FileUpload updatedFile = fileUploadRepository.save(existingFile);
                return ResponseEntity.ok(ApiResponse.success(updatedFile, "Archivo actualizado"));
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Archivo no encontrado", "FILE_NOT_FOUND"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage(), "UPDATE_FILE_ERROR"));
        }
    }

    /**
     * Eliminar un archivo.
     * DELETE /api/files/{fileId}
     */
    @DeleteMapping("/{fileId}")
    public ResponseEntity<ApiResponse<Object>> deleteFile(@PathVariable Long fileId) {
        try {
            Optional<FileUpload> file = fileUploadRepository.findById(fileId);
            if (file.isPresent()) {
                fileUploadRepository.deleteById(fileId);
                return ResponseEntity.ok(ApiResponse.success(null, "Archivo eliminado correctamente"));
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Archivo no encontrado", "FILE_NOT_FOUND"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage(), "DELETE_FILE_ERROR"));
        }
    }
}
