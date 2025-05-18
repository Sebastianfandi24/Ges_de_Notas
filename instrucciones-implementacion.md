# Instrucciones para Implementar Solución de Gestión de Tareas

## Problema Identificado

La página de "Gestión de Tareas" para profesores presenta los siguientes problemas:

1. La página muestra correctamente las tareas pero las funciones de editar, calificar y eliminar no funcionan
2. Existe un conflicto entre múltiples servlets que gestionan diferentes rutas relacionadas con tareas
3. No se está incluyendo el archivo JavaScript necesario para manejar las operaciones en el cliente

## Solución Implementada

La solución consta de los siguientes cambios:

### 1. Modificación de `profesortareas.jsp`

Reemplazar el archivo actual con `profesortareas-fixed.jsp`, que incluye los siguientes cambios:
- Configuración correcta de los formularios para enviar a `/profesor/tareas` en vez de `/profesor/tareas/crear` o `/profesor/tareas/editar`
- Inclusión del archivo JavaScript `tareas-profesor.js`
- Definición de la variable global `window.contextPath` para uso en JavaScript

### 2. Modificación de `tareas-profesor.js`

Se ha actualizado el archivo para:
- Incluir un indicador de carga exitosa con la variable `window.tareasScriptLoaded`
- Asegurar que los formularios apunten a las rutas correctas
- Mejorar el manejo de errores

### 3. Nuevo archivo de diagnóstico

Se ha creado un archivo `diagnostico-rutas.jsp` para verificar que todas las rutas estén funcionando correctamente.

## Pasos para Implementar

1. **Hacer copias de seguridad**:
   ```
   cp web/profesortareas.jsp web/profesortareas.jsp.bak
   cp web/js/tareas-profesor.js web/js/tareas-profesor.js.bak
   ```

2. **Reemplazar archivos**:
   ```
   cp web/profesortareas-fixed.jsp web/profesortareas.jsp
   ```

3. **Verificar rutas de servlets en `web.xml`** para asegurar que no haya conflictos:
   - `ProfesorTareasServlet` debe mapear a `/profesor/tareas`
   - `GestionTareasServlet` debe mapear a `/profesor/tareas/*`
   - `CalificarTareasServlet` debe mapear a `/profesor/tareas/calificar`

4. **Desplegar aplicación y verificar** usando la página de diagnóstico:
   - Acceder a `http://tu-servidor/Ges_de_Notas/diagnostico-rutas.jsp`

5. **Verificar funcionamiento**:
   - Crear una nueva tarea
   - Editar una tarea existente
   - Calificar una tarea
   - Eliminar una tarea

## Solución de Problemas

Si después de implementar los cambios siguen habiendo problemas:

1. **Verificar consola del navegador** para errores JavaScript
2. **Revisar logs del servidor** para errores en los servlets
3. **Comprobar que `tareas-profesor.js` se está cargando** (debería mostrar un mensaje en la consola)
4. **Verificar que el contexto de la aplicación sea correcto** en `window.contextPath`

## Notas Adicionales

- Si se observan problemas de permisos o redirecciones, verificar las políticas de seguridad en los filtros
- La ruta `/profesor/tareas/estudiantes` se ha mantenido como respaldo, pero se recomienda usar `/profesor/tareas/{tareaId}?includeEstudiantes=true` como se implementa en `GestionTareasServlet`
