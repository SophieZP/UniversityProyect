# University Organizer - Documentación de API

## Estructura del Proyecto

Este documento describe la API REST del sistema University Organizer, organizado en arquitectura de capas.

### Directorio Backend

```
backend/
├── src/main/java/SophieZP/demo/
│   ├── controller/         # Controladores REST
│   ├── service/           # Lógica de negocio
│   ├── entity/            # Modelos JPA
│   ├── repository/        # Acceso a datos
│   ├── dto/               # Data Transfer Objects
│   ├── exception/         # Excepciones personalizadas
│   └── UniversityOrganizerApplication.java
```

## Estructura de Respuestas API

Todas las respuestas de la API siguen el siguiente formato:

```json
{
  "success": true,
  "message": "Operación exitosa",
  "data": { /* objeto o lista */ },
  "errorCode": null
}
```

En caso de error:

```json
{
  "success": false,
  "message": "Descripción del error",
  "data": null,
  "errorCode": "ERROR_CODE"
}
```

## Códigos HTTP

- **201 Created**: Recurso creado exitosamente
- **200 OK**: Operación exitosa
- **400 Bad Request**: Error de validación o solicitud inválida
- **401 Unauthorized**: Credenciales incorrectas
- **404 Not Found**: Recurso no encontrado
- **500 Internal Server Error**: Error interno del servidor

## Endpoints de Usuarios

### Registro de Usuario
- **POST** `/api/users/register`
- **Body**: 
```json
{
  "fullName": "Juan Pérez",
  "email": "juan@email.com",
  "universityName": "Universidad XYZ",
  "passwordHash": "contraseña123"
}
```
- **Response**: 201 Created - Objeto usuario creado

### Login
- **POST** `/api/users/login`
- **Body**:
```json
{
  "email": "juan@email.com",
  "passwordHash": "contraseña123"
}
```
- **Response**: 200 OK - Objeto usuario autenticado

### Obtener Usuario por ID
- **GET** `/api/users/{id}`
- **Response**: 200 OK - Objeto usuario

## Endpoints de Materias

### Crear Materia
- **POST** `/api/subjects/user/{userId}`
- **Body**: Objeto Subject
- **Response**: 201 Created

### Obtener Materias de Usuario
- **GET** `/api/subjects/user/{userId}`
- **Response**: 200 OK - Lista de materias

## Endpoints de Tareas

### Crear Tarea
- **POST** `/api/tasks/subject/{subjectId}`
- **Response**: 201 Created

### Obtener Tareas de Usuario
- **GET** `/api/tasks/user/{userId}`
- **Response**: 200 OK - Lista de tareas

### Obtener Tareas de Materia
- **GET** `/api/tasks/subject/{subjectId}`
- **Response**: 200 OK - Lista de tareas

### Alternar Estado de Tarea
- **PUT** `/api/tasks/{taskId}/toggle`
- **Response**: 200 OK - Tarea actualizada

### Eliminar Tarea
- **DELETE** `/api/tasks/{taskId}`
- **Response**: 200 OK

## Endpoints de Notas

### Crear Nota
- **POST** `/api/notes/subject/{subjectId}/user/{userId}`
- **Response**: 201 Created

### Obtener Notas de Materia
- **GET** `/api/notes/subject/{subjectId}`

### Obtener Notas de Usuario
- **GET** `/api/notes/user/{userId}`

### Obtener Notas por Materia y Usuario
- **GET** `/api/notes/subject/{subjectId}/user/{userId}`

### Actualizar Nota
- **PUT** `/api/notes/{noteId}`
- **Response**: 200 OK

### Eliminar Nota
- **DELETE** `/api/notes/{noteId}`
- **Response**: 200 OK

## Endpoints de Notificaciones

### Crear Notificación
- **POST** `/api/notifications/user/{userId}`
- **Response**: 201 Created

### Obtener Notificaciones de Usuario
- **GET** `/api/notifications/user/{userId}`

### Obtener Notificaciones No Leídas
- **GET** `/api/notifications/user/{userId}/unread`

### Marcar como Leída
- **PUT** `/api/notifications/{notificationId}/read`

### Marcar Todas como Leídas
- **PUT** `/api/notifications/user/{userId}/read-all`

### Eliminar Notificación
- **DELETE** `/api/notifications/{notificationId}`

## Endpoints de Archivos

### Subir Archivo
- **POST** `/api/files/subject/{subjectId}/user/{userId}`
- **Response**: 201 Created

### Obtener Archivos de Materia
- **GET** `/api/files/subject/{subjectId}`

### Obtener Archivos de Usuario
- **GET** `/api/files/user/{userId}`

### Obtener Archivos por Materia y Usuario
- **GET** `/api/files/subject/{subjectId}/user/{userId}`

### Actualizar Archivo
- **PUT** `/api/files/{fileId}`

### Eliminar Archivo
- **DELETE** `/api/files/{fileId}`

## DTOs Principales

### UserRegisterDTO
```java
{
  "fullName": String,      // 2-100 caracteres
  "email": String,         // Email válido
  "universityName": String, // 2-100 caracteres
  "passwordHash": String    // 6-255 caracteres
}
```

### UserLoginDTO
```java
{
  "email": String,
  "passwordHash": String
}
```

### UserResponseDTO
```java
{
  "id": Long,
  "fullName": String,
  "email": String,
  "universityName": String,
  "createdAt": LocalDateTime
}
```

## Manejo de Errores

La aplicación utiliza un `GlobalExceptionHandler` para manejar excepciones de forma consistente:

- **BusinessException**: Errores de negocio esperados
- **MethodArgumentNotValidException**: Errores de validación
- **IllegalArgumentException**: Argumentos inválidos
- **Exception**: Errores generales del servidor

## Validaciones

Las validaciones se realizan en dos niveles:

1. **DTOs**: Validación de entrada con anotaciones `@Valid`
2. **Servicios**: Lógica de negocio adicional

### Anotaciones de Validación

- `@NotBlank`: Campo no vacío
- `@Email`: Formato de email válido
- `@Size(min, max)`: Longitud válida
- `@Valid`: Valida objetos anidados

## Notas de Seguridad

⚠️ **IMPORTANTE**: En producción:
1. Usar **BCrypt** para encriptación de contraseñas (no texto plano)
2. Implementar **JWT** o **OAuth2** para autenticación
3. Usar **HTTPS** para todas las comunicaciones
4. Implementar **Rate Limiting**
5. Usar variables de entorno para configuraciones sensibles

## Ejecución

```bash
# Compilar
mvn clean compile

# Ejecutar tests
mvn test

# Empaquetar
mvn package

# Ejecutar
java -jar target/demo-0.0.1-SNAPSHOT.jar
```

## Dependencias Principales

- Spring Boot 4.0.1
- Spring Data JPA
- PostgreSQL
- Lombok
- Jakarta Validation
