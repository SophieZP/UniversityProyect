package SophieZP.demo.controller;

import SophieZP.demo.entity.FileUpload;
import SophieZP.demo.entity.Subject;
import SophieZP.demo.entity.User;
import SophieZP.demo.repository.FileUploadRepository;
import SophieZP.demo.repository.SubjectRepository;
import SophieZP.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/files")
@CrossOrigin(origins = "*")
public class FileUploadController {

    @Autowired
    private FileUploadRepository fileUploadRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private UserRepository userRepository;

    // Directorio donde se guardarán los archivos
    private static final String UPLOAD_DIR = "uploads/";

    /**
     * Subir un archivo para una materia
     * POST /api/files/subject/{subjectId}/user/{userId}
     */
    @PostMapping("/subject/{subjectId}/user/{userId}")
    public ResponseEntity<Map<String, Object>> uploadFile(
            @PathVariable Long subjectId,
            @PathVariable Long userId,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "description", required = false) String description) {

        Map<String, Object> response = new HashMap<>();

        try {
            // Validar que el archivo no esté vacío
            if (file.isEmpty()) {
                response.put("success", false);
                response.put("message", "El archivo está vacío");
                return ResponseEntity.badRequest().body(response);
            }

            // Buscar materia y usuario
            Subject subject = subjectRepository.findById(subjectId)
                    .orElseThrow(() -> new RuntimeException("Materia no encontrada"));

            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            // Crear directorio si no existe
            File uploadDir = new File(UPLOAD_DIR);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            // Generar nombre único para el archivo
            String originalFileName = file.getOriginalFilename();
            String fileExtension = "";
            if (originalFileName != null && originalFileName.contains(".")) {
                fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
            }
            String uniqueFileName = System.currentTimeMillis() + "_" + originalFileName;

            // Guardar el archivo en el sistema de archivos
            Path filePath = Paths.get(UPLOAD_DIR + uniqueFileName);
            Files.write(filePath, file.getBytes());

            // Crear registro en la base de datos
            FileUpload fileUpload = new FileUpload();
            fileUpload.setFileName(originalFileName);
            fileUpload.setFileType(getFileExtension(originalFileName));
            fileUpload.setFileSize(file.getSize());
            fileUpload.setFilePath(filePath.toString());
            fileUpload.setDescription(description);
            fileUpload.setSubject(subject);
            fileUpload.setUser(user);

            FileUpload savedFile = fileUploadRepository.save(fileUpload);

            response.put("success", true);
            response.put("message", "Archivo subido correctamente");
            response.put("data", savedFile);

            return ResponseEntity.status(201).body(response);

        } catch (IOException e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "Error al guardar el archivo: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "Error: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * Obtener todos los archivos de una materia
     * GET /api/files/subject/{subjectId}
     */
    @GetMapping("/subject/{subjectId}")
    public ResponseEntity<Map<String, Object>> getFilesBySubject(@PathVariable Long subjectId) {
        List<FileUpload> files = fileUploadRepository.findBySubjectId(subjectId);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", files);

        return ResponseEntity.ok(response);
    }

    /**
     * Obtener todos los archivos de un usuario
     * GET /api/files/user/{userId}
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<Map<String, Object>> getFilesByUser(@PathVariable Long userId) {
        List<FileUpload> files = fileUploadRepository.findByUserId(userId);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", files);

        return ResponseEntity.ok(response);
    }

    /**
     * Eliminar un archivo
     * DELETE /api/files/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteFile(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();

        try {
            FileUpload fileUpload = fileUploadRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Archivo no encontrado"));

            // Eliminar archivo físico
            try {
                Path filePath = Paths.get(fileUpload.getFilePath());
                Files.deleteIfExists(filePath);
            } catch (IOException e) {
                System.err.println("No se pudo eliminar el archivo físico: " + e.getMessage());
            }

            // Eliminar registro de la base de datos
            fileUploadRepository.deleteById(id);

            response.put("success", true);
            response.put("message", "Archivo eliminado correctamente");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al eliminar archivo: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * Descargar un archivo
     * GET /api/files/{id}/download
     */
    @GetMapping("/{id}/download")
    public ResponseEntity<?> downloadFile(@PathVariable Long id) {
        try {
            FileUpload fileUpload = fileUploadRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Archivo no encontrado"));

            Path filePath = Paths.get(fileUpload.getFilePath());
            byte[] fileContent = Files.readAllBytes(filePath);

            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=\"" + fileUpload.getFileName() + "\"")
                    .header("Content-Type", "application/octet-stream")
                    .body(fileContent);

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error al descargar archivo: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    /**
     * Obtener información de un archivo
     * GET /api/files/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getFileInfo(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();

        FileUpload fileUpload = fileUploadRepository.findById(id).orElse(null);

        if (fileUpload == null) {
            response.put("success", false);
            response.put("message", "Archivo no encontrado");
            return ResponseEntity.status(404).body(response);
        }

        response.put("success", true);
        response.put("data", fileUpload);

        return ResponseEntity.ok(response);
    }

    /**
     * Método auxiliar para obtener la extensión del archivo
     */
    private String getFileExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "UNKNOWN";
        }
        String extension = fileName.substring(fileName.lastIndexOf(".") + 1);
        return extension.toUpperCase();
    }
}