# University Organizer Mobile - Documentación Técnica

## Estructura del Proyecto Mobile

```
mobile/myapp/common/src/main/java/com/sophiezp/
├── ApiClient.java              # Cliente centralizado para API REST
├── LoginScreen.java            # Pantalla de login
├── SignUpScreen.java           # Pantalla de registro
├── DashboardForm.java          # Dashboard principal
├── UserSession.java            # Gestor de sesión
├── AddSubjectForm.java         # Crear materia
├── SubjectDetailForm.java      # Detalles de materia
├── AddTaskForm.java            # Crear tarea
├── AddNoteForm.java            # Crear nota
├── NotesForm.java              # Gestión de notas
├── NotificationsForm.java      # Notificaciones
├── FilesForm.java              # Gestor de archivos
└── MyApp.java                  # Punto de entrada
```

## ApiClient - Cliente Centralizado

### Propósito

Centraliza todas las llamadas HTTP a la API REST, proporcionando:
- Construcción consistente de requests
- Parseo uniforme de respuestas
- Manejo centralizado de errores
- Interfaz callback para respuestas asincrónicas

### Configuración Base

```
BASE_URL = "http://localhost:8080/api"
```

### Métodos Principales

#### POST Request
```java
ApiClient.post("/users/register", jsonBody, response -> {
    if (response.isSuccess()) {
        // Manejar éxito
    } else {
        // Manejar error con response.getMessage()
    }
});
```

#### GET Request
```java
ApiClient.get("/tasks/user/1", response -> {
    if (response.isSuccess()) {
        Map<String, Object> data = response.getData();
    }
});
```

### Clase ApiResponse

```java
public class ApiResponse {
    private int responseCode;        // Código HTTP
    private boolean success;         // ¿Fue exitoso?
    private String message;          // Mensaje amigable
    private String errorCode;        // Código de error interno
    private Map<String, Object> data; // Datos de respuesta
}
```

## Mejoras de UI/UX

### Contrastes Mejorados

**LoginScreen y SignUpScreen**:

| Elemento | Color | Código Hex |
|----------|-------|-----------|
| Background | Azul claro | 0xE8F4F8 |
| Título | Verde oscuro | 0x1B5E20 |
| Campos de entrada | Blanco | 0xFFFFFF |
| Texto campos | Gris muy oscuro | 0x1A1A1A |
| Borde campos | Verde | 0x4CAF50 |
| Botón primario | Verde oscuro | 0x2E7D32 |
| Botón secundario | Rojo oscuro | 0xD32F2F |
| Botón alternativo | Azul oscuro | 0x1976D2 |

### Propiedades de Estilos

```java
// Campos de texto
field.getAllStyles().setMargin(12, 12, 8, 12);
field.getAllStyles().setPadding(10, 10, 10, 10);
field.getAllStyles().setFgColor(0x1A1A1A);
field.getAllStyles().setBorder(Border.createLineBorder(2, 0x4CAF50));

// Botones
button.getAllStyles().setPadding(18, 20, 18, 20);
button.getAllStyles().setFont(Font.createSystemFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_LARGE));
button.getAllStyles().setAlignment(Component.CENTER);
```

## Validaciones Locales

### LoginScreen
```java
if (emailText.isEmpty() || passwordText.isEmpty()) {
    Dialog.show("Error", "Por favor completa todos los campos", "OK", null);
}

if (!emailText.contains("@")) {
    Dialog.show("Error", "Ingresa un correo válido", "OK", null);
}
```

### SignUpScreen
```java
if (fullName.isEmpty() || email.isEmpty() || university.isEmpty() || password.isEmpty()) {
    Dialog.show("Error", "Por favor completa todos los campos", "OK", null);
}

if (password.length() < 6) {
    Dialog.show("Error", "La contraseña debe tener al menos 6 caracteres", "OK", null);
}

if (!password.equals(confirmPassword)) {
    Dialog.show("Error", "Las contraseñas no coinciden", "OK", null);
}
```

## UserSession - Gestor de Sesión

Maneja la información de usuario autenticado:

```java
UserSession.getInstance().startSession(userDataMap);
Long userId = UserSession.getInstance().getId();
String userEmail = UserSession.getInstance().getEmail();
```

## Flujo de Autenticación

### Registro

1. Usuario completa formulario en `SignUpScreen`
2. Validaciones locales
3. ApiClient envía POST a `/users/register`
4. Si éxito (201):
   - Mostrar diálogo de éxito
   - Navegar a `LoginScreen`
5. Si error (400):
   - Mostrar mensaje de error
   - Usuario intenta nuevamente

### Login

1. Usuario completa formulario en `LoginScreen`
2. Validaciones locales
3. ApiClient envía POST a `/users/login`
4. Si éxito (200):
   - Guardar sesión con `UserSession.startSession()`
   - Navegar a `DashboardForm`
5. Si error (401):
   - Mostrar "Credenciales incorrectas"

## Manejo de Errores

El ApiClient maneja automáticamente:

| Código | Acción |
|--------|--------|
| 200/201 | Éxito |
| 400 | Bad Request - Mostrar mensaje del servidor |
| 401 | Unauthorized - "Credenciales incorrectas" |
| 404 | Not Found - "Recurso no encontrado" |
| 500 | Server Error - "Error interno del servidor" |

## Escape de Caracteres JSON

Método auxiliar para evitar errores de parseo:

```java
private String escapeJson(String input) {
    if (input == null) return "";
    return input.replace("\\", "\\\\")
               .replace("\"", "\\\"")
               .replace("\n", "\\n")
               .replace("\r", "\\r")
               .replace("\t", "\\t");
}
```

Uso:
```java
String json = "{\"name\":\"" + escapeJson(userName) + "\"}";
```

## Pantallas Principales

### LoginScreen
- Email y contraseña
- Botón "Iniciar Sesión"
- Link "Crear Cuenta" → SignUpScreen

### SignUpScreen
- Nombre completo
- Correo electrónico
- Universidad
- Contraseña
- Confirmar contraseña
- Botón "Registrarse"
- Botón "Volver"

### DashboardForm
- Vista general de tareas
- Acceso a materias
- Acceso a notas
- Acceso a notificaciones
- Acceso a archivos

## Testing

Para probar localmente:

1. Asegurar que el backend está corriendo en `http://localhost:8080`
2. Hacer login con credenciales de prueba
3. Verificar que las pantallas navegan correctamente
4. Probar creación de materias, tareas, notas

## Notas de Desarrollo

- Las respuestas del servidor siguen formato ApiResponse
- Todos los campos vacíos deben escaparse para JSON
- Las llamadas HTTP son asincrónicas con callbacks
- UserSession persiste durante toda la sesión de la app
- Los errores se muestran con diálogos

## Futuras Mejoras

- [ ] Implementar caching de respuestas
- [ ] Agregar indicador de carga (spinner)
- [ ] Implementar offline mode
- [ ] Agregar autenticación por biometría
- [ ] Mejorar diseño responsive
