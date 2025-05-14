<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="true" %>
<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Gestión de Calificaciones</title>
  <!-- Bootstrap 5 & Bootstrap Icons -->
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
  <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons/font/bootstrap-icons.css" rel="stylesheet">
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
    </div>

    <!-- Tabla de calificaciones dinámica -->
    <div id="notasContainer" class="card mb-4 shadow-sm" style="display:none;">
      <div class="card-header bg-primary text-white">
        <i class="bi bi-table"></i> <span id="cursoTitulo"></span> – Calificaciones
      </div>
      <div class="card-body p-0">
        <div class="table-responsive">
          <table id="notasTable" class="table mb-0">
            <thead id="notasThead"></thead>
            <tbody id="notasTbody"></tbody>
            <tfoot id="notasTfoot"></tfoot>
          </table>
        </div>
      </div>
    </div>
  </div>

  <!-- Scripts -->
  <script>
    const apiBase = '<%= request.getContextPath() %>/profesor';
    async function fetchJson(url) {
      const resp = await fetch(url);
      if (!resp.ok) throw new Error(resp.statusText);
      return resp.json();
    }

    // Obtener parámetro de consulta id_curso
    const urlParams = new URLSearchParams(window.location.search);
    const selectedCurso = urlParams.get('id_curso');
    fetchJson(`${apiBase}/cursos/mis-cursos?id_profesor=1`)
      .then(cursos => {
        const sel = document.getElementById('cursoSelect');
        sel.innerHTML = '<option value="">-- Seleccione un curso --</option>';
        cursos.forEach(c => {
          const opt = new Option(c.nombre, c.id_curso);
          if (c.id_curso == selectedCurso) opt.selected = true;
          sel.add(opt);
        });
        // Si viene id_curso en la URL, disparar evento change
        if (selectedCurso) sel.dispatchEvent(new Event('change'));
      }).catch(console.error);

    document.getElementById('cursoSelect').addEventListener('change', async e => {
      const idCurso = e.target.value;
      if (!idCurso) return;
      try {
        // Nombre del curso
        const curso = await fetchJson(apiBase + '/cursos/' + idCurso);
        document.getElementById('cursoTitulo').textContent = curso.nombre;

        // Datos de tareas y estudiantes
        const tareas = await fetchJson(apiBase + '/tareas/por-curso?id_curso=' + idCurso);
        const estudiantes = await fetchJson(apiBase + '/curso-estudiantes?id_curso=' + idCurso);
        // Notas por tarea
        const notesData = {};
        for (const t of tareas) {
          notesData[t.id_tarea] = await fetchJson(apiBase + '/notas/por-tarea?id_tarea=' + t.id_tarea);
        }

        // Construir encabezado
        const thead1 = document.getElementById('notasThead');
        thead1.innerHTML = `
          <tr><th>Estudiante</th>${tareas.map(t => `<th>${t.titulo}</th>`).join('')}<th>Promedio</th></tr>
        `;

        // Construir cuerpo con promedio por estudiante
        const tbody = document.getElementById('notasTbody');
        tbody.innerHTML = estudiantes.map(est => {
          let sum=0, count=0;
          const cols = tareas.map(t => {
            const rec = notesData[t.id_tarea].find(n => n.id_estudiante==est.id_estudiante);
            if (rec) { sum+=rec.nota; count++; return `<td>${rec.nota.toFixed(1)}</td>`; }
            return '<td>-</td>';
          }).join('');
          const avgStu = count? (sum/count).toFixed(1) : '-';
          return `<tr><td>${est.nombre}</td>${cols}<td>${avgStu}</td></tr>`;
        }).join('');

        // Construir pie con promedio por actividad y total
        const tfoot = document.getElementById('notasTfoot');
        const avgTasks = tareas.map(t => {
          const list = notesData[t.id_tarea];
          const total = estudiantes.length;
          const sumNotes = list.reduce((a,n)=>a+Number(n.nota),0);
          return total? (sumNotes/total).toFixed(1) : '-';
        });
        const overall = avgTasks.filter(v=>v!=='-');
        const weighted = overall.length? (overall.reduce((a,v)=>a+Number(v),0)/overall.length).toFixed(1): '-';
        tfoot.innerHTML = `
          <tr><th>Promedio actividad</th>${avgTasks.map(v=>`<th>${v}</th>`).join('')}<th>${weighted}</th></tr>
        `;

        document.getElementById('notasContainer').style.display = '';
      } catch (err) {
        console.error(err);
      }
    });
  </script>

  <!-- Bootstrap Bundle JS -->
  <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
