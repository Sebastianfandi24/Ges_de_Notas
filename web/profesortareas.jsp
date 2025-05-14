<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="true" %>
<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Gestión de Tareas</title>

  <!-- Bootstrap 5 + Bootstrap Icons -->
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
  <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons/font/bootstrap-icons.css" rel="stylesheet">

  <style>
    body {
      background-color: #f8f9fa;
    }
    .table-wrapper {
      border: 1px solid #dee2e6;
      border-radius: .375rem;
      background-color: #fff;
      overflow: hidden;
    }
    .table-wrapper .table {
      margin-bottom: 0;
    }
    .badge {
      font-size: .85rem;
      padding: .4em .75em;
    }
  </style>
</head>
<body>
  <div class="container-fluid py-4">
    <!-- Encabezado con filtros y botón Nueva tarea -->
    <div class="d-flex justify-content-between align-items-center mb-4">
      <h2 class="m-0">Gestión de Tareas</h2>
      <div>
        <button id="btnNewTask" class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#newTaskModal">
          <i class="bi bi-plus-circle me-1"></i> Nueva tarea
        </button>
      </div>
    </div>

    <!-- Tabla principal -->
    <div class="table-responsive table-wrapper">
      <table class="table align-middle mb-0">
        <thead>
          <tr>
            <th>Título</th>
            <th>Curso</th>
            <th>Fecha entrega</th>
            <th>Entregas</th>
            <th>Estado</th>
            <th>Acciones</th>
          </tr>
        </thead>
        <tbody id="tareasTbody">
          <!-- Filas de tareas se generarán dinámicamente -->
        </tbody>
      </table>
    </div>
  </div>

  <!-- Modal Crear Tarea -->
  <div class="modal fade" id="newTaskModal" tabindex="-1" aria-labelledby="newTaskModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-lg">
      <div class="modal-content">
        <div class="modal-header">
          <h5 class="modal-title" id="newTaskModalLabel">Crear Nueva Tarea</h5>
          <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Cerrar"></button>
        </div>
        <div class="modal-body">
          <form id="newTaskForm">
            <div class="mb-3">
              <label for="taskTitle" class="form-label">Título</label>
              <input type="text" class="form-control" id="taskTitle" name="titulo" required>
            </div>
            <div class="mb-3">
              <label for="taskDueDate" class="form-label">Fecha de entrega</label>
              <input type="date" class="form-control" id="taskDueDate" name="fechaEntrega" required>
            </div>
            <div class="mb-3">
              <label for="taskCourse" class="form-label">Curso</label>
              <select class="form-select" id="taskCourse" name="cursoId" required>
                <option value="">Seleccione un curso...</option>
              </select>
            </div>
            <div id="studentsTableContainer" class="table-responsive" style="display:none;">
              <table class="table table-sm">
                <thead>
                  <tr><th>Estudiante</th><th>Nota inicial</th><th>Comentario</th></tr>
                </thead>
                <tbody id="studentsTableBody"></tbody>
              </table>
            </div>
          </form>
        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancelar</button>
          <button type="button" id="saveTaskBtn" class="btn btn-primary">Guardar tarea</button>
        </div>
      </div>
    </div>
  </div>

  <!-- Modal Calificar Entregas -->
  <div class="modal fade" id="gradeModal" tabindex="-1" aria-labelledby="gradeModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-lg">
      <div class="modal-content">
        <div class="modal-header">
          <h5 class="modal-title" id="gradeModalLabel">Calificar entregas - <span id="gradeTareaTitle"></span></h5>
          <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Cerrar"></button>
        </div>
        <div class="modal-body">
          <form id="gradeForm">
            <input type="hidden" id="gradeTareaId" name="id_tarea" />
            <div class="table-responsive">
              <table class="table table-sm">
                <thead>
                  <tr><th>Estudiante</th><th>Nota</th><th>Comentario</th></tr>
                </thead>
                <tbody id="gradeTableBody"></tbody>
              </table>
            </div>
          </form>
        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancelar</button>
          <button type="button" id="saveGradesBtn" class="btn btn-primary">Guardar calificaciones</button>
        </div>
      </div>
    </div>
  </div>

  <!-- Modal Editar Tarea -->
  <div class="modal fade" id="editTaskModal" tabindex="-1" aria-labelledby="editTaskModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-lg">
      <div class="modal-content">
        <div class="modal-header">
          <h5 class="modal-title" id="editTaskModalLabel">Editar Tarea</h5>
          <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
        </div>
        <div class="modal-body">
          <form id="editTaskForm">
            <input type="hidden" id="editTaskId" name="id_tarea">
            <div class="mb-3">
              <label for="editTaskTitle" class="form-label">Título</label>
              <input type="text" id="editTaskTitle" name="titulo" class="form-control">
            </div>
            <div class="mb-3">
              <label for="editTaskDesc" class="form-label">Descripción</label>
              <textarea id="editTaskDesc" name="descripcion" class="form-control" rows="3"></textarea>
            </div>
            <div class="mb-3">
              <label for="editTaskDueDate" class="form-label">Fecha de entrega</label>
              <input type="date" id="editTaskDueDate" name="fecha_entrega" class="form-control">
            </div>
            <div class="mb-3">
              <label for="editTaskCourse" class="form-label">Curso</label>
              <select id="editTaskCourse" name="cursoId" class="form-select"></select>
            </div>
          </form>
        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancelar</button>
          <button type="button" id="saveEditTaskBtn" class="btn btn-primary">Guardar cambios</button>
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
      // 1) Cargar tareas
      const tbody = document.getElementById('tareasTbody');
      const idProfesor = 1; // TODO: Obtener de sesión
      try {
        const cursos = await fetchJson(apiBase + '/cursos/mis-cursos?id_profesor=' + idProfesor);
        for (const curso of cursos) {
          const tareas = await fetchJson(apiBase + '/tareas/por-curso?id_curso=' + curso.id_curso);
          for (const t of tareas) {
            const notas = await fetchJson(apiBase + '/notas/por-tarea?id_tarea=' + t.id_tarea);
            const estado = new Date(t.fecha_entrega) > new Date() ? 'Activa' : (notas.length < curso.total_estudiantes ? 'Por calificar' : 'Calificada');
            const badgeClass = estado==='Activa'? 'bg-info': estado==='Por calificar'? 'bg-warning': 'bg-success';
            const row = `
              <tr>
                <td>${t.titulo}</td>
                <td>${curso.nombre}</td>
                <td>${new Date(t.fecha_entrega).toLocaleDateString()}</td>
                <td>${notas.length}/${curso.total_estudiantes}</td>
                <td><span class="badge ${badgeClass} text-white">${estado}</span></td>
                <td>
                  <button class="btn btn-outline-primary btn-sm btn-grade" data-id="${t.id_tarea}">Calificar entregas</button>
                  <button class="btn btn-outline-warning btn-sm btn-edit" data-id="${t.id_tarea}">Editar tarea</button>
                </td>
              </tr>`;
            tbody.insertAdjacentHTML('beforeend', row);
          }
        }
      } catch (e) {
        console.error('Error cargando tareas:', e);
        tbody.innerHTML = '<tr><td colspan="6" class="text-center text-danger">No se pudieron cargar las tareas</td></tr>';
      }

      // 2) Inicializar modal de nueva tarea
      const btnNew = document.getElementById('btnNewTask');
      const selectCourse = document.getElementById('taskCourse');
      const tableContainer = document.getElementById('studentsTableContainer');
      const tableBody = document.getElementById('studentsTableBody');
      const saveBtn = document.getElementById('saveTaskBtn');
      const form = document.getElementById('newTaskForm');

      btnNew.addEventListener('click', async () => {
        const cursos = await fetchJson(apiBase + '/cursos/mis-cursos?id_profesor=' + idProfesor);
        selectCourse.innerHTML = '<option value="">Seleccione un curso...</option>';
        cursos.forEach(c => {
          const opt = document.createElement('option');
          opt.value = c.id_curso;
          opt.textContent = c.nombre;
          selectCourse.append(opt);
        });
        tableContainer.style.display = 'none';
      });

      selectCourse.addEventListener('change', async () => {
        const idCurso = selectCourse.value;
        if (!idCurso) { tableContainer.style.display = 'none'; return; }
        const estudiantes = await fetchJson(apiBase + '/curso-estudiantes?id_curso=' + idCurso);
        tableBody.innerHTML = '';
        estudiantes.forEach(e => {
          const tr = document.createElement('tr');
          tr.innerHTML = `
            <td>${e.nombre}</td>
            <td><input type="number" min="0" max="10" step="0.1" class="form-control form-control-sm" name="nota_${e.id_estudiante}"></td>
            <td><input type="text" class="form-control form-control-sm" name="comentario_${e.id_estudiante}" placeholder="Comentario"></td>
          `;
          tableBody.append(tr);
        });
        tableContainer.style.display = 'block';
      });

      saveBtn.addEventListener('click', async () => {
        const data = new FormData(form);
        console.group('Crear Tarea - Request');
        for (const [key, value] of data.entries()) console.log(key, value);
        console.groupEnd();
        const resp = await fetch(apiBase + '/tareas/crear', { method: 'POST', body: data });
        console.log('Crear Tarea - Status:', resp.status);
        const resultCreate = await resp.json().catch(() => null);
        console.log('Crear Tarea - Response:', resultCreate);
        if (resp.ok) location.reload(); else alert('Error al crear tarea');
      });
    });

    const idProfesor = 1; // TODO: obtener del session
    document.querySelector('.table-wrapper table tbody').addEventListener('click', async e => {
      const btn = e.target.closest('button');
      if (!btn) return;
      const id = btn.dataset.id;
      if (btn.classList.contains('btn-grade')) {
        // Mostrar modal Calificar
        document.getElementById('gradeTareaId').value = id;
        document.getElementById('gradeTareaTitle').textContent = btn.closest('tr').querySelector('td').textContent;
        // Cargar datos para calificar
        const tarea = await fetchJson(apiBase + '/tareas/' + id);
        const cursoId = tarea.id_curso;
        const estudiantes = await fetchJson(apiBase + '/curso-estudiantes?id_curso=' + cursoId);
        const notas = await fetchJson(apiBase + '/notas/por-tarea?id_tarea=' + id);
        const gtbody = document.getElementById('gradeTableBody');
        gtbody.innerHTML = '';
        estudiantes.forEach(est => {
          const notaObj = notas.find(n => n.id_estudiante === est.id_estudiante) || {};
          const val = notaObj.nota || '';
          const com = notaObj.comentario || '';
          const idNota = notaObj.id_nota || '';
          const tr = document.createElement('tr');
          tr.innerHTML = `
            <td>${est.nombre}</td>
            <td>
              <input type="hidden" name="id_nota_${est.id_estudiante}" value="${idNota}">
              <input type="number" class="form-control" name="nota_${est.id_estudiante}" value="${val}" min="0" max="10" step="0.1">
            </td>
            <td><input type="text" class="form-control" name="comentario_${est.id_estudiante}" value="${com}"></td>
          `;
          gtbody.appendChild(tr);
        });
        new bootstrap.Modal(document.getElementById('gradeModal')).show();
      } else if (btn.classList.contains('btn-edit')) {
        // Mostrar modal Editar
        document.getElementById('editTaskId').value = id;
        // Cargar datos de tarea y cursos
        const tarea = await fetchJson(apiBase + '/tareas/' + id);
        document.getElementById('editTaskTitle').value = tarea.titulo;
        document.getElementById('editTaskDesc').value = tarea.descripcion || '';
        document.getElementById('editTaskDueDate').value = tarea.fecha_entrega.split('T')[0];
        // Cargar opciones de cursos
        const cursos = await fetchJson(apiBase + '/cursos/mis-cursos?id_profesor=' + idProfesor);
        const sel = document.getElementById('editTaskCourse'); sel.innerHTML = '';
        cursos.forEach(c => sel.add(new Option(c.nombre, c.id_curso)));
        sel.value = tarea.id_curso;
        new bootstrap.Modal(document.getElementById('editTaskModal')).show();
      }
    });

    document.getElementById('saveEditTaskBtn').addEventListener('click', async () => {
      const formE = document.getElementById('editTaskForm');
      const dataE = new FormData(formE);
      console.group('Editar Tarea - Request');
      for (const [k, v] of dataE.entries()) console.log(k, v);
      console.groupEnd();
      const id = dataE.get('id_tarea');
      // Enviar edición mediante URLSearchParams
      const urlEncE = new URLSearchParams();
      for (const [k, v] of dataE.entries()) urlEncE.append(k, v);
      const respE = await fetch(apiBase + '/tareas/actualizar', {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8' },
        body: urlEncE.toString()
      });
      console.log('Editar Tarea - Status:', respE.status);
      const rawE = await respE.text();
      console.log('Editar Tarea - Raw Response:', rawE);
      let resultEdit;
      try { resultEdit = JSON.parse(rawE); }
      catch (e) { console.error('Error parsing edit JSON:', e); resultEdit = null; }
      console.log('Editar Tarea - Parsed:', resultEdit);
      if (respE.ok) location.reload(); else alert('Error al actualizar tarea');
    });
  
    document.getElementById('saveGradesBtn').addEventListener('click', async () => {
      const formG = document.getElementById('gradeForm');
      const dataG = new FormData(formG);
      console.group('Guardar Calificaciones - Request');
      for (const [k, v] of dataG.entries()) console.log(k, v);
      console.groupEnd();
      // Enviar como urlencoded para que el servlet pueda parsear
      const urlEncoded = new URLSearchParams();
      for (const [k, v] of dataG.entries()) urlEncoded.append(k, v);
      const respG = await fetch(apiBase + '/tareas/calificar', {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8' },
        body: urlEncoded.toString()
      });
      console.log('Guardar Calificaciones - Status:', respG.status);
      const rawG = await respG.text();
      console.log('Guardar Calificaciones - Raw Response:', rawG);
      let resultG;
      try {
        resultG = JSON.parse(rawG);
      } catch(e) {
        console.error('Error parsing JSON:', e);
        resultG = null;
      }
      console.log('Guardar Calificaciones - Parsed:', resultG);
      if (respG.ok) { alert('Calificaciones guardadas'); location.reload(); }
      else alert('Error al guardar calificaciones');
    });
  </script>
</body>
</html>
