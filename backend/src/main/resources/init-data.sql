-- Script de datos iniciales para pruebas

-- Insertar usuario de prueba si no existe
INSERT INTO users (id, full_name, email, password_hash, university_name) 
VALUES (1, 'Usuario Prueba', 'test@example.com', 'password123', 'Universidad Test')
ON CONFLICT (email) DO NOTHING;

-- Insertar materias de prueba
INSERT INTO subjects (id, name, teacher_name, min_passing_grade, color_code, user_id, created_at)
VALUES 
  (1, 'Cálculo Multivariado', 'Dr. García', 3.0, '#FF6B6B', 1, NOW()),
  (2, 'Física II', 'Dra. López', 3.0, '#4ECDC4', 1, NOW()),
  (3, 'Programación Avanzada', 'Ing. Rodríguez', 3.0, '#45B7D1', 1, NOW()),
  (4, 'Base de Datos', 'Ing. Martínez', 3.0, '#FFA07A', 1, NOW()),
  (5, 'Ecuaciones Diferenciales', 'Dr. Fernández', 3.0, '#98D8C8', 1, NOW()),
  (6, 'Álgebra Lineal', 'Dr. Torres', 3.0, '#F7DC6F', 1, NOW()),
  (7, 'Geometría Computacional', 'Ing. Gómez', 3.0, '#BB8FCE', 1, NOW())
ON CONFLICT (id) DO NOTHING;

-- Insertar tareas de prueba
INSERT INTO tasks (id, title, description, due_date, is_completed, subject_id, created_at)
VALUES
  (1, 'Tarea 1: Integrales Triples', 'Resolver problemas 1-10 del capítulo 5', NOW() + INTERVAL '5 days', false, 1, NOW()),
  (2, 'Laboratorio 1: Ley de Newton', 'Experimentar con fuerzas en diferentes superficies', NOW() + INTERVAL '7 days', false, 2, NOW()),
  (3, 'Proyecto Final: Sistema ORM', 'Implementar un micro ORM en Java', NOW() + INTERVAL '14 days', false, 3, NOW())
ON CONFLICT (id) DO NOTHING;

-- Insertar notas de prueba
INSERT INTO notes (id, title, content, color_code, subject_id, user_id, created_at, updated_at)
VALUES
  (1, 'Método de Integración por Partes', 'Recordar la fórmula: ∫u dv = uv - ∫v du. Utilizar cuando tengas funciones logarítmicas o inversas.', '#FF6B6B', 1, 1, NOW(), NOW()),
  (2, 'Teorema del Cambio de Variable', 'Si u=g(x), entonces ∫f(g(x))g''(x)dx = ∫f(u)du. Simplifica integrales complicadas.', '#FFD700', 1, 1, NOW(), NOW()),
  (3, 'Leyes de Newton Resumen', 'Primera: F=0 entonces v=cte. Segunda: F=ma. Tercera: Acción-Reacción.', '#4ECDC4', 2, 1, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

-- Insertar archivos de prueba
INSERT INTO file_uploads (id, file_name, file_type, file_size, file_path, uploaded_at, description, subject_id, user_id)
VALUES
  (1, 'Apuntes_Integrales.pdf', 'PDF', 2048000, '/uploads/apuntes_integrales.pdf', NOW(), 'Apuntes completos sobre integrales triples', 1, 1),
  (2, 'Ejemplos_Fisica.docx', 'DOCX', 1024000, '/uploads/ejemplos_fisica.docx', NOW(), 'Ejemplos resueltos de física', 2, 1),
  (3, 'Código_ORM.java', 'JAVA', 512000, '/uploads/orm_codigo.java', NOW(), 'Código base para el proyecto final', 3, 1)
ON CONFLICT (id) DO NOTHING;

-- Insertar notificaciones de prueba
INSERT INTO notifications (id, title, message, type, is_read, user_id, created_at, related_subject_id, related_task_id)
VALUES
  (1, 'Entrega Próxima', 'La tarea de Cálculo vence en 2 días', 'TASK_REMINDER', false, 1, NOW(), 1, 1),
  (2, 'Nuevo Material', 'Se subieron nuevos apuntes en Física II', 'NEW_MATERIAL', false, 1, NOW() - INTERVAL '1 day', 2, null),
  (3, 'Recordatorio de Examen', 'Examen parcial de Programación Avanzada próxima semana', 'EXAM_ALERT', false, 1, NOW() - INTERVAL '3 days', 3, null)
ON CONFLICT (id) DO NOTHING;
