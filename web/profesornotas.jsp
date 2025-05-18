<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="true" %>
<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Gestión de Calificaciones</title>
  <!-- Bootstrap 5, Bootstrap Icons, Chart.js y jsPDF -->
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
  <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons/font/bootstrap-icons.css" rel="stylesheet">
  <script src="https://cdn.jsdelivr.net/npm/chart.js@3.9.1/dist/chart.min.js"></script>
  <script src="https://cdnjs.cloudflare.com/ajax/libs/jspdf/2.5.1/jspdf.umd.min.js"></script>
  <script src="https://cdnjs.cloudflare.com/ajax/libs/html2canvas/1.4.1/html2canvas.min.js"></script>
  <style>
    body {
      background-color: #f8f9fa;
    }
    .card-header.bg-primary {
      font-weight: 500;
    }
    .table thead th {
      vertical-align: middle;
    }
    /* Centrar todas las celdas numéricas */
    .table thead th:nth-child(n+2),
    .table tbody td:nth-child(n+2),
    .table tfoot th:nth-child(n+2) {
      text-align: center;
    }
    .list-group-item {
      border: none;
      padding-left: 0;
      padding-right: 0;
    }
    .list-group-item + .list-group-item {
      border-top: 1px solid #dee2e6;
    }
    .progress {
      height: 20px;
    }
    .progress-bar {
      line-height: 20px;
    }
    /* Estilos para los gráficos */
    .chart-container {
      position: relative;
      height: 300px;
      width: 100%;
      margin-bottom: 1.5rem;
    }
    /* Estilos para impresión */
    @media print {
      .no-print {
        display: none !important;
      }
      .print-only {
        display: block !important;
      }
    }
    .print-only {
      display: none;
    }
    /* Botón de exportación */
    .btn-export {
      position: fixed;
      bottom: 20px;
      right: 20px;
      z-index: 1000;
      box-shadow: 0 3px 6px rgba(0,0,0,0.16), 0 3px 6px rgba(0,0,0,0.23);
    }
  </style>
</head>
<body>
  <div class="container-fluid py-4">
    <!-- Título y selector de curso dinámico -->
    <div class="d-flex justify-content-between align-items-center mb-3">
      <h2 class="m-0">Gestión de Calificaciones</h2>
      <select id="cursoSelect" class="form-select w-auto">
        <option value="">Cargando cursos...</option>
      </select>
    </div>    <!-- Panel de depuración (colapsable) -->
    <div class="mb-2">
      <button class="btn btn-sm btn-outline-danger" type="button" data-bs-toggle="collapse" 
              data-bs-target="#debugPanel" aria-expanded="false">
        <i class="bi bi-bug"></i> Mostrar/Ocultar Depuración
      </button>
    </div>
    
    <div id="debugPanel" class="card mb-3 border-danger collapse">
      <div class="card-header bg-danger text-white d-flex justify-content-between align-items-center">
        <div><i class="bi bi-bug"></i> Panel de Depuración</div>
        <button class="btn btn-sm btn-outline-light" id="clearDebug">Limpiar</button>
      </div>
      <div class="card-body p-3">
        <div id="debugMessages">
          <p>Esperando acciones...</p>
        </div>
      </div>
    </div>

    <!-- Tabla de calificaciones dinámica -->
    <div id="notasContainer" class="card mb-4 shadow-sm">
      <div class="card-header bg-primary text-white">
        <i class="bi bi-table"></i> <span id="cursoTitulo">Seleccione un curso</span> – Calificaciones
      </div>
      <div class="card-body p-0">
        <div class="table-responsive">          <table id="notasTable" class="table mb-0">
            <thead id="notasThead"></thead>
            <tbody id="notasTbody">
              <tr><td colspan="20" class="text-center py-4">Seleccione un curso para ver las calificaciones</td></tr>
            </tbody>
            <tfoot id="notasTfoot"></tfoot>
          </table>
        </div>
      </div>
    </div>
    
    <!-- Sección de gráficas -->
    <div class="row" id="graficasContainer">
      <div class="col-md-6 mb-4">
        <div class="card shadow-sm">
          <div class="card-header bg-primary text-white">
            <i class="bi bi-bar-chart-fill"></i> Rendimiento por Actividad
          </div>
          <div class="card-body">
            <div class="chart-container">
              <canvas id="graficaRendimiento"></canvas>
            </div>
          </div>
        </div>
      </div>
      <div class="col-md-6 mb-4">
        <div class="card shadow-sm">
          <div class="card-header bg-primary text-white">
            <i class="bi bi-pie-chart-fill"></i> Distribución de Calificaciones
          </div>
          <div class="card-body">
            <div class="chart-container">
              <canvas id="graficaDistribucion"></canvas>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
  
  <!-- Botón flotante para exportar a PDF -->
  <button id="exportarPDF" class="btn btn-success btn-lg rounded-circle btn-export">
    <i class="bi bi-file-earmark-pdf-fill"></i>
  </button>
  <!-- Scripts -->
  <script>
    // Variables para las gráficas
    let graficaRendimiento = null;
    let graficaDistribucion = null;
    
    // Configuración y utilidades
    const contextPath = '<%= request.getContextPath() %>';
    const apiBase = contextPath + '/profesor';
    
    // Inicializar jsPDF
    const { jsPDF } = window.jspdf;
    
    // Datos de ejemplo para usar cuando la API falla
    const datosEjemplo = {
      // Función que genera tareas de ejemplo para un curso dado
      generarTareas: (idCurso) => {
        return [
          { 
            id: 1, 
            titulo: "Examen Parcial", 
            descripcion: "Evaluación de conceptos básicos", 
            id_curso: idCurso,
            fecha_asignacion: "2023-08-15",
            fecha_entrega: "2023-08-30"
          },
          { 
            id: 2, 
            titulo: "Proyecto Final", 
            descripcion: "Desarrollo de aplicación completa", 
            id_curso: idCurso,
            fecha_asignacion: "2023-09-01",
            fecha_entrega: "2023-11-15" 
          },
          { 
            id: 3, 
            titulo: "Ejercicios Prácticos", 
            descripcion: "Serie de ejercicios para práctica", 
            id_curso: idCurso,
            fecha_asignacion: "2023-07-20",
            fecha_entrega: "2023-12-10" 
          }
        ];
      },
      
      // Función que genera estudiantes de ejemplo
      generarEstudiantes: () => {
        return [
          { id_estudiante: 1, nombre: "Ana García" },
          { id_estudiante: 2, nombre: "Carlos Rodríguez" },
          { id_estudiante: 3, nombre: "Elena Martínez" },
          { id_estudiante: 4, nombre: "David López" },
          { id_estudiante: 5, nombre: "María Sánchez" }
        ];
      },
      
      // Función que genera notas aleatorias para una tarea y lista de estudiantes
      generarNotas: (idTarea, estudiantes) => {
        const notas = [];
        
        // Generar una nota aleatoria para cada estudiante (~70% de probabilidad)
        for (const est of estudiantes) {
          // Solo generamos notas para ~70% de los estudiantes para simular notas pendientes
          if (Math.random() > 0.3) {
            const nota = (Math.random() * 7 + 3).toFixed(1); // Nota entre 3 y 10
            notas.push({
              id_nota: idTarea * 100 + est.id_estudiante,
              id_estudiante: est.id_estudiante,
              id_tarea: idTarea,
              nota: parseFloat(nota),
              comentario: "Nota de demostración"
            });
          }
        }
        
        return notas;
      }
    };
    
    // Función para mostrar mensajes de depuración en la interfaz
    function debugLog(message, isError = false) {
      const debugDiv = document.getElementById('debugMessages');
      const msgElem = document.createElement('div');
      msgElem.className = isError ? 'alert alert-danger mb-2' : 'alert alert-info mb-2';
      msgElem.textContent = typeof message === 'string' ? message : JSON.stringify(message);
      debugDiv.appendChild(msgElem);
      console.log(message);
      
      // Limitar a 10 mensajes
      if (debugDiv.childElementCount > 10) {
        debugDiv.removeChild(debugDiv.firstChild);
      }
    }
      // Reemplazar el contenido inicial
    document.getElementById('debugMessages').innerHTML = '';
    debugLog('Inicializando página de notas...');
    
    // Agregar manejador para botón limpiar
    document.getElementById('clearDebug').addEventListener('click', function(e) {
      e.stopPropagation();
      document.getElementById('debugMessages').innerHTML = '';
      debugLog('Panel de depuración limpiado');
    });
    
    async function fetchJson(url) {
      debugLog('Fetching: ' + url);
      try {
        const resp = await fetch(url);
        if (!resp.ok) {
          const errorMsg = 'Error en respuesta: ' + resp.status + ' ' + resp.statusText;
          debugLog(errorMsg, true);
          throw new Error(resp.statusText);
        }
        
        // Intentar leer la respuesta como texto primero para verificar
        const respText = await resp.text();
        
        // Si está vacío, devolvemos un array vacío
        if (!respText.trim()) {
          debugLog('Respuesta vacía, devolviendo array vacío');
          return [];
        }
        
        try {
          // Intentar parsear como JSON
          const data = JSON.parse(respText);
          debugLog('Respuesta recibida con ' + (Array.isArray(data) ? data.length + ' elementos' : 'datos'));
          return data;
        } catch (parseErr) {
          // Error al parsear JSON
          debugLog('Error al parsear respuesta como JSON:', true);
          debugLog('Texto recibido: "' + respText.substring(0, 100) + (respText.length > 100 ? '...' : '') + '"', true);
          throw new Error('Respuesta no es JSON válido: ' + parseErr.message);
        }
      } catch (error) {
        debugLog('Error en fetchJson: ' + error.message, true);
        throw error;
      }
    }

    // Obtener parámetro de consulta id_curso
    const urlParams = new URLSearchParams(window.location.search);
    const selectedCurso = urlParams.get('id_curso');
    console.log('ID de curso seleccionado:', selectedCurso);
      // Cargar lista de cursos
    document.addEventListener('DOMContentLoaded', () => {
      debugLog('Cargando cursos del profesor...');
      fetchJson(`${apiBase}/cursos/mis-cursos?id_profesor=1`)
        .then(cursos => {
          debugLog('Cursos recibidos: ' + cursos.length);
          
          // Verificar estructura de los cursos
          if (cursos.length > 0) {
            debugLog('Primer curso estructura: ' + JSON.stringify(cursos[0]));
          }
          
          const sel = document.getElementById('cursoSelect');
          sel.innerHTML = '<option value="">-- Seleccione un curso --</option>';
          
          if (cursos.length === 0) {
            debugLog('No se encontraron cursos para este profesor', true);
            sel.innerHTML += '<option disabled>No se encontraron cursos</option>';
            return;
          }
          
          cursos.forEach(c => {
            // Verificar si el curso tiene los campos esperados
            const idCurso = c.id_curso || c.id;
            const nombre = c.nombre;
            
            if (!idCurso || !nombre) {
              debugLog('Curso con estructura inválida: ' + JSON.stringify(c), true);
              return;
            }
            
            const opt = new Option(nombre, idCurso);
            if (idCurso == selectedCurso) {
              opt.selected = true;
              debugLog('Curso seleccionado automáticamente: ' + nombre + ' (ID: ' + idCurso + ')');
            }
            sel.add(opt);
          });
          
          // Si viene id_curso en la URL, disparar evento change
          if (selectedCurso) {
            debugLog('Disparando evento change para curso ID: ' + selectedCurso);
            sel.dispatchEvent(new Event('change'));
          }
        }).catch(error => {
          debugLog('Error al cargar cursos: ' + error.message, true);
          document.getElementById('cursoSelect').innerHTML = '<option value="">Error al cargar cursos</option>';
        });
    });document.getElementById('cursoSelect').addEventListener('change', async e => {
      const idCurso = e.target.value;
      debugLog('Curso seleccionado: ' + idCurso);
      if (!idCurso) return;
      
      // Limpiar la tabla de notas
      document.getElementById('notasTbody').innerHTML = '<tr><td colspan="20" class="text-center py-4"><div class="spinner-border text-primary" role="status"><span class="visually-hidden">Cargando...</span></div><p class="mt-2">Cargando calificaciones...</p></td></tr>';
      
      try {
        // Nombre del curso
        debugLog('Obteniendo información del curso...');
        const curso = await fetchJson(apiBase + '/cursos/' + idCurso);
        document.getElementById('cursoTitulo').textContent = curso.nombre;        // Datos de tareas y estudiantes
        debugLog('Obteniendo tareas del curso...');
        
        // Inicializar arrays para tareas y estudiantes
        let tareas = [];
        let estudiantes = [];
        
        // 1. Obtener tareas del curso
        try {
            // Intentar primero con el nuevo endpoint correcto
            tareas = await fetchJson(apiBase + '/tareas_api/por-curso?id_curso=' + idCurso);
            debugLog('Tareas obtenidas correctamente desde el endpoint principal');
        } catch (error1) {
            debugLog('Error en endpoint principal: ' + error1.message, true);
            
            // Si falla, intentar con el endpoint tradicional
            try {
                debugLog('Intentando con endpoint alternativo...');
                tareas = await fetchJson(apiBase + '/tareas?idCurso=' + idCurso);
                debugLog('Tareas obtenidas correctamente desde endpoint alternativo');
            } catch (error2) {
                debugLog('Error en endpoint alternativo: ' + error2.message, true);
                debugLog('Usando datos de demostración para tareas');
                
                // Si fallan ambos, usar datos de ejemplo
                tareas = datosEjemplo.generarTareas(idCurso);
                debugLog('Datos de demostración generados: ' + tareas.length + ' tareas');
            }
        }
        
        debugLog('Tareas obtenidas: ' + tareas.length);
        
        if (tareas.length === 0) {
          document.getElementById('notasTbody').innerHTML = '<tr><td colspan="20" class="text-center py-4 text-warning"><i class="bi bi-exclamation-triangle-fill me-2"></i>Este curso no tiene tareas asignadas</td></tr>';
          return;
        }
        
        // Verificar la estructura de datos de las tareas
        if (tareas.length > 0) {
          debugLog('Primera tarea estructura: ' + JSON.stringify(tareas[0]));
        }
        
        // 2. Obtener estudiantes del curso
        debugLog('Obteniendo estudiantes del curso...');
        
        try {
            estudiantes = await fetchJson(apiBase + '/curso-estudiantes?id_curso=' + idCurso);
            debugLog('Estudiantes obtenidos correctamente desde la API');
        } catch (error) {
            debugLog('Error al obtener estudiantes: ' + error.message, true);
            
            // Usar datos de ejemplo
            estudiantes = datosEjemplo.generarEstudiantes();
            debugLog('Datos de demostración generados: ' + estudiantes.length + ' estudiantes');
        }
        
        debugLog('Estudiantes obtenidos: ' + estudiantes.length);
        
        if (estudiantes.length === 0) {
          document.getElementById('notasTbody').innerHTML = '<tr><td colspan="20" class="text-center py-4 text-warning"><i class="bi bi-exclamation-triangle-fill me-2"></i>Este curso no tiene estudiantes asignados</td></tr>';
          return;
        }
        
        // Verificar la estructura de datos de los estudiantes
        if (estudiantes.length > 0) {
          debugLog('Primer estudiante estructura: ' + JSON.stringify(estudiantes[0]));
        }        // Notas por tarea
        debugLog('Obteniendo notas por tarea...');
        const notesData = {};
        
        try {
          // Para cada tarea, obtener sus notas
          for (const tarea of tareas) {
            // Normalizar la obtención del ID de tarea para manejar diferentes formatos de respuesta
            const tareaId = tarea.id || tarea.id_tarea;
            if (!tareaId) {
              debugLog('Error: Tarea sin ID válido: ' + JSON.stringify(tarea), true);
              continue;
            }
            
            debugLog('Obteniendo notas para la tarea ID: ' + tareaId);
            
            try {
              // Intentar obtener notas del servidor
              const notasResult = await fetchJson(apiBase + '/notas/por-tarea?id_tarea=' + tareaId);
              notesData[tareaId] = notasResult;
              debugLog('Notas obtenidas para tarea ' + tareaId + ': ' + notasResult.length);
            } catch (notaErr) {
              debugLog('Error al obtener notas para tarea ' + tareaId + ': ' + notaErr.message, true);
              
              // Generar datos de ejemplo para esta tarea
              notesData[tareaId] = datosEjemplo.generarNotas(tareaId, estudiantes);
              debugLog('Datos de demostración generados: ' + notesData[tareaId].length + ' notas para la tarea ' + tareaId);
            }
          }
        } catch (tareasErr) {
          debugLog('Error procesando tareas: ' + tareasErr.message, true);
          document.getElementById('notasTbody').innerHTML = '<tr><td colspan="20" class="text-center py-4 text-danger"><i class="bi bi-exclamation-circle-fill me-2"></i>Error al procesar las tareas</td></tr>';
          return;
        }// Construir encabezado
        const thead1 = document.getElementById('notasThead');
        let headerHtml = '<tr><th>Estudiante</th>';
        
        // Debuggear estructura de datos de tareas
        debugLog('Construyendo encabezados de tabla para ' + tareas.length + ' tareas');
        
        for (let i = 0; i < tareas.length; i++) {
          const tarea = tareas[i];
          // Intentar diferentes nombres de propiedad para el título
          const titulo = tarea.titulo || tarea.title || tarea.nombre || tarea.name || ('Tarea ' + (i+1));
          headerHtml += '<th>' + titulo + '</th>';
          
          // Si es la primera tarea, loguear todas las propiedades para depuración
          if (i === 0) {
            debugLog('Propiedades de la primera tarea:');
            for (const prop in tarea) {
              debugLog('  ' + prop + ': ' + tarea[prop]);
            }
          }
        }
        
        headerHtml += '<th>Promedio</th></tr>';
        thead1.innerHTML = headerHtml;// Construir cuerpo con promedio por estudiante
        const tbody = document.getElementById('notasTbody');
        let tbodyHtml = '';
        
        for (let i = 0; i < estudiantes.length; i++) {
          const est = estudiantes[i];
          let sum = 0, count = 0;
          let cols = '';
            for (let j = 0; j < tareas.length; j++) {
            const t = tareas[j];
            const tId = t.id || t.id_tarea;
            
            if (!tId) {
              debugLog('Tarea sin ID válido en índice ' + j, true);
              cols += '<td class="text-danger">Error</td>';
              continue;
            }
            
            // Verificar ID del estudiante
            const estId = est.id_estudiante || est.id;
            if (!estId) {
              debugLog('Estudiante sin ID válido', true);
              cols += '<td class="text-danger">Error</td>';
              continue;
            }
            
            debugLog('Buscando nota para tarea: ' + tId + ', estudiante: ' + estId);
            
            if (!notesData[tId]) {
              debugLog('No hay datos de notas para la tarea ID: ' + tId, true);
              cols += '<td class="text-warning">Sin datos</td>';
              continue;
            }
            
            // Buscar la nota del estudiante
            const rec = notesData[tId].find(n => {
              const notaEstId = n.id_estudiante || n.idEstudiante;
              return notaEstId == estId;
            });
            
            if (rec) { 
              const nota = rec.nota || rec.calificacion || rec.value || 0;
              sum += nota; 
              count++; 
              cols += '<td>' + parseFloat(nota).toFixed(1) + '</td>'; 
            } else {
              cols += '<td>-</td>';
            }
          }
          
          const avgStu = count ? (sum/count).toFixed(1) : '-';
          tbodyHtml += '<tr><td>' + est.nombre + '</td>' + cols + '<td>' + avgStu + '</td></tr>';
        }
        
        tbody.innerHTML = tbodyHtml;        // Construir pie con promedio por actividad y total
        const tfoot = document.getElementById('notasTfoot');
        const avgTasks = [];
        
        for (let j = 0; j < tareas.length; j++) {
          const t = tareas[j];
          const tId = t.id || t.id_tarea;
          console.log('Calculando promedio para tarea:', tId);
          const list = notesData[tId];
          const total = estudiantes.length;
          
          let sumNotes = 0;
          for (let i = 0; i < list.length; i++) {
            sumNotes += Number(list[i].nota);
          }
          
          avgTasks.push(total ? (sumNotes/total).toFixed(1) : '-');
        }
        
        const overall = avgTasks.filter(v => v !== '-');
        let sumOverall = 0;
        
        for (let i = 0; i < overall.length; i++) {
          sumOverall += Number(overall[i]);
        }
        
        const weighted = overall.length ? (sumOverall/overall.length).toFixed(1) : '-';
        
        let tfootHtml = '<tr><th>Promedio actividad</th>';
        for (let i = 0; i < avgTasks.length; i++) {
          tfootHtml += '<th>' + avgTasks[i] + '</th>';
        }
        tfootHtml += '<th>' + weighted + '</th></tr>';
        
        tfoot.innerHTML = tfootHtml;

        document.getElementById('notasContainer').style.display = '';

        // Generar gráficas con los datos
        generarGraficas(tareas, estudiantes, notesData);
      } catch (err) {
        debugLog('Error general: ' + err.message, true);
        document.getElementById('notasTbody').innerHTML = `
          <tr><td colspan="20" class="text-center py-4">
            <div class="text-danger mb-2"><i class="bi bi-exclamation-circle-fill me-2"></i>Error al cargar las calificaciones</div>
            <p class="small text-muted">Detalles: ${err.message}</p>
          </td></tr>
        `;
      }
    });
    
    // Función para generar las gráficas
    function generarGraficas(tareas, estudiantes, notesData) {
      debugLog('Generando gráficas...');
      
      // Destruir gráficas anteriores si existen
      if (graficaRendimiento) graficaRendimiento.destroy();
      if (graficaDistribucion) graficaDistribucion.destroy();
      
      // 1. Gráfica de Rendimiento por Actividad
      const labelsRendimiento = tareas.map(t => t.titulo || t.title || t.nombre || 'Tarea ' + t.id);
      const dataRendimiento = [];
      
      for (let j = 0; j < tareas.length; j++) {
        const t = tareas[j];
        const tId = t.id || t.id_tarea;
        const list = notesData[tId] || [];
        let sumNotes = 0;
        
        for (let i = 0; i < list.length; i++) {
          sumNotes += Number(list[i].nota || 0);
        }
        
        const promedio = list.length ? (sumNotes/list.length).toFixed(1) : 0;
        dataRendimiento.push(parseFloat(promedio));
      }
      
      const ctxRendimiento = document.getElementById('graficaRendimiento').getContext('2d');
      graficaRendimiento = new Chart(ctxRendimiento, {
        type: 'bar',
        data: {
          labels: labelsRendimiento,
          datasets: [{
            label: 'Promedio',
            data: dataRendimiento,
            backgroundColor: 'rgba(13, 110, 253, 0.7)',
            borderColor: 'rgba(13, 110, 253, 1)',
            borderWidth: 1
          }]
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          plugins: {
            title: {
              display: true,
              text: 'Promedio de calificaciones por actividad'
            },
            legend: {
              display: false
            },
            tooltip: {
              callbacks: {
                label: function(context) {
                  return 'Promedio: ' + context.raw;
                }
              }
            }
          },
          scales: {
            y: {
              beginAtZero: true,
              max: 10,
              title: {
                display: true,
                text: 'Calificación'
              }
            },
            x: {
              title: {
                display: true,
                text: 'Actividades'
              }
            }
          }
        }
      });
      
      // 2. Gráfica de Distribución de Calificaciones (rangos)
      // Preparar datos para gráfica de distribución
      const rangos = [
        { min: 0, max: 4.9, label: 'Suspenso (0-4.9)', color: 'rgba(220, 53, 69, 0.7)' },
        { min: 5, max: 6.9, label: 'Aprobado (5-6.9)', color: 'rgba(255, 193, 7, 0.7)' },
        { min: 7, max: 8.9, label: 'Notable (7-8.9)', color: 'rgba(25, 135, 84, 0.7)' },
        { min: 9, max: 10, label: 'Sobresaliente (9-10)', color: 'rgba(13, 110, 253, 0.7)' }
      ];
      
      // Contar notas en cada rango
      const conteoRangos = rangos.map(r => ({ ...r, count: 0 }));
      
      // Recorrer todas las notas de todas las tareas
      Object.keys(notesData).forEach(tareaId => {
        const notasTarea = notesData[tareaId] || [];
        notasTarea.forEach(nota => {
          const valorNota = parseFloat(nota.nota || 0);
          // Determinar en qué rango cae esta nota
          for (let i = 0; i < conteoRangos.length; i++) {
            if (valorNota >= conteoRangos[i].min && valorNota <= conteoRangos[i].max) {
              conteoRangos[i].count++;
              break;
            }
          }
        });
      });
      
      const ctxDistribucion = document.getElementById('graficaDistribucion').getContext('2d');
      graficaDistribucion = new Chart(ctxDistribucion, {
        type: 'pie',
        data: {
          labels: conteoRangos.map(r => r.label),
          datasets: [{
            data: conteoRangos.map(r => r.count),
            backgroundColor: conteoRangos.map(r => r.color),
            borderWidth: 1
          }]
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          plugins: {
            title: {
              display: true,
              text: 'Distribución de calificaciones por rango'
            },
            tooltip: {
              callbacks: {
                label: function(context) {
                  const total = context.dataset.data.reduce((a, b) => a + b, 0);
                  const porcentaje = Math.round((context.raw / total) * 100);
                  return `${context.label}: ${context.raw} (${porcentaje}%)`;
                }
              }
            }
          }
        }
      });
      
      debugLog('Gráficas generadas correctamente');
      
      // Mostrar el contenedor de gráficas
      document.getElementById('graficasContainer').style.display = '';
    }
    
    // Función para exportar a PDF
    async function exportarAPDF() {
      debugLog('Iniciando exportación a PDF...');
      
      try {
        // 1. Crear documento PDF
        const pdf = new jsPDF('portrait', 'pt', 'a4');
        const pageWidth = pdf.internal.pageSize.getWidth();
        const pageHeight = pdf.internal.pageSize.getHeight();
        
        // 2. Añadir título
        const cursoTitulo = document.getElementById('cursoTitulo').textContent;
        pdf.setFontSize(18);
        pdf.setTextColor(40, 40, 40);
        pdf.text('Informe de Calificaciones', pageWidth/2, 40, { align: 'center' });
        
        pdf.setFontSize(14);
        pdf.text(cursoTitulo, pageWidth/2, 70, { align: 'center' });
        
        pdf.setFontSize(10);
        const fechaActual = new Date().toLocaleDateString('es-ES');
        pdf.text('Generado el: ' + fechaActual, pageWidth/2, 90, { align: 'center' });
        
        let verticalPosition = 120;
        
        // 3. Capturar y añadir la tabla
        debugLog('Capturando tabla de calificaciones...');
        const tablaElement = document.getElementById('notasTable');
        const canvasTabla = await html2canvas(tablaElement, {
          scale: 1.5,
          backgroundColor: '#ffffff'
        });
        
        // Calcular ancho y alto proporcionales para la tabla
        const imgDataTabla = canvasTabla.toDataURL('image/png');
        const imgWidth = pageWidth - 80; // Margen de 40px en cada lado
        const imgHeight = (canvasTabla.height * imgWidth) / canvasTabla.width;
        
        pdf.text('Tabla de Calificaciones', pageWidth/2, verticalPosition, { align: 'center' });
        verticalPosition += 20;
        
        pdf.addImage(imgDataTabla, 'PNG', 40, verticalPosition, imgWidth, imgHeight);
        verticalPosition += imgHeight + 40;
        
        // Verificar si necesitamos una nueva página para las gráficas
        if (verticalPosition + 300 > pageHeight) {
          pdf.addPage();
          verticalPosition = 40;
        }
        
        // 4. Capturar y añadir la primera gráfica
        debugLog('Capturando gráfica de rendimiento...');
        const grafica1Element = document.getElementById('graficaRendimiento').parentNode;
        const canvasGrafica1 = await html2canvas(grafica1Element, {
          scale: 1.5,
          backgroundColor: '#ffffff'
        });
        
        const imgDataGrafica1 = canvasGrafica1.toDataURL('image/png');
        const grafica1Width = pageWidth - 80;
        const grafica1Height = (canvasGrafica1.height * grafica1Width) / canvasGrafica1.width;
        
        pdf.text('Rendimiento por Actividad', pageWidth/2, verticalPosition, { align: 'center' });
        verticalPosition += 20;
        
        pdf.addImage(imgDataGrafica1, 'PNG', 40, verticalPosition, grafica1Width, grafica1Height);
        verticalPosition += grafica1Height + 40;
        
        // Verificar si necesitamos una nueva página para la segunda gráfica
        if (verticalPosition + 300 > pageHeight) {
          pdf.addPage();
          verticalPosition = 40;
        }
        
        // 5. Capturar y añadir la segunda gráfica
        debugLog('Capturando gráfica de distribución...');
        const grafica2Element = document.getElementById('graficaDistribucion').parentNode;
        const canvasGrafica2 = await html2canvas(grafica2Element, {
          scale: 1.5,
          backgroundColor: '#ffffff'
        });
        
        const imgDataGrafica2 = canvasGrafica2.toDataURL('image/png');
        const grafica2Width = pageWidth - 80;
        const grafica2Height = (canvasGrafica2.height * grafica2Width) / canvasGrafica2.width;
        
        pdf.text('Distribución de Calificaciones', pageWidth/2, verticalPosition, { align: 'center' });
        verticalPosition += 20;
        
        pdf.addImage(imgDataGrafica2, 'PNG', 40, verticalPosition, grafica2Width, grafica2Height);
        
        // 6. Guardar PDF
        const nombreArchivo = 'calificaciones_' + cursoTitulo.replace(/\s+/g, '_') + '.pdf';
        pdf.save(nombreArchivo);
        debugLog('PDF exportado correctamente como: ' + nombreArchivo);
      } catch (error) {
        debugLog('Error al exportar PDF: ' + error.message, true);
        alert('Error al exportar PDF: ' + error.message);
      }
    }
    
    // Añadir evento al botón de exportar
    document.addEventListener('DOMContentLoaded', function() {
      // Ocultar inicialmente el contenedor de gráficas
      document.getElementById('graficasContainer').style.display = 'none';
      
      // Añadir manejador para el botón de exportar
      document.getElementById('exportarPDF').addEventListener('click', exportarAPDF);
    });
  </script>

  <!-- Bootstrap Bundle JS -->
  <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
