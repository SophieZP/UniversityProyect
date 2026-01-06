package SophieZP.demo.controller;

import SophieZP.demo.dto.ApiResponse;
import SophieZP.demo.entity.Notification;
import SophieZP.demo.entity.User;
import SophieZP.demo.repository.NotificationRepository;
import SophieZP.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

/**
 * Controlador para la gestión de notificaciones.
 * Proporciona endpoints para crear, obtener y marcar notificaciones como leídas.
 */
@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Crear una nueva notificación para un usuario.
     * POST /api/notifications/user/{userId}
     */
    @PostMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<Notification>> createNotification(@PathVariable Long userId, @RequestBody Notification notification) {
        try {
            Optional<User> user = userRepository.findById(userId);

            if (user.isPresent()) {
                notification.setUser(user.get());
                Notification savedNotification = notificationRepository.save(notification);
                return ResponseEntity.status(HttpStatus.CREATED)
                        .body(ApiResponse.success(savedNotification, "Notificación creada exitosamente"));
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Usuario no encontrado", "USER_NOT_FOUND"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage(), "CREATE_NOTIFICATION_ERROR"));
        }
    }

    /**
     * Obtener todas las notificaciones de un usuario.
     * GET /api/notifications/user/{userId}
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<Notification>>> getNotificationsByUser(@PathVariable Long userId) {
        try {
            List<Notification> notifications = notificationRepository.findByUserId(userId);
            return ResponseEntity.ok(ApiResponse.success(notifications));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage(), "GET_NOTIFICATIONS_ERROR"));
        }
    }

    /**
     * Obtener notificaciones no leídas de un usuario.
     * GET /api/notifications/user/{userId}/unread
     */
    @GetMapping("/user/{userId}/unread")
    public ResponseEntity<ApiResponse<List<Notification>>> getUnreadNotifications(@PathVariable Long userId) {
        try {
            List<Notification> notifications = notificationRepository.findByUserIdAndIsReadFalse(userId);
            return ResponseEntity.ok(ApiResponse.success(notifications));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage(), "GET_UNREAD_NOTIFICATIONS_ERROR"));
        }
    }

    /**
     * Marcar una notificación como leída.
     * PUT /api/notifications/{notificationId}/read
     */
    @PutMapping("/{notificationId}/read")
    public ResponseEntity<ApiResponse<Notification>> markAsRead(@PathVariable Long notificationId) {
        try {
            Optional<Notification> notification = notificationRepository.findById(notificationId);
            if (notification.isPresent()) {
                Notification existingNotification = notification.get();
                existingNotification.setIsRead(true);
                Notification updatedNotification = notificationRepository.save(existingNotification);
                return ResponseEntity.ok(ApiResponse.success(updatedNotification, "Notificación marcada como leída"));
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Notificación no encontrada", "NOTIFICATION_NOT_FOUND"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage(), "UPDATE_NOTIFICATION_ERROR"));
        }
    }

    /**
     * Marcar todas las notificaciones de un usuario como leídas.
     * PUT /api/notifications/user/{userId}/read-all
     */
    @PutMapping("/user/{userId}/read-all")
    public ResponseEntity<ApiResponse<Object>> markAllAsRead(@PathVariable Long userId) {
        try {
            List<Notification> unreadNotifications = notificationRepository.findByUserIdAndIsReadFalse(userId);
            for (Notification notification : unreadNotifications) {
                notification.setIsRead(true);
                notificationRepository.save(notification);
            }
            return ResponseEntity.ok(ApiResponse.success(null, "Todas las notificaciones marcadas como leídas"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage(), "MARK_ALL_READ_ERROR"));
        }
    }

    /**
     * Obtener una notificación específica por ID.
     * GET /api/notifications/{notificationId}
     */
    @GetMapping("/{notificationId}")
    public ResponseEntity<ApiResponse<Notification>> getNotificationById(@PathVariable Long notificationId) {
        try {
            Optional<Notification> notification = notificationRepository.findById(notificationId);
            if (notification.isPresent()) {
                return ResponseEntity.ok(ApiResponse.success(notification.get()));
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Notificación no encontrada", "NOTIFICATION_NOT_FOUND"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage(), "GET_NOTIFICATION_ERROR"));
        }
    }

    /**
     * Eliminar una notificación.
     * DELETE /api/notifications/{notificationId}
     */
    @DeleteMapping("/{notificationId}")
    public ResponseEntity<ApiResponse<Object>> deleteNotification(@PathVariable Long notificationId) {
        try {
            Optional<Notification> notification = notificationRepository.findById(notificationId);
            if (notification.isPresent()) {
                notificationRepository.deleteById(notificationId);
                return ResponseEntity.ok(ApiResponse.success(null, "Notificación eliminada correctamente"));
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Notificación no encontrada", "NOTIFICATION_NOT_FOUND"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage(), "DELETE_NOTIFICATION_ERROR"));
        }
    }
}
