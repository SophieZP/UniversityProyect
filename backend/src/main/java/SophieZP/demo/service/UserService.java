package SophieZP.demo.service;

import SophieZP.demo.dto.UserRegisterDTO;
import SophieZP.demo.entity.User;
import SophieZP.demo.exception.BusinessException;
import SophieZP.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

/**
 * Servicio de gestión de usuarios.
 * Contiene la lógica de negocio relacionada con usuarios.
 */
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    /**
     * Registrar un nuevo usuario desde un DTO.
     * Valida que el email no exista y luego guarda el usuario.
     */
    public User registerUser(UserRegisterDTO registerDTO) {
        // Validar que el email no exista
        if (userRepository.findByEmail(registerDTO.getEmail()).isPresent()) {
            throw new BusinessException(
                    "El correo " + registerDTO.getEmail() + " ya está registrado",
                    "EMAIL_ALREADY_EXISTS"
            );
        }

        // Crear nuevo usuario desde el DTO
        User user = new User();
        user.setFullName(registerDTO.getFullName());
        user.setEmail(registerDTO.getEmail());
        user.setUniversityName(registerDTO.getUniversityName());
        user.setPasswordHash(registerDTO.getPasswordHash());
        user.setCreatedAt(LocalDateTime.now());

        return userRepository.save(user);
    }

    /**
     * Crear usuario (método heredado, usar registerUser en su lugar).
     */
    public User createUser(User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new BusinessException(
                    "El correo " + user.getEmail() + " ya está registrado",
                    "EMAIL_ALREADY_EXISTS"
            );
        }
        return userRepository.save(user);
    }

    /**
     * Obtener usuario por ID.
     */
    public User getUserById(Long id) {
        if (id == null || id <= 0) {
            throw new BusinessException("ID de usuario inválido", "INVALID_USER_ID");
        }
        return userRepository.findById(id).orElse(null);
    }

    /**
     * Autenticar usuario con email y contraseña.
     * NOTA: En producción, usar BCrypt para comparar contraseñas.
     */
    public User authenticate(String email, String password) {
        if (email == null || email.isBlank() || password == null || password.isBlank()) {
            throw new BusinessException("Email y contraseña son requeridos", "MISSING_CREDENTIALS");
        }

        User user = userRepository.findByEmail(email).orElse(null);

        if (user != null && user.getPasswordHash().equals(password)) {
            return user;
        }

        return null;
    }
}
