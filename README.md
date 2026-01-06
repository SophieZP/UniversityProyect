# 📚 University Organizer - MVP v0.0.1

Un sistema completo de organización universitaria con **Backend Spring Boot** y **Cliente Mobile Codename One**.

## 🎯 Características

### Gestión de Materias
- ✅ Crear y organizar materias/asignaturas
- ✅ Ver detalles de materias
- ✅ Eliminar materias

### Gestión de Tareas
- ✅ Crear tareas de estudio
- ✅ Marcar tareas como completadas
- ✅ Eliminar tareas
- ✅ Visualizar en dashboard

### Gestión de Notas
- ✅ Crear notas de clase
- ✅ Colorear notas
- ✅ Editar contenido
- ✅ Eliminar notas

### Notificaciones
- ✅ Recibir notificaciones
- ✅ Marcar como leídas
- ✅ Ver historial

### Gestión de Archivos
- ✅ Subir archivos por materia
- ✅ Organizar archivos
- ✅ Descargar recursos

### Autenticación
- ✅ Registro de usuarios
- ✅ Login seguro
- ✅ Sesión persistente

---

## 🏗️ Arquitectura

### Backend - Spring Boot

```
backend/
├── src/main/java/SophieZP/demo/
│   ├── controller/          # REST Endpoints
│   ├── service/            # Lógica de negocio
│   ├── entity/             # Modelos JPA
│   ├── repository/         # Acceso a datos
│   ├── dto/                # DTOs de transferencia
│   └── exception/          # Excepciones personalizadas
├── pom.xml
└── README.md
```

**Stack:**
- Java 25
- Spring Boot 4.0.1
- PostgreSQL
- Spring Data JPA
- Lombok
- Jakarta Validation

### Mobile - Codename One

```
mobile/myapp/common/src/main/java/com/sophiezp/
├── ApiClient.java          # Cliente HTTP centralizado
├── LoginScreen.java        # Pantalla de login
├── SignUpScreen.java       # Pantalla de registro
├── DashboardForm.java      # Dashboard principal
├── UserSession.java        # Gestión de sesión
├── *Form.java              # Otras pantallas
└── MyApp.java              # Punto de entrada
```

**Features:**
- ✅ UI mejorada con mejor contraste
- ✅ Cliente HTTP centralizado
- ✅ Validaciones locales
- ✅ Manejo robusto de errores

---

## 🚀 Inicio Rápido

### Requisitos
- Java 25+
- Maven 3.8+
- PostgreSQL 14+

### Configurar Backend

```bash
cd backend

# Configurar base de datos en application.properties
# spring.datasource.url=jdbc:postgresql://localhost:5432/university_organizer
# spring.datasource.username=postgres
# spring.datasource.password=tu_contraseña

# Compilar
mvn clean compile

# Ejecutar
mvn spring-boot:run
```

La API estará disponible en: `http://localhost:8080`

### Configurar Mobile

```bash
cd mobile/myapp

# Compilar
./build.sh      # Linux/Mac
# o
build.bat       # Windows

# Ejecutar
./run.sh        # Linux/Mac
# o
run.bat         # Windows
```

---

## 📚 Documentación

### 📖 [API Documentation](./backend/API_DOCUMENTATION.md)
Documentación completa de todos los endpoints REST

### 📖 [Mobile Documentation](./mobile/MOBILE_DOCUMENTATION.md)
Guía de desarrollo de la aplicación mobile

### 📖 [MVP Checklist](./MVP_CHECKLIST.md)
Lista de features completados para MVP

### 📖 [Cambios Realizados](./CAMBIOS_REALIZADOS.md)
Resumen detallado de todas las mejoras realizadas

---

## 🔌 API REST

### Autenticación

**Registro:**
```http
POST /api/users/register
Content-Type: application/json

{
  "fullName": "Juan Pérez",
  "email": "juan@ejemplo.com",
  "universityName": "Universidad XYZ",
  "passwordHash": "contraseña123"
}
```

**Login:**
```http
POST /api/users/login
Content-Type: application/json

{
  "email": "juan@ejemplo.com",
  "passwordHash": "contraseña123"
}
```

### Respuesta Estándar

```json
{
  "success": true,
  "message": "Operación exitosa",
  "data": {
    "id": 1,
    "fullName": "Juan Pérez",
    "email": "juan@ejemplo.com",
    "universityName": "Universidad XYZ",
    "createdAt": "2025-01-06T10:30:00"
  },
  "errorCode": null
}
```

### Códigos HTTP
- `201 Created` - Recurso creado
- `200 OK` - Operación exitosa
- `400 Bad Request` - Validación fallida
- `401 Unauthorized` - No autenticado
- `404 Not Found` - Recurso no encontrado
- `500 Internal Server Error` - Error del servidor

---

## 🎨 Mejoras de UI/UX

### Contraste Visual Mejorado

| Elemento | Color | Contraste |
|----------|-------|-----------|
| Background | `#E8F4F8` | Azul claro |
| Título | `#1B5E20` | Verde oscuro |
| Campos | `#FFFFFF` | Blanco puro |
| Texto campos | `#1A1A1A` | Gris muy oscuro |
| Borde campos | `#4CAF50` | Verde brillante |
| Botón primario | `#2E7D32` | Verde oscuro |
| Botón secundario | `#D32F2F` | Rojo oscuro |

**Cambios principales:**
- ✅ Bordes 2px en campos de entrada
- ✅ Colores más oscuros y saturados
- ✅ Fuentes bold en títulos y botones
- ✅ Padding aumentado para mejor espaciado
- ✅ Alineación mejorada

---

## 🔒 Seguridad

### En Desarrollo (Actual)
- Contraseñas en texto plano (solo para MVP)
- Validación en DTOs
- Manejo centralizado de excepciones

### En Producción (Futuro)
- [ ] BCrypt para encriptación de contraseñas
- [ ] JWT/OAuth2 para autenticación
- [ ] HTTPS obligatorio
- [ ] Rate limiting en API
- [ ] CORS configurado

---

## 🧪 Testing

Para probar manualmente:

1. **Registro:**
   - Ir a SignUpScreen
   - Completar formulario
   - Click en "Registrarse"
   - Debería redirigir a LoginScreen

2. **Login:**
   - Usar credenciales registradas
   - Click en "Iniciar Sesión"
   - Debería mostrar DashboardForm

3. **Crear Materia:**
   - En Dashboard, click en "Nueva Materia"
   - Completar datos
   - Click en "Crear"

4. **Crear Tarea:**
   - Seleccionar una materia
   - Click en "Nueva Tarea"
   - Completar información

---

## 📊 Base de Datos

### Tablas Principales
- `users` - Usuarios registrados
- `subjects` - Materias/asignaturas
- `tasks` - Tareas de estudio
- `notes` - Notas de clase
- `notifications` - Notificaciones
- `file_uploads` - Archivos subidos
- `evaluations` - Evaluaciones
- `study_resources` - Recursos de estudio

### Script de Inicialización
Ver: [init-data.sql](./backend/src/main/resources/init-data.sql)

---

## 🛠️ Herramientas y Tecnologías

**Backend:**
- Spring Boot 4.0.1
- Spring Data JPA
- PostgreSQL
- Maven
- Lombok
- Jakarta Validation

**Mobile:**
- CodeName One
- Java
- Maven

**Documentación:**
- Markdown
- Javadoc

---

## 📋 Estado del Proyecto

**Versión:** 0.0.1-SNAPSHOT  
**Estado:** ✅ MVP Completo y Funcional  
**Último Update:** 6 Enero 2025

### Completado
- ✅ Backend con 6 controladores
- ✅ 10+ endpoints de API
- ✅ Mobile con UI mejorada
- ✅ Autenticación
- ✅ Gestión de materias, tareas, notas
- ✅ Documentación completa
- ✅ Manejo de errores robusto

### Próximas Mejoras
- [ ] BCrypt para contraseñas
- [ ] JWT/OAuth2
- [ ] Caching
- [ ] Paginación
- [ ] Notificaciones push
- [ ] Modo offline
- [ ] Colaboración entre usuarios

---

## 👥 Autor

**Sophie ZP**  
📧 Email: [tu_email]  
🔗 GitLab: [tu_gitlab]

---

## 📄 Licencia

Este proyecto está bajo la Licencia MIT. Ver archivo [LICENSE](./LICENSE) para más detalles.

---

## 📞 Soporte

Para reportar bugs o sugerencias:
1. Abrir un issue en GitLab
2. Describir el problema con detalles
3. Incluir pasos para reproducir

---

## 🚀 Deployment

Ver instrucciones de deployment en:
- [Backend Deployment](./backend/HELP.md)
- [Mobile Deployment](./mobile/README.adoc)

---

**¡Gracias por usar University Organizer! 🎓**

## Suggestions for a good README

Every project is different, so consider which of these sections apply to yours. The sections used in the template are suggestions for most open source projects. Also keep in mind that while a README can be too long and detailed, too long is better than too short. If you think your README is too long, consider utilizing another form of documentation rather than cutting out information.

## Name
Choose a self-explaining name for your project.

## Description
Let people know what your project can do specifically. Provide context and add a link to any reference visitors might be unfamiliar with. A list of Features or a Background subsection can also be added here. If there are alternatives to your project, this is a good place to list differentiating factors.

## Badges
On some READMEs, you may see small images that convey metadata, such as whether or not all the tests are passing for the project. You can use Shields to add some to your README. Many services also have instructions for adding a badge.

## Visuals
Depending on what you are making, it can be a good idea to include screenshots or even a video (you'll frequently see GIFs rather than actual videos). Tools like ttygif can help, but check out Asciinema for a more sophisticated method.

## Installation
Within a particular ecosystem, there may be a common way of installing things, such as using Yarn, NuGet, or Homebrew. However, consider the possibility that whoever is reading your README is a novice and would like more guidance. Listing specific steps helps remove ambiguity and gets people to using your project as quickly as possible. If it only runs in a specific context like a particular programming language version or operating system or has dependencies that have to be installed manually, also add a Requirements subsection.

## Usage
Use examples liberally, and show the expected output if you can. It's helpful to have inline the smallest example of usage that you can demonstrate, while providing links to more sophisticated examples if they are too long to reasonably include in the README.

## Support
Tell people where they can go to for help. It can be any combination of an issue tracker, a chat room, an email address, etc.

## Roadmap
If you have ideas for releases in the future, it is a good idea to list them in the README.

## Contributing
State if you are open to contributions and what your requirements are for accepting them.

For people who want to make changes to your project, it's helpful to have some documentation on how to get started. Perhaps there is a script that they should run or some environment variables that they need to set. Make these steps explicit. These instructions could also be useful to your future self.

You can also document commands to lint the code or run tests. These steps help to ensure high code quality and reduce the likelihood that the changes inadvertently break something. Having instructions for running tests is especially helpful if it requires external setup, such as starting a Selenium server for testing in a browser.

## Authors and acknowledgment
Show your appreciation to those who have contributed to the project.

## License
For open source projects, say how it is licensed.

## Project status
If you have run out of energy or time for your project, put a note at the top of the README saying that development has slowed down or stopped completely. Someone may choose to fork your project or volunteer to step in as a maintainer or owner, allowing your project to keep going. You can also make an explicit request for maintainers.
