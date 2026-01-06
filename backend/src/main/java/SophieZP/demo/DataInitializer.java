package SophieZP.demo;

import SophieZP.demo.entity.*;
import SophieZP.demo.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

@Component
public class DataInitializer {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private NoteRepository noteRepository;

    @Autowired
    private FileUploadRepository fileUploadRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @EventListener(ApplicationReadyEvent.class)
    public void initializeData() {
        // Verificar si ya existe datos
        if (userRepository.count() > 0) {
            System.out.println("✓ Datos ya inicializados, omitiendo carga...");
            return;
        }

        System.out.println("📝 Inicializando datos de prueba...");

        // Crear usuario
        User user = new User();
        user.setFullName("Usuario Prueba");
        user.setEmail("test@example.com");
        user.setPasswordHash("password123");
        user.setUniversityName("Universidad Test");
        user = userRepository.save(user);
        System.out.println("✓ Usuario creado: " + user.getId());

        // Crear materias
        Subject[] subjects = new Subject[7];
        String[] subjectNames = {
            "Cálculo Multivariado",
            "Física II",
            "Programación Avanzada",
            "Base de Datos",
            "Ecuaciones Diferenciales",
            "Álgebra Lineal",
            "Geometría Computacional"
        };
        String[] colors = {"#FF6B6B", "#4ECDC4", "#45B7D1", "#FFA07A", "#98D8C8", "#F7DC6F", "#BB8FCE"};

        for (int i = 0; i < 7; i++) {
            Subject subject = new Subject();
            subject.setName(subjectNames[i]);
            subject.setTeacherName("Dr. Profesor " + (i + 1));
            subject.setMinPassingGrade(3.0);
            subject.setColorCode(colors[i]);
            subject.setUser(user);
            subjects[i] = subjectRepository.save(subject);
        }
        System.out.println("✓ 7 Materias creadas");

        // Crear tareas
        for (int i = 0; i < 3; i++) {
            Task task = new Task();
            task.setTitle("Tarea " + (i + 1));
            task.setDescription("Descripción de la tarea " + (i + 1));
            task.setDueDate(LocalDateTime.now().plusDays(5 + i));
            task.setIsCompleted(false);
            task.setType(Task.TaskType.HOMEWORK);
            task.setSubject(subjects[i]);
            taskRepository.save(task);
        }
        System.out.println("✓ 3 Tareas creadas");

        // Crear notas
        String[] noteTitles = {
            "Método de Integración por Partes",
            "Teorema del Cambio de Variable",
            "Leyes de Newton Resumen"
        };
        String[] noteContents = {
            "Recordar la fórmula: ∫u dv = uv - ∫v du",
            "Si u=g(x), entonces la integral se simplifica",
            "Primera: F=0. Segunda: F=ma. Tercera: Acción-Reacción"
        };

        for (int i = 0; i < 3; i++) {
            Note note = new Note();
            note.setTitle(noteTitles[i]);
            note.setContent(noteContents[i]);
            note.setColorCode(colors[i]);
            note.setSubject(subjects[i]);
            note.setUser(user);
            noteRepository.save(note);
        }
        System.out.println("✓ 3 Notas creadas");

        // Crear archivos
        String[] fileNames = {
            "Apuntes_Integrales.pdf",
            "Ejemplos_Fisica.docx",
            "Código_ORM.java"
        };

        for (int i = 0; i < 3; i++) {
            FileUpload file = new FileUpload();
            file.setFileName(fileNames[i]);
            file.setFileType(fileNames[i].substring(fileNames[i].lastIndexOf('.') + 1));
            file.setFileSize(1024000L - (i * 512000L));
            file.setFilePath("/uploads/" + fileNames[i]);
            file.setDescription("Archivo de prueba " + (i + 1));
            file.setSubject(subjects[i]);
            file.setUser(user);
            fileUploadRepository.save(file);
        }
        System.out.println("✓ 3 Archivos creados");

        // Crear notificaciones
        Notification notif1 = new Notification();
        notif1.setTitle("Entrega Próxima");
        notif1.setMessage("La tarea de Cálculo vence en 2 días");
        notif1.setType(Notification.NotificationType.TASK_REMINDER);
        notif1.setIsRead(false);
        notif1.setUser(user);
        notif1.setRelatedSubjectId(subjects[0].getId());
        notificationRepository.save(notif1);

        Notification notif2 = new Notification();
        notif2.setTitle("Nuevo Material");
        notif2.setMessage("Se subieron nuevos apuntes en Física II");
        notif2.setType(Notification.NotificationType.NEW_MATERIAL);
        notif2.setIsRead(false);
        notif2.setUser(user);
        notif2.setRelatedSubjectId(subjects[1].getId());
        notificationRepository.save(notif2);

        System.out.println("✓ 2 Notificaciones creadas");
        System.out.println("\n✅ DATOS INICIALIZADOS CORRECTAMENTE");
        System.out.println("📧 Email: test@example.com");
        System.out.println("🔐 Contraseña: password123");
    }
}
