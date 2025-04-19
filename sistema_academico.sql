-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Servidor: 127.0.0.1:3306
-- Tiempo de generación: 18-04-2025 a las 14:01:56
-- Versión del servidor: 9.1.0
-- Versión de PHP: 8.3.14

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";

SET FOREIGN_KEY_CHECKS = 0;

DROP DATABASE IF EXISTS `sistema_academico`;
CREATE DATABASE IF NOT EXISTS `sistema_academico` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE `sistema_academico`;

-- ----------------------------
-- Tabla: rol
-- ----------------------------
CREATE TABLE `rol` (
  `id_rol` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(50) NOT NULL,
  PRIMARY KEY (`id_rol`),
  UNIQUE KEY `nombre` (`nombre`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


-- ----------------------------
-- Tabla: usuario
-- ----------------------------
CREATE TABLE `usuario` (
  `id_usu` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(255) NOT NULL,
  `correo` varchar(191) NOT NULL,
  `contraseña` varchar(64) NOT NULL,
  `id_rol` int NOT NULL,
  `fecha_creacion` datetime NOT NULL,
  `ultima_conexion` datetime DEFAULT NULL,
  PRIMARY KEY (`id_usu`),
  UNIQUE KEY `correo` (`correo`),
  KEY `id_rol` (`id_rol`),
  CONSTRAINT `usuario_fk_rol` FOREIGN KEY (`id_rol`) REFERENCES `rol` (`id_rol`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;



-- ----------------------------
-- Tabla: actividad
-- ----------------------------
CREATE TABLE `actividad` (
  `id_actividad` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(255) NOT NULL,
  `enlace` varchar(255) NOT NULL,
  PRIMARY KEY (`id_actividad`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ----------------------------
-- Tabla: gestion_actividades
-- ----------------------------
CREATE TABLE `gestion_actividades` (
  `id_rol` int NOT NULL,
  `id_actividad` int NOT NULL,
  PRIMARY KEY (`id_rol`, `id_actividad`),
  KEY `id_actividad` (`id_actividad`),
  CONSTRAINT `gestion_fk_rol` FOREIGN KEY (`id_rol`) REFERENCES `rol` (`id_rol`),
  CONSTRAINT `gestion_fk_actividad` FOREIGN KEY (`id_actividad`) REFERENCES `actividad` (`id_actividad`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ----------------------------
-- Tabla: administrador
-- ----------------------------
CREATE TABLE `administrador` (
  `id_admin` int NOT NULL AUTO_INCREMENT,
  `idUsuario` int NOT NULL,
  `departamento` varchar(255) DEFAULT NULL,
  `fecha_ingreso` datetime DEFAULT NULL,
  PRIMARY KEY (`id_admin`),
  KEY `idUsuario` (`idUsuario`),
  CONSTRAINT `admin_fk_usuario` FOREIGN KEY (`idUsuario`) REFERENCES `usuario` (`id_usu`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ----------------------------
-- Tabla: profesor
-- ----------------------------
CREATE TABLE `profesor` (
  `id_profesor` int NOT NULL AUTO_INCREMENT,
  `idUsuario` int NOT NULL,
  `fecha_nacimiento` date DEFAULT NULL,
  `direccion` varchar(255) DEFAULT NULL,
  `telefono` varchar(20) DEFAULT NULL,
  `grado_academico` varchar(255) DEFAULT NULL,
  `especializacion` varchar(255) DEFAULT NULL,
  `fecha_contratacion` datetime DEFAULT NULL,
  `estado` enum('Activo','Baja') NOT NULL,
  PRIMARY KEY (`id_profesor`),
  KEY `idUsuario` (`idUsuario`),
  CONSTRAINT `profesor_fk_usuario` FOREIGN KEY (`idUsuario`) REFERENCES `usuario` (`id_usu`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ----------------------------
-- Tabla: estudiante
-- ----------------------------
CREATE TABLE `estudiante` (
  `id_estudiante` int NOT NULL AUTO_INCREMENT,
  `idUsuario` int NOT NULL,
  `fecha_nacimiento` date DEFAULT NULL,
  `direccion` varchar(255) DEFAULT NULL,
  `telefono` varchar(20) DEFAULT NULL,
  `numero_identificacion` varchar(50) DEFAULT NULL,
  `estado` enum('Activo','Inactivo') NOT NULL,
  `promedio_academico` decimal(5,2) DEFAULT NULL,
  PRIMARY KEY (`id_estudiante`),
  UNIQUE KEY `numero_identificacion` (`numero_identificacion`),
  KEY `idUsuario` (`idUsuario`),
  CONSTRAINT `estudiante_fk_usuario` FOREIGN KEY (`idUsuario`) REFERENCES `usuario` (`id_usu`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ----------------------------
-- Tabla: curso
-- ----------------------------
CREATE TABLE `curso` (
  `id_curso` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(255) NOT NULL,
  `codigo` varchar(100) NOT NULL,
  `descripcion` text,
  `idProfesor` int DEFAULT NULL,
  PRIMARY KEY (`id_curso`),
  UNIQUE KEY `codigo` (`codigo`),
  KEY `idProfesor` (`idProfesor`),
  CONSTRAINT `curso_fk_profesor` FOREIGN KEY (`idProfesor`) REFERENCES `profesor` (`id_profesor`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ----------------------------
-- Tabla: curso_estudiante
-- ----------------------------
CREATE TABLE `curso_estudiante` (
  `id_curso` int NOT NULL,
  `id_estudiante` int NOT NULL,
  PRIMARY KEY (`id_curso`,`id_estudiante`),
  KEY `id_estudiante` (`id_estudiante`),
  CONSTRAINT `curso_estudiante_fk_curso` FOREIGN KEY (`id_curso`) REFERENCES `curso` (`id_curso`),
  CONSTRAINT `curso_estudiante_fk_estudiante` FOREIGN KEY (`id_estudiante`) REFERENCES `estudiante` (`id_estudiante`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ----------------------------
-- Tabla: tarea
-- ----------------------------
CREATE TABLE `tarea` (
  `id_tarea` int NOT NULL AUTO_INCREMENT,
  `titulo` varchar(255) NOT NULL,
  `descripcion` text,
  `fecha_asignacion` datetime DEFAULT NULL,
  `fecha_entrega` datetime DEFAULT NULL,
  `id_curso` int NOT NULL,
  PRIMARY KEY (`id_tarea`),
  KEY `id_curso` (`id_curso`),
  CONSTRAINT `tarea_fk_curso` FOREIGN KEY (`id_curso`) REFERENCES `curso` (`id_curso`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ----------------------------
-- Tabla: nota_tarea
-- ----------------------------
CREATE TABLE `nota_tarea` (
  `id_nota` int NOT NULL AUTO_INCREMENT,
  `id_tarea` int NOT NULL,
  `id_estudiante` int NOT NULL,
  `nota` decimal(5,2) DEFAULT NULL,
  `fecha_evaluacion` datetime DEFAULT NULL,
  `comentario` text,
  PRIMARY KEY (`id_nota`),
  UNIQUE KEY `uk_tarea_estudiante` (`id_tarea`,`id_estudiante`),
  KEY `id_estudiante` (`id_estudiante`),
  CONSTRAINT `nota_fk_tarea` FOREIGN KEY (`id_tarea`) REFERENCES `tarea` (`id_tarea`),
  CONSTRAINT `nota_fk_estudiante` FOREIGN KEY (`id_estudiante`) REFERENCES `estudiante` (`id_estudiante`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ============================
-- DATOS
-- ============================

INSERT INTO `rol` (`id_rol`, `nombre`) VALUES
(1, 'Estudiante'),
(2, 'Profesor'),
(3, 'Administrador');

INSERT INTO `usuario` (`id_usu`, `nombre`, `correo`, `contraseña`, `id_rol`, `fecha_creacion`, `ultima_conexion`) VALUES
(1, 'Admin User', 'admin@example.com', '240BE518FABD2724DDB6F04EEB1DA5967448D7E831C08C8FA822809F74C720A9', 3, '2025-04-16 20:58:40', '2025-04-18 00:00:00'),
(2, 'Profesor User', 'profesor@example.com', 'CFFA965D9FAA1D453F2D336294B029A7F84F485F75CE2A2C723065453B12B03B', 2, '2025-04-16 20:58:40', '2025-04-17 00:00:00'),
(3, 'Estudiante User', 'estudiante@example.com', '2E63A1090735F47213FEA3B974418E3E42437325F313B3D3D2F6238CC22298F9', 1, '2025-04-16 20:58:40', '2025-04-17 00:00:00');

INSERT INTO `actividad` (`id_actividad`, `nombre`, `enlace`) VALUES
(1, 'Gestionar Profesores', 'dashboard/admin/profesores.jsp'),
(2, 'Gestionar Estudiantes', 'dashboard/admin/estudiantes.jsp'),
(3, 'Gestionar Cursos', 'dashboard/admin/cursos.jsp'),
(4, 'Ver Mis Cursos', 'dashboard/profesor/mis-cursos.jsp'),
(5, 'Crear Tarea', 'dashboard/profesor/nueva-tarea.jsp'),
(6, 'Calificar Tareas', 'dashboard/profesor/calificar-tareas.jsp'),
(7, 'Ver Cursos Estudiante', 'dashboard/estudiante/mis-cursos.jsp'),
(8, 'Ver Tareas Estudiante', 'dashboard/estudiante/mis-tareas.jsp'),
(9, 'Ver Notas Estudiante', 'dashboard/estudiante/mis-notas.jsp');

INSERT INTO `gestion_actividades` (`id_rol`, `id_actividad`) VALUES
(1, 7),
(1, 8),
(1, 9),
(2, 4),
(2, 5),
(2, 6),
(3, 1),
(3, 2),
(3, 3);

INSERT INTO `administrador` (`id_admin`, `idUsuario`, `departamento`, `fecha_ingreso`) VALUES
(1, 1, 'Sistemas', '2019-01-10 08:00:00');

INSERT INTO `profesor` (`id_profesor`, `idUsuario`, `fecha_nacimiento`, `direccion`, `telefono`, `grado_academico`, `especializacion`, `fecha_contratacion`, `estado`) VALUES
(1, 2, '1980-05-15', 'Calle Falsa 123', '555-1234', 'Maestría', 'Ingeniería de Sistemas', '2020-08-01 09:00:00', 'Activo');

INSERT INTO `estudiante` (`id_estudiante`, `idUsuario`, `fecha_nacimiento`, `direccion`, `telefono`, `numero_identificacion`, `estado`, `promedio_academico`) VALUES
(1, 3, '2000-03-20', 'Avenida Siempre Viva 456', '555-5678', 'ID123456', 'Activo', 85.50);

INSERT INTO `curso` (`id_curso`, `nombre`, `codigo`, `descripcion`, `idProfesor`) VALUES
(1, 'Introducción a la Programación', 'CS101', 'Curso introductorio de programación.', 1),
(2, 'Bases de Datos', 'CS102', 'Curso sobre diseño y gestión de bases de datos.', 1);

INSERT INTO `curso_estudiante` (`id_curso`, `id_estudiante`) VALUES
(1, 1),
(2, 1);

INSERT INTO `tarea` (`id_tarea`, `titulo`, `descripcion`, `fecha_asignacion`, `fecha_entrega`, `id_curso`) VALUES
(1, 'Tarea 1', 'Resolver ejercicios de lógica.', '2023-04-01 08:00:00', '2023-04-15 23:59:59', 1),
(2, 'Tarea 2', 'Diseñar una base de datos simple.', '2023-04-05 08:00:00', '2023-04-20 23:59:59', 2);

INSERT INTO `nota_tarea` (`id_nota`, `id_tarea`, `id_estudiante`, `nota`, `fecha_evaluacion`, `comentario`) VALUES
(1, 1, 1, 92.50, '2023-04-16 14:30:00', 'Buen desempeño'),
(2, 2, 1, 88.00, '2023-04-21 15:45:00', 'Buen trabajo, pero hay aspectos a mejorar');

SET FOREIGN_KEY_CHECKS = 1;
COMMIT;
