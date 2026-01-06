package SophieZP.demo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO para el registro de nuevos usuarios.
 * Contiene las validaciones necesarias en el lado del servidor.
 */
public class UserRegisterDTO {

    @NotBlank(message = "El nombre completo no puede estar vacío")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    private String fullName;

    @NotBlank(message = "El correo electrónico no puede estar vacío")
    @Email(message = "El correo electrónico debe ser válido")
    private String email;

    @NotBlank(message = "El nombre de la universidad no puede estar vacío")
    @Size(min = 2, max = 100, message = "El nombre de la universidad debe tener entre 2 y 100 caracteres")
    private String universityName;

    @NotBlank(message = "La contraseña no puede estar vacía")
    @Size(min = 6, max = 255, message = "La contraseña debe tener entre 6 y 255 caracteres")
    private String passwordHash;

    // Constructors
    public UserRegisterDTO() {}

    public UserRegisterDTO(String fullName, String email, String universityName, String passwordHash) {
        this.fullName = fullName;
        this.email = email;
        this.universityName = universityName;
        this.passwordHash = passwordHash;
    }

    // Getters and Setters
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

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }
}
