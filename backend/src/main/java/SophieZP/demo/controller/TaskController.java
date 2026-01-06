package SophieZP.demo.controller;

import SophieZP.demo.dto.ApiResponse;
import SophieZP.demo.entity.Task;
import SophieZP.demo.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * Controlador para la gestión de tareas.
 * Proporciona endpoints para crear, modificar y eliminar tareas.
 */
@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    /**
     * Crear una nueva tarea para una materia.
     * POST /api/tasks/subject/{subjectId}
     */
    @PostMapping("/subject/{subjectId}")
    public ResponseEntity<ApiResponse<Task>> createTask(@PathVariable Long subjectId, @RequestBody Task task) {
        try {
            Task createdTask = taskService.createTask(subjectId, task);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(ApiResponse.success(createdTask, "Tarea creada exitosamente"));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage(), "CREATE_TASK_ERROR"));
        }
    }

    /**
     * Obtener todas las tareas de un usuario.
     * GET /api/tasks/user/{userId}
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<Task>>> getUserDashboard(@PathVariable Long userId) {
        try {
            List<Task> tasks = taskService.getTasksByUserId(userId);
            return ResponseEntity.ok(ApiResponse.success(tasks, "Tareas obtenidas"));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage(), "GET_TASKS_ERROR"));
        }
    }

    /**
     * Alternar estado de completación de una tarea.
     * PUT /api/tasks/{taskId}/toggle
     */
    @PutMapping("/{taskId}/toggle")
    public ResponseEntity<ApiResponse<Task>> toggleTask(@PathVariable Long taskId) {
        try {
            Task task = taskService.toggleTaskCompletion(taskId);
            return ResponseEntity.ok(ApiResponse.success(task, "Estado de tarea actualizado"));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage(), "TOGGLE_TASK_ERROR"));
        }
    }

    /**
     * Obtener todas las tareas de una materia.
     * GET /api/tasks/subject/{subjectId}
     */
    @GetMapping("/subject/{subjectId}")
    public ResponseEntity<ApiResponse<List<Task>>> getTasksBySubject(@PathVariable Long subjectId) {
        try {
            List<Task> tasks = taskService.getTasksBySubjectId(subjectId);
            return ResponseEntity.ok(ApiResponse.success(tasks));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage(), "GET_SUBJECT_TASKS_ERROR"));
        }
    }

    /**
     * Eliminar una tarea.
     * DELETE /api/tasks/{taskId}
     */
    @DeleteMapping("/{taskId}")
    public ResponseEntity<ApiResponse<Object>> deleteTask(@PathVariable Long taskId) {
        try {
            boolean deleted = taskService.deleteTask(taskId);
            if (deleted) {
                return ResponseEntity.ok(ApiResponse.success(null, "Tarea eliminada correctamente"));
            } else {
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Tarea no encontrada", "TASK_NOT_FOUND"));
            }
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage(), "DELETE_TASK_ERROR"));
        }
    }
}