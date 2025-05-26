<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%
    if (session == null || session.getAttribute("userRol") == null) {
        response.sendRedirect("login.jsp");
        return;
    }
%>
<c:set var="userRol" value="${sessionScope.userRol}" />
<c:set var="userNombre" value="${sessionScope.userNombre}" />
<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Menú Principal</title>
  <!-- Bootstrap 5 + FA6 -->
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
  <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
  <style>
    body {
      background-color: #eef4ff;
    }
    .sidebar {
      min-height: 100vh;
      background-color: transparent;
      padding: 2rem 1rem 4rem;
      display: flex;
      flex-direction: column;
      justify-content: space-between;
    }
    /* Card de cabecera */
    .sidebar-header {
      background: #fff;
      border-radius: 16px;
      padding: 1rem 1.25rem;
      box-shadow: 0 4px 12px rgba(0,0,0,0.05);
      margin-bottom: 2rem;
    }
    .sidebar-header h4 {
      margin: 0;
      color: #2e6ff8;
      font-size: 1.75rem;
      font-weight: 700;
      line-height: 1.2;
    }
    .sidebar-header p {
      margin: .5rem 0 0;
      color: #555;
      font-size: 1rem;
      display: flex;
      align-items: center;
      gap: .5rem;
    }

    /* Botones de navegación */
    .sidebar .nav-link {
      display: flex;
      align-items: center;
      gap: .75rem;
      background-color: #2e6ff8;
      color: #fff !important;
      border-radius: 12px;
      padding: .75rem 1rem;
      font-size: 1.05rem;
      font-weight: 500;
      margin-bottom: .75rem;
      transition: background .2s;
    }
    .sidebar .nav-link i {
      font-size: 1.25rem;
    }
    .sidebar .nav-link:hover,
    .sidebar .nav-link.active {
      background-color: #235ac4;
      text-decoration: none;
    }

    /* Botón Cerrar Sesión */
    .btn-logout {
      background: #e9f9f5;
      color: #dc3545;
      border: 2px solid #dc3545;
      border-radius: 12px;
      font-weight: 600;
      padding: .75rem;
      display: flex;
      align-items: center;
      justify-content: center;
      gap: .5rem;
      transition: background .2s, color .2s;
    }
    .btn-logout i {
      font-size: 1.25rem;
    }
    .btn-logout:hover {
      background-color: #dc3545;
      color: #fff;
      text-decoration: none;
    }

    /* iframe ocupa todo el espacio restante */
    iframe {
      width: 100%;
      height: calc(100vh - 2rem);
      border: none;
      background: #fff;
    }
  </style>
</head>
<body>
  <div class="container-fluid">
    <div class="row">
      <!-- Sidebar -->      <div class="col-md-3 col-lg-2 sidebar">
        <div>
          <div class="sidebar-header">
            <h4>Sistema<br>Académico</h4>
            <p><i class="fas fa-user"></i> ${userNombre}</p>
            <p><i class="fas fa-user-circle"></i>
              Rol: 
              <c:choose>
                <c:when test="${userRol == 3}">Administrador</c:when>
                <c:when test="${userRol == 2}">Profesor</c:when>
                <c:when test="${userRol == 1}">Estudiante</c:when>
                <c:otherwise>Desconocido</c:otherwise>
              </c:choose>
            </p>
          </div>          <nav class="nav flex-column">
            <a class="nav-link" href="${pageContext.request.contextPath}/perfil" target="contentFrame">
              <i class="fas fa-user-edit"></i> Mi Perfil
            </a>
            <c:forEach var="act" items="${actividades}">
              <c:choose>
                <c:when test="${userRol == 2 and fn:endsWith(act.enlace,'profesorcursos.jsp')}">
                  <a class="nav-link" href="${pageContext.request.contextPath}/profesor/cursos" target="contentFrame">
                    <i class="fas fa-circle"></i> ${act.nombre}
                  </a>
                </c:when>            
                <c:when test="${userRol == 2 and fn:endsWith(act.enlace,'profesortareas.jsp')}">
              <a class="nav-link" href="${pageContext.request.contextPath}/profesor/tareas" target="contentFrame">
                <i class="fas fa-circle"></i> ${act.nombre}
              </a>
            </c:when>                <c:when test="${userRol == 1 and fn:endsWith(act.enlace,'estudiantecursos.jsp')}">
                  <a class="nav-link" href="${pageContext.request.contextPath}/estudiante/cursos" target="contentFrame">
                    <i class="fas fa-circle"></i> ${act.nombre}
                  </a>
                </c:when>
                <c:when test="${userRol == 1 and fn:endsWith(act.enlace,'estudianteindex.jsp')}">
                  <a class="nav-link" href="${pageContext.request.contextPath}/estudiante/dashboard" target="contentFrame">
                    <i class="fas fa-circle"></i> ${act.nombre}
                  </a>
                </c:when>
                <c:when test="${userRol == 1 and fn:endsWith(act.enlace,'estudiantetareas.jsp')}">
                  <a class="nav-link" href="${pageContext.request.contextPath}/estudiante/tareas" target="contentFrame">
                    <i class="fas fa-circle"></i> ${act.nombre}
                  </a>
                </c:when>
                <c:otherwise>
                  <a class="nav-link" href="${act.enlace}" target="contentFrame">
                    <i class="fas fa-circle"></i> ${act.nombre}
                  </a>
                </c:otherwise>
              </c:choose>
            </c:forEach>
          </nav>
        </div>        <a href="javascript:void(0);" onclick="logoutSeguro()" class="btn btn-logout mt-4">
          <i class="fas fa-sign-out-alt"></i> Cerrar Sesión
        </a>
      </div>

      <!-- Contenido Principal -->
      <c:choose>
        <c:when test="${empty actividades}">
          <div class="col-md-9 col-lg-10 d-flex align-items-center justify-content-center">
            <h3>No tienes ninguna actividad asignada</h3>
          </div>
        </c:when>
        <c:otherwise>
          <div class="col-md-9 col-lg-10 p-0">
            <iframe name="contentFrame" src="${defaultEnlace}" title="Contenido del menú"></iframe>
          </div>
        </c:otherwise>
      </c:choose>
    </div>
  </div>

  <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
  <script>
    // ==================== BLOQUEO DE NAVEGACIÓN HACIA ATRÁS ====================
    
    // Variables de control
    let sessionActive = true;
    let logoutInProgress = false;
    
    // Función para agregar una entrada temporal al historial
    function preventBackNavigation() {
      // Agregamos una entrada al historial para interceptar el evento popstate
      history.pushState(null, null, location.href);
    }
    
    // Función para manejar el evento popstate (botón atrás)
    function handlePopState(event) {
      if (sessionActive && !logoutInProgress) {
        // Bloquear navegación hacia atrás
        event.preventDefault();
        event.stopPropagation();
        
        // Restaurar la entrada en el historial
        preventBackNavigation();
        
        // Mostrar mensaje al usuario
        if (confirm('¿Estás seguro de que quieres salir del sistema? Se cerrará tu sesión actual.')) {
          logoutSeguro();
        }
        
        return false;
      }
    }
    
    // Función para cerrar sesión de forma segura
    function logoutSeguro() {
      if (logoutInProgress) return; // Evitar múltiples llamadas
      
      logoutInProgress = true;
      sessionActive = false;
      
      // Limpiar el historial y redirigir usando location.replace
      // Esto evita que el usuario pueda volver atrás después del logout
      location.replace('${pageContext.request.contextPath}/login?action=logout');
    }
    
    // Función para verificar el estado de la sesión (opcional)
    function verificarSesion() {
      if (!sessionActive) return;
      
      fetch('${pageContext.request.contextPath}/sessioncheck', {
        method: 'GET',
        credentials: 'same-origin'
      })
      .then(response => {
        if (response.status === 401 || response.status === 403) {
          // Sesión expirada
          sessionActive = false;
          alert('Tu sesión ha expirado. Serás redirigido al login.');
          location.replace('${pageContext.request.contextPath}/login.jsp');
        }
      })
      .catch(error => {
        console.warn('Error verificando sesión:', error);
      });
    }
    
    // Manejar el evento beforeunload para advertir al usuario
    function handleBeforeUnload(event) {
      if (sessionActive && !logoutInProgress) {
        const message = '¿Estás seguro de que quieres salir? Se perderán los cambios no guardados.';
        event.returnValue = message;
        return message;
      }
    }
    
    // ==================== INICIALIZACIÓN ====================
    
    // Inicializar cuando el DOM esté listo
    document.addEventListener('DOMContentLoaded', function() {
      // Agregar entrada inicial al historial
      preventBackNavigation();
      
      // Configurar event listeners
      window.addEventListener('popstate', handlePopState);
      window.addEventListener('beforeunload', handleBeforeUnload);
      
      // Verificación periódica de sesión cada 5 minutos (opcional)
      setInterval(verificarSesion, 5 * 60 * 1000);
      
      console.log('Sistema de bloqueo de navegación activado');
    });
    
    // Limpiar event listeners cuando se vaya a cerrar la página
    window.addEventListener('beforeunload', function() {
      window.removeEventListener('popstate', handlePopState);
      window.removeEventListener('beforeunload', handleBeforeUnload);
    });
    
    // ==================== MANEJO DEL IFRAME ====================
    
    // Función para sincronizar el historial del iframe con la ventana principal
    function syncIframeHistory() {
      try {
        const iframe = document.querySelector('iframe[name="contentFrame"]');
        if (iframe && iframe.contentWindow) {
          // Agregar listener al iframe para detectar cambios de navegación
          iframe.contentWindow.addEventListener('beforeunload', function() {
            if (sessionActive && !logoutInProgress) {
              preventBackNavigation();
            }
          });
        }
      } catch (e) {
        // Ignorar errores de cross-origin
        console.log('No se puede acceder al contenido del iframe (cross-origin)');
      }
    }
    
    // Sincronizar cuando el iframe se cargue
    window.addEventListener('load', function() {
      setTimeout(syncIframeHistory, 1000);
    });
  </script>
</body>
</html>