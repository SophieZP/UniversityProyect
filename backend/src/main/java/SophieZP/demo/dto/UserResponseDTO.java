package SophieZP.demo.dto;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * DTO para la respuesta de usuario.
 * No expone datos sensibles como la contraseña.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponseDTO {
    private Long id;
    private String fullName;
    private String email;
    private String universityName;
    private LocalDateTime createdAt;

    // Constructors
    public UserResponseDTO() {}

    public UserResponseDTO(Long id, String fullName, String email, String universityName, LocalDateTime createdAt) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.universityName = universityName;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUniversityName() {
        return universityName;
    }

    public void setUniversityName(String universityName) {
        this.universityName = universityName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
