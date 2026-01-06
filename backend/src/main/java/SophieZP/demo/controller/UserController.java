package SophieZP.demo.controller;

import SophieZP.demo.dto.ApiResponse;
import SophieZP.demo.dto.UserRegisterDTO;
import SophieZP.demo.dto.UserLoginDTO;
import SophieZP.demo.dto.UserResponseDTO;
import SophieZP.demo.entity.User;
import SophieZP.demo.exception.BusinessException;
import SophieZP.demo.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * Registrar un nuevo usuario.
     * POST /api/users/register
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponseDTO>> register(@Valid @RequestBody UserRegisterDTO registerDTO) {
        try {
            User newUser = userService.registerUser(registerDTO);
            UserResponseDTO responseDTO = convertToResponseDTO(newUser);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(ApiResponse.success(responseDTO, "Usuario registrado exitosamente"));
        } catch (BusinessException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage(), e.getErrorCode()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error al registrar el usuario", "REGISTRATION_ERROR"));
        }
    }

    /**
     * Iniciar sesión.
     * POST /api/users/login
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<UserResponseDTO>> login(@Valid @RequestBody UserLoginDTO loginDTO) {
        try {
            User user = userService.authenticate(loginDTO.getEmail(), loginDTO.getPasswordHash());
            if (user != null) {
                UserResponseDTO responseDTO = convertToResponseDTO(user);
                return ResponseEntity
                        .ok(ApiResponse.success(responseDTO, "Sesión iniciada exitosamente"));
            } else {
                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("Credenciales incorrectas", "AUTHENTICATION_FAILED"));
            }
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error al autenticar", "AUTH_ERROR"));
        }
    }

    /**
     * Obtener usuario por ID.
     * GET /api/users/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponseDTO>> getUserById(@PathVariable Long id) {
        try {
            User user = userService.getUserById(id);
            if (user != null) {
                UserResponseDTO responseDTO = convertToResponseDTO(user);
                return ResponseEntity.ok(ApiResponse.success(responseDTO));
            } else {
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Usuario no encontrado", "USER_NOT_FOUND"));
            }
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error al obtener el usuario", "GET_USER_ERROR"));
        }
    }

    /**
     * Convierte un objeto User a UserResponseDTO.
     */
    private UserResponseDTO convertToResponseDTO(User user) {
        return new UserResponseDTO(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getUniversityName(),
                user.getCreatedAt()
        );
    }
}