# 🧪 Testing Guide - University Organizer

## Pruebas Manuales del MVP

### ✅ Antes de Empezar

1. **Backend corriendo:**
   ```bash
   cd backend
   mvn spring-boot:run
   ```
   Verificar: `http://localhost:8080` debe responder

2. **Base de datos:**
   ```sql
   CREATE DATABASE university_organizer;
   ```

3. **Mobile ejecutándose:**
   ```bash
   cd mobile/myapp
   ./run.sh
   ```

---

## 🔐 Test 1: Registro de Usuario

### Pasos
1. Abrir app mobile
2. Ver pantalla LoginScreen
3. Click en botón "Crear Cuenta"
4. Completa el formulario:
   - Nombre: `Juan Pérez`
   - Email: `juan@ejemplo.com`
   - Universidad: `Universidad XYZ`
   - Contraseña: `Password123`
   - Confirmar: `Password123`

### Validaciones Locales

**Debe rechazar:**
- ❌ Campos vacíos
- ❌ Email sin @
- ❌ Contraseña < 6 caracteres
- ❌ Contraseñas que no coinciden

### Respuestas Esperadas

**Éxito (201):**
```json
{
  "success": true,
  "message": "Usuario registrado exitosamente",
  "data": {
    "id": 1,
    "fullName": "Juan Pérez",
    "email": "juan@ejemplo.com",
    "universityName": "Universidad XYZ",
    "createdAt": "2025-01-06T10:30:00"
  }
}
```
✅ Muestra diálogo "Éxito - Cuenta creada..."  
✅ Redirige a LoginScreen

**Error - Email existe (400):**
```json
{
  "success": false,
  "message": "El correo juan@ejemplo.com ya está registrado",
  "errorCode": "EMAIL_ALREADY_EXISTS"
}
```
✅ Muestra: "Este correo ya está registrado"

**Error - Servidor (500):**
```json
{
  "success": false,
  "message": "Error interno del servidor",
  "errorCode": "INTERNAL_SERVER_ERROR"
}
```
✅ Muestra: "Error en el servidor. Por favor intenta más tarde."

---

## 🔑 Test 2: Login

### Pasos
1. En LoginScreen, completa:
   - Email: `juan@ejemplo.com`
   - Contraseña: `Password123`
2. Click "Iniciar Sesión"

### Validaciones Locales

**Debe rechazar:**
- ❌ Campos vacíos
- ❌ Email sin formato válido

### Respuesta Esperada

**Éxito (200):**
```json
{
  "success": true,
  "message": "Sesión iniciada exitosamente",
  "data": {
    "id": 1,
    "fullName": "Juan Pérez",
    "email": "juan@ejemplo.com",
    "universityName": "Universidad XYZ",
    "createdAt": "2025-01-06T10:30:00"
  }
}
```
✅ Sesión guardada en UserSession  
✅ Navega a DashboardForm  
✅ ID disponible: `UserSession.getInstance().getId()`

**Error - Credenciales (401):**
```json
{
  "success": false,
  "message": "Credenciales incorrectas",
  "errorCode": "AUTHENTICATION_FAILED"
}
```
✅ Muestra: "Credenciales incorrectas"

---

## 📚 Test 3: Crear Materia

### Pasos
1. En DashboardForm
2. Click "Nueva Materia"
3. Completa:
   - Nombre: `Matemáticas I`
   - Código: `MAT101`
   - Profesor: `Dr. García`

### API esperada
```http
POST /api/subjects/user/1
Content-Type: application/json

{
  "name": "Matemáticas I",
  "code": "MAT101",
  "professor": "Dr. García"
}
```

### Respuesta esperada
```json
{
  "success": true,
  "message": "Materia creada exitosamente",
  "data": {
    "id": 1,
    "name": "Matemáticas I",
    "code": "MAT101",
    "professor": "Dr. García",
    "user": { "id": 1 }
  }
}
```
✅ Mostrar en listado

---

## ✅ Test 4: Crear Tarea

### Pasos
1. Seleccionar materia "Matemáticas I"
2. Click "Nueva Tarea"
3. Completa:
   - Título: `Resolver ejercicios cap. 5`
   - Descripción: `Ejercicios 1-20 página 45`
   - Fecha vencimiento: `10/01/2025`
   - Prioridad: `Alta`

### API esperada
```http
POST /api/tasks/subject/1
{
  "title": "Resolver ejercicios cap. 5",
  "description": "Ejercicios 1-20 página 45",
  "dueDate": "2025-01-10",
  "priority": "HIGH",
  "completed": false
}
```

### Respuesta esperada
```json
{
  "success": true,
  "message": "Tarea creada exitosamente",
  "data": {
    "id": 1,
    "title": "Resolver ejercicios cap. 5",
    "completed": false
  }
}
```
✅ Mostrar en listado

---

## 🔄 Test 5: Completar Tarea

### Pasos
1. En listado de tareas
2. Click en tarea
3. Click "Marcar como completada"

### API esperada
```http
PUT /api/tasks/1/toggle
```

### Respuesta esperada
```json
{
  "success": true,
  "message": "Estado de tarea actualizado",
  "data": {
    "id": 1,
    "completed": true
  }
}
```
✅ Mostrar con checkmark

---

## 📝 Test 6: Crear Nota

### Pasos
1. Click "Notas"
2. Seleccionar materia "Matemáticas I"
3. Click "Nueva Nota"
4. Completa:
   - Título: `Derivadas`
   - Contenido: `f'(x) = lim(h→0) (f(x+h)-f(x))/h`
   - Color: Amarillo

### API esperada
```http
POST /api/notes/subject/1/user/1
{
  "title": "Derivadas",
  "content": "f'(x) = lim(h→0) (f(x+h)-f(x))/h",
  "colorCode": "#FFEB3B"
}
```

---

## 📁 Test 7: Subir Archivo

### Pasos
1. Click "Archivos"
2. Seleccionar materia
3. Click "Subir archivo"
4. Seleccionar PDF: `apuntes.pdf`
5. Descripción: `Apuntes clase 1-5`

### API esperada
```http
POST /api/files/subject/1/user/1
{
  "fileName": "apuntes.pdf",
  "description": "Apuntes clase 1-5",
  "fileSize": 2048000
}
```

---

## 🔔 Test 8: Notificaciones

### Crear notificación (desde backend)
```bash
curl -X POST http://localhost:8080/api/notifications/user/1 \
  -H "Content-Type: application/json" \
  -d '{
    "message": "Tienes una tarea pendiente",
    "type": "TASK_REMINDER",
    "isRead": false
  }'
```

### Verificar en Mobile
1. Click "Notificaciones"
2. Debe mostrar la notificación

---

## 🎯 Test 9: Contraste Visual

### Verificar en LoginScreen
- ✅ Fondo azul claro
- ✅ Título en verde oscuro
- ✅ Campos con bordes verdes
- ✅ Texto muy oscuro en campos
- ✅ Botones oscuros

### Verificar en SignUpScreen
- ✅ Mismos colores
- ✅ Estilos consistentes
- ✅ Fuentes bold

---

## 🧬 Test 10: Validaciones Backend

### Email inválido
```bash
curl -X POST http://localhost:8080/api/users/register \
  -H "Content-Type: application/json" \
  -d '{
    "fullName": "Test User",
    "email": "invalid-email",
    "universityName": "Test Uni",
    "passwordHash": "password123"
  }'
```

Respuesta esperada (400):
```json
{
  "success": false,
  "message": "Errores de validación",
  "errorCode": "VALIDATION_ERROR"
}
```

### Contraseña muy corta
```bash
curl -X POST http://localhost:8080/api/users/register \
  -H "Content-Type: application/json" \
  -d '{
    "fullName": "Test",
    "email": "test@test.com",
    "universityName": "Test",
    "passwordHash": "123"
  }'
```

Respuesta esperada (400):
```json
{
  "success": false,
  "message": "Errores de validación",
  "errorCode": "VALIDATION_ERROR"
}
```

---

## 📊 Test 11: Manejo de Errores

### Conexión rechazada
1. Detener el backend
2. Intentar login en mobile
3. Esperar error de conexión

Esperado: Diálogo de error

### Error del servidor
1. Corromper la BD
2. Intentar crear registro

Esperado: "Error interno del servidor"

### Usuario no encontrado
```bash
curl -X GET http://localhost:8080/api/users/99999
```

Respuesta esperada (404):
```json
{
  "success": false,
  "message": "Usuario no encontrado",
  "errorCode": "USER_NOT_FOUND"
}
```

---

## 🔍 Test 12: Pruebas con Postman

### Colección recomendada

```json
{
  "info": {
    "name": "University Organizer",
    "schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
  },
  "item": [
    {
      "name": "Register",
      "request": {
        "method": "POST",
        "url": "http://localhost:8080/api/users/register",
        "body": {
          "mode": "raw",
          "raw": "{\"fullName\": \"Test\", \"email\": \"test@test.com\", \"universityName\": \"Test\", \"passwordHash\": \"password123\"}"
        }
      }
    },
    {
      "name": "Login",
      "request": {
        "method": "POST",
        "url": "http://localhost:8080/api/users/login",
        "body": {
          "mode": "raw",
          "raw": "{\"email\": \"test@test.com\", \"passwordHash\": \"password123\"}"
        }
      }
    }
  ]
}
```

---

## ✨ Checklist de Testing

- [ ] Registro funciona
- [ ] Login funciona
- [ ] Crear materia funciona
- [ ] Crear tarea funciona
- [ ] Marcar tarea completa funciona
- [ ] Crear nota funciona
- [ ] Subir archivo funciona
- [ ] Ver notificaciones funciona
- [ ] Interfaz tiene buen contraste
- [ ] Errores se muestran correctamente
- [ ] Validaciones funcionan
- [ ] API responde con código correcto
- [ ] Sesión persiste correctamente
- [ ] NavegaciónEntre pantallas funciona
- [ ] Escape JSON funciona (caracteres especiales)

---

## 📞 Troubleshooting

### "Connection refused"
```
✅ Verificar que backend está en puerto 8080
✅ Verificar URL en ApiClient: http://localhost:8080/api
```

### "Column not found"
```
✅ Ejecutar init-data.sql
✅ Verificar tablas en BD
```

### "Invalid JSON"
```
✅ Verificar escaping en mobile
✅ Usar ApiClient en lugar de ConnectionRequest directo
```

### "401 Unauthorized"
```
✅ Verificar contraseña correcta
✅ Verificar email registrado
```

---

## 🎓 Conclusión

Si todos los tests pasan:
✅ **MVP está listo para deployment**

**Gracias por testear! 🎉**
