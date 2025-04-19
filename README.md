# Importar Base de Datos en phpMyAdmin

## Requisitos Previos
1. Tener instalado XAMPP (u otro servidor que incluya MySQL y phpMyAdmin)
2. Asegurarse de que los servicios de Apache y MySQL estén activos en el panel de control de XAMPP

## Pasos para Importar

1. **Iniciar phpMyAdmin**
   - Abrir el navegador web
   - Ir a `http://localhost/phpmyadmin`

2. **Preparar la Base de Datos**
   - La base de datos se creará automáticamente durante la importación, ya que el script SQL incluye la instrucción `CREATE DATABASE`

3. **Importar el Archivo SQL**
   - En el menú superior de phpMyAdmin, hacer clic en "Importar"
   - Hacer clic en "Seleccionar archivo"
   - Buscar y seleccionar el archivo `sistema_academico.sql`
   - Asegurarse que el formato de importación sea "SQL"
   - Hacer clic en el botón "Importar" en la parte inferior

4. **Verificar la Importación**
   - En el panel izquierdo, debería aparecer la base de datos `sistema_academico`
   - Al hacer clic en ella, deberían verse las siguientes tablas:
     - ROL
     - USUARIO
     - PROFESOR
     - ESTUDIANTE
     - ADMINISTRADOR
     - CURSO
     - CURSO_ESTUDIANTE
     - ACTIVIDAD
     - GESTION_ACTIVIDADES

## Solución de Problemas

1. **Error de Conexión a phpMyAdmin**
   - Verificar que Apache y MySQL estén activos en XAMPP
   - Comprobar que el puerto 80 esté disponible para Apache

2. **Error durante la Importación**
   - Asegurarse de que MySQL esté corriendo
   - Verificar que no exista una base de datos con el mismo nombre
   - Comprobar que el archivo SQL no esté corrupto

3. **Permisos Insuficientes**
   - Acceder a phpMyAdmin con un usuario que tenga privilegios de administrador
   - Por defecto, el usuario 'root' sin contraseña debería funcionar