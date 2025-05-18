// tareas-profesor.js - Funcionalidades para gestión de tareas del profesor

document.addEventListener('DOMContentLoaded', function() {
  // Botones para calificar tareas
  document.querySelectorAll('.btn-grade').forEach(btn => {
    btn.addEventListener('click', async function() {
      const tareaId = this.dataset.tareaId;
      const tareaTitulo = this.dataset.tareaTitulo;
      document.getElementById('gradeTareaId').value = tareaId;
      document.getElementById('gradeTareaTitle').textContent = tareaTitulo;
      
      try {
        // Usar el nuevo endpoint
        const response = await fetch(`${window.contextPath}/profesor/tareas_api/${tareaId}?includeEstudiantes=true`);
        
        if (!response.ok) {
          const errorText = await response.text();
          throw new Error('Error cargando estudiantes: ' + errorText);
        }
        
        const data = await response.json();
        console.log("Datos recibidos:", data);
        
        const tableBody = document.getElementById('gradeTableBody');          if (data.estudiantes && Array.isArray(data.estudiantes)) {
          tableBody.innerHTML = data.estudiantes.map(est => {
            console.log("Datos del estudiante:", est); // Para depuración
            // Asegurar que todos los campos necesarios existan y sean del tipo correcto
            const estudianteId = est.id || est.idEstudiante || '';
            const nombre = est.nombre || 'Sin nombre';
            
            // Manejar explícitamente valores nulos, indefinidos, o NaN en la nota
            let valorNota = '';
            if (est.nota !== null && est.nota !== undefined && !isNaN(est.nota)) {
              valorNota = est.nota;
            }
            
            // Manejar explícitamente valores nulos o indefinidos en comentarios
            const comentario = (est.comentario !== null && est.comentario !== undefined) 
                             ? est.comentario 
                             : '';
            
            return `
              <tr>
                <td>${nombre}</td>
                <td>
                  <input type="number" class="form-control form-control-sm" 
                         name="nota_${estudianteId}" 
                         value="${valorNota}"
                         min="0" max="100" step="0.1">
                </td>
                <td>
                  <input type="text" class="form-control form-control-sm"
                         name="comentario_${estudianteId}" 
                         value="${comentario}">
                </td>
              </tr>
            `;
          }).join('');
        } else {
          tableBody.innerHTML = '<tr><td colspan="3" class="text-center py-3">No hay estudiantes inscritos en este curso</td></tr>';
        }
      } catch (error) {
        console.error('Error al cargar estudiantes:', error);
        document.getElementById('gradeTableBody').innerHTML = 
          `<tr><td colspan="3" class="text-danger text-center py-3">
             Error al cargar datos: ${error.message}
           </td></tr>`;
      }
    });
  });

  // Botones para editar tarea
  document.querySelectorAll('.btn-edit').forEach(btn => {
    btn.addEventListener('click', async function() {
      const tareaId = this.dataset.tareaId;
      try {
        const response = await fetch(`${window.contextPath}/profesor/tareas_api/${tareaId}`);
        
        if (!response.ok) {
          const errorText = await response.text();
          throw new Error('Error cargando tarea: ' + errorText);
        }
        
        const tarea = await response.json();
        console.log("Datos de tarea recibidos:", tarea);
        
        document.getElementById('editTaskId').value = tarea.id_tarea;
        document.getElementById('editTaskTitle').value = tarea.titulo;
        document.getElementById('editTaskDesc').value = tarea.descripcion || '';
        document.getElementById('editTaskCourse').value = tarea.id_curso;
        
        // Formatear fecha para el input date
        if (tarea.fecha_entrega) {
          const fecha = new Date(tarea.fecha_entrega);
          document.getElementById('editTaskDueDate').value = 
            fecha.toISOString().split('T')[0];
        }
      } catch (error) {
        console.error('Error al cargar datos de la tarea:', error);
        alert('Error cargando datos de la tarea: ' + error.message);
      }
    });
  });

  // Botones para eliminar tarea
  document.querySelectorAll('.btn-delete').forEach(btn => {
    btn.addEventListener('click', function() {
      if (confirm('¿Está seguro de que desea eliminar esta tarea?')) {
        const tareaId = this.dataset.tareaId;
        
        // Crear y enviar una solicitud DELETE
        fetch(`${window.contextPath}/profesor/tareas/${tareaId}`, {
          method: 'DELETE',
          headers: {
            'Content-Type': 'application/json'
          }
        })
        .then(response => {
          if (!response.ok) {
            return response.text().then(text => {
              throw new Error(text || 'Error eliminando tarea');
            });
          }
          
          // Recargar la página si todo va bien
          window.location.reload();
          return response.text();
        })
        .then(data => {
          console.log('Éxito:', data);
        })
        .catch(error => {
          console.error('Error:', error);
          alert('Error eliminando la tarea: ' + error.message);
        });
      }
    });
  });
  // Configurar formulario de nueva tarea
  const newTaskForm = document.getElementById('newTaskForm');
  if (newTaskForm) {
    newTaskForm.action = `${window.contextPath}/profesor/tareas/crear`;
  }
    // Configurar formulario de editar tarea
  const editTaskForm = document.getElementById('editTaskForm');
  if (editTaskForm) {
    editTaskForm.action = `${window.contextPath}/profesor/tareas/editar`;
    
    // Añadir validación antes del envío del formulario
    editTaskForm.addEventListener('submit', function(event) {
      // Verificar que el ID de la tarea sea válido (número)
      const idTarea = document.getElementById('editTaskId').value;
      if (!idTarea || isNaN(parseInt(idTarea))) {
        event.preventDefault();
        alert('Error: ID de tarea inválido o no especificado');
        return false;
      }
      
      // Verificar que el ID del curso sea válido (número)
      const idCurso = document.getElementById('editTaskCourse').value;
      if (!idCurso || isNaN(parseInt(idCurso))) {
        event.preventDefault();
        alert('Error: Por favor seleccione un curso válido');
        return false;
      }
      
      // Verificar que el título no esté vacío
      const titulo = document.getElementById('editTaskTitle').value.trim();
      if (!titulo) {
        event.preventDefault();
        alert('Error: El título de la tarea no puede estar vacío');
        return false;
      }
      
      // Si todo está bien, permitir que el formulario se envíe
      return true;
    });
  }
  
  // Indicador de que el script se ha cargado correctamente
  window.tareasScriptLoaded = true;
  console.log("✅ Script tareas-profesor.js cargado correctamente");
});
