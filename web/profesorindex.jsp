<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1" />
  <title>Dashboard Profesor</title>

  <!-- Bootstrap 5 + Bootstrap Icons -->
  <link
    href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css"
    rel="stylesheet"
  />
  <link
    href="https://cdn.jsdelivr.net/npm/bootstrap-icons/font/bootstrap-icons.css"
    rel="stylesheet"
  />

  <style>
    body {
      background-color: #f8f9fa;
    }
    .dashboard-card {
      background-color: #fff;
      border-radius: 12px;
      padding: 1.5rem;
      box-shadow: 0 0.5rem 1rem rgba(0, 0, 0, 0.08);
      transition: transform 0.3s ease, box-shadow 0.3s ease;
    }
    .dashboard-card:hover {
      transform: translateY(-5px);
      box-shadow: 0 0.75rem 1.5rem rgba(0, 0, 0, 0.12);
    }
    .dashboard-card .card-title {
      color: #6c757d;
      font-size: 0.9rem;
      font-weight: 500;
    }
    .dashboard-card .card-value {
      color: #212529;
      font-size: 2rem;
      font-weight: 700;
    }
    .dashboard-card .card-icon {
      font-size: 2.5rem;
      color: #0d6efd;
      opacity: 0.8;
    }
    .table-section {
      margin-top: 2rem;
    }
    .table-section .table thead th {
      background-color: #0d6efd;
      color: #fff;
    }
    .badge-circle {
      border-radius: 50%;
      padding: 0.5em 0.75em;
    }
  </style>
</head>

<body>
  <div class="container-fluid py-4">
    <!-- Métricas superiores -->
    <div class="row g-4 mb-4">
      <div class="col-md-3">
        <div class="dashboard-card d-flex justify-content-between align-items-center">
          <div>
            <div class="card-title">Mis Cursos</div>
            <div id="misCursosValue" class="card-value">3</div>
          </div>
          <i class="bi bi-journal-text card-icon"></i>
        </div>
      </div>
      <div class="col-md-3">
        <div class="dashboard-card d-flex justify-content-between align-items-center">
          <div>
            <div class="card-title">Estudiantes</div>
            <div id="estudiantesValue" class="card-value">45</div>
          </div>
          <i class="bi bi-people card-icon"></i>
        </div>
      </div>
      <div class="col-md-3">
        <div class="dashboard-card d-flex justify-content-between align-items-center">
          <div>
            <div class="card-title">Tareas Pendientes</div>
            <div id="tareasPendientesValue" class="card-value">12</div>
          </div>
          <i class="bi bi-list-task card-icon"></i>
        </div>
      </div>
      <div class="col-md-3">
        <div class="dashboard-card d-flex justify-content-between align-items-center">
          <div>
            <div class="card-title">Promedio General</div>
            <div id="promedioGeneralValue" class="card-value">7.8</div>
          </div>
          <i class="bi bi-star card-icon"></i>
        </div>
      </div>
    </div>

    <!-- Tablas secundarias -->
    <div class="row table-section">
      <!-- Cursos Activos -->
      <div class="col-lg-7 mb-4">
        <div class="table-responsive">
          <table class="table align-middle mb-0">
            <thead>
              <tr>
                <th>Mis Cursos Activos</th>
                <th>Estudiantes</th>
                <th>Progreso</th>
                <th>Acciones</th>
              </tr>
            </thead>
            <tbody id="cursosActivosBody">
              <!-- Filas de cursos activos se generarán dinámicamente -->
            </tbody>
          </table>
        </div>
      </div>

      <!-- Tareas pendientes por calificar -->
      <div class="col-lg-5 mb-4">
        <div class="table-responsive">
          <table class="table align-middle mb-0">
            <thead>
              <tr>
                <th>Tareas pendientes por calificar</th>
                <th></th>
              </tr>
            </thead>
            <tbody id="tareasPendientesBody">
              <!-- Filas de tareas pendientes se generarán dinámicamente -->
            </tbody>
          </table>
        </div>        <div class="p-3 bg-white rounded-bottom shadow-sm">
          <a href="profesortareas.jsp" class="btn btn-primary">Ver todas las tareas</a>
        </div>
      </div>
    </div>
  </div>
  <!-- Bootstrap Bundle JS -->
  <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>  
  <script>
    const apiBase = '<%= request.getContextPath() %>/profesor';
    async function fetchJson(url) {
      const resp = await fetch(url);
      if (!resp.ok) throw new Error(resp.statusText);
      return resp.json();
    }
    document.addEventListener('DOMContentLoaded', async () => {
      const idProfesor = 1; // TODO: obtener del session
      try {
        console.log("Cargando datos del dashboard...");
        
        // Métricas
        const m = await fetchJson(apiBase + '/dashboard?id_profesor=' + idProfesor);
        console.log("Datos principales recibidos:", m);
        document.getElementById('misCursosValue').textContent = m.cursosCount;
        document.getElementById('estudiantesValue').textContent = m.estudiantesCount;
        document.getElementById('tareasPendientesValue').textContent = m.tareasPendientes;
        document.getElementById('promedioGeneralValue').textContent = m.average.toFixed(1);
        
        // Cursos Activos
        const cursos = await fetchJson(apiBase + '/dashboard/cursos?id_profesor=' + idProfesor);
        console.log("Datos de cursos recibidos:", cursos);
          document.getElementById('cursosActivosBody').innerHTML = cursos.map(c => {
            console.log("Curso individual:", c);
            // Obtener el nombre del curso, manejando inconsistencias directamente en la línea
            let nombreCurso = 'Sin nombre';
            if (c.nombre) nombreCurso = c.nombre;
            else if (c.nomBbre) nombreCurso = c.nomBbre;
            else if (c.nomBre) nombreCurso = c.nomBre;
            else if (c.nombreCurso) nombreCurso = c.nombreCurso;
            
            // Manejar valores numéricos correctamente
            const estudiantes = c.estudiantes !== undefined ? c.estudiantes : 0;
            const progress = c.progress !== undefined ? c.progress : 0;
            const progressClass = c.progressClass || 'bg-primary';
            const cursoId = c.id !== undefined ? c.id : 0;
            
            return `
              <tr>
                <td>\${nombreCurso}</td>
                <td>\${estudiantes}</td>
                <td style="min-width:150px;">
                  <div class="progress" style="height:10px;">
                    <div class="progress-bar \${progressClass}" role="progressbar"
                         style="width:\${progress}%" aria-valuenow="\${progress}" aria-valuemin="0" aria-valuemax="100"></div>
                  </div>
                </td>
                <td><a href="<%= request.getContextPath() %>/profesornotas.jsp?id_curso=\${cursoId}" class="btn btn-sm btn-primary">Ver curso</a></td>
              </tr>
            `;
        }).join('');        // Tareas pendientes
        const tareas = await fetchJson(apiBase + '/dashboard/tareas-pendientes?id_profesor=' + idProfesor);
        console.log("Datos de tareas pendientes recibidos:", tareas);
        
        // Comprobar si hay tareas
        if (tareas && tareas.length > 0) {
          let tareasHtml = '';
          for (let i = 0; i < tareas.length; i++) {
            const t = tareas[i];
            const titulo = t.titulo ? t.titulo : 'Sin título';
            const count = t.count !== undefined ? t.count : 0;
            const tareaId = t.id !== undefined ? t.id : 0; // Definir tareaId explícitamente
            // Determinar el color de la insignia según el número de pendientes
            let badgeClass = 'bg-primary';
            if (count > 5) badgeClass = 'bg-danger';
            else if (count > 2) badgeClass = 'bg-warning';
            
            tareasHtml += `
              <tr>
                <td>
                  <a href="\${apiBase}/tareas/\${tareaId}" class="text-decoration-none text-dark">
                    \${titulo}
                  </a>
                </td>
                <td class="text-end">
                  <span class="badge \${badgeClass} text-white badge-circle">\${count}</span>
                </td>
              </tr>
            `;
          }
          document.getElementById('tareasPendientesBody').innerHTML = tareasHtml;
        } else {
          document.getElementById('tareasPendientesBody').innerHTML = 
            '<tr><td colspan="2" class="text-center">No hay tareas pendientes por calificar</td></tr>';
        }
      } catch (e) {
        console.error('Error cargando dashboard:', e);
        alert('Error al cargar el dashboard: ' + e.message);
      }
    });
  </script>
</body>
</html>
