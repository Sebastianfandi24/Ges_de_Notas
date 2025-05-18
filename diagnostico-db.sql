-- Script de diagnóstico para problemas con tareas del profesor
-- Ejecutar en phpMyAdmin o cualquier cliente MySQL

-- 1. Verificar tablas principales
SHOW TABLES;

-- 2. Estructura de la tabla de usuarios
DESCRIBE usuario;

-- 3. Estructura de la tabla de profesores
DESCRIBE profesor;

-- 4. Estructura de la tabla de cursos
DESCRIBE curso;

-- 5. Estructura de la tabla de tareas
DESCRIBE tarea;

-- 6. Verificar usuarios existentes
SELECT id_usu, nombre, correo, id_rol, fecha_creacion FROM usuario;

-- 7. Verificar profesores existentes
SELECT p.id_profesor, p.idUsuario, u.nombre, u.correo 
FROM profesor p 
JOIN usuario u ON p.idUsuario = u.id_usu;

-- 8. Verificar cursos asignados a profesores
SELECT c.id_curso, c.nombre, c.codigo, c.idProfesor, 
       p.id_profesor, u.nombre as nombre_profesor
FROM curso c
JOIN profesor p ON c.idProfesor = p.id_profesor
JOIN usuario u ON p.idUsuario = u.id_usu;

-- 9. Verificar tareas existentes
SELECT t.id_tarea, t.titulo, t.id_curso, c.nombre as curso_nombre,
       c.idProfesor, p.id_profesor, u.nombre as nombre_profesor
FROM tarea t
JOIN curso c ON t.id_curso = c.id_curso
JOIN profesor p ON c.idProfesor = p.id_profesor
JOIN usuario u ON p.idUsuario = u.id_usu;

-- 10. Verificar relación entre usuario con rol de profesor y la tabla profesor
SELECT u.id_usu, u.nombre, u.id_rol, p.id_profesor
FROM usuario u
LEFT JOIN profesor p ON u.id_usu = p.idUsuario
WHERE u.id_rol = 2;  -- 2 es el rol de profesor según el SQL de creación

-- 11. Verificar si hay tareas sin asociación correcta a cursos
SELECT t.id_tarea, t.titulo, t.id_curso
FROM tarea t
LEFT JOIN curso c ON t.id_curso = c.id_curso
WHERE c.id_curso IS NULL;

-- 12. Verificar si hay cursos sin profesor asignado
SELECT id_curso, nombre, codigo, idProfesor 
FROM curso 
WHERE idProfesor IS NULL;
