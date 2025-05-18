<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Diagnóstico de Tareas</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
    <div class="container my-4">
        <h2 class="mb-4">Diagnóstico de Tareas</h2>
        
        <div class="card mb-4">
            <div class="card-header">Información de la Sesión</div>
            <div class="card-body">
                <p><strong>ID de Usuario:</strong> ${sessionScope.userId != null ? sessionScope.userId : 'No disponible'}</p>
                <p><strong>Rol de Usuario:</strong> ${sessionScope.userRol != null ? sessionScope.userRol : 'No disponible'}</p>
                <p><strong>Nombre de Usuario:</strong> ${sessionScope.userNombre != null ? sessionScope.userNombre : 'No disponible'}</p>
            </div>
        </div>
        
        <div class="card mb-4">
            <div class="card-header">Información de Cursos</div>
            <div class="card-body">
                <h5>Cursos en el Request:</h5>
                <c:choose>
                    <c:when test="${empty cursos}">
                        <div class="alert alert-warning">No hay cursos disponibles en el request</div>
                    </c:when>
                    <c:otherwise>
                        <table class="table table-striped">
                            <thead>
                                <tr>
                                    <th>ID</th>
                                    <th>Nombre</th>
                                    <th>Código</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="curso" items="${cursos}">
                                    <tr>
                                        <td>${curso.id}</td>
                                        <td>${curso.nombre}</td>
                                        <td>${curso.codigo}</td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
        
        <div class="card mb-4">
            <div class="card-header">Información de Tareas</div>
            <div class="card-body">
                <h5>Tareas en el Request:</h5>
                <c:choose>
                    <c:when test="${empty tareas}">
                        <div class="alert alert-warning">No hay tareas disponibles en el request</div>
                    </c:when>
                    <c:otherwise>
                        <table class="table table-striped">
                            <thead>
                                <tr>
                                    <th>ID</th>
                                    <th>Título</th>
                                    <th>Curso ID</th>
                                    <th>Curso Nombre</th>
                                    <th>Fecha Asignación</th>
                                    <th>Fecha Entrega</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="tarea" items="${tareas}">
                                    <tr>
                                        <td>${tarea.id}</td>
                                        <td>${tarea.titulo}</td>
                                        <td>${tarea.idCurso}</td>                                        <td>${tarea.cursoNombre}</td>
                                        <td><fmt:formatDate value="${tarea.fechaAsignacion}" pattern="dd/MM/yyyy"/></td>
                                        <td><fmt:formatDate value="${tarea.fechaEntrega}" pattern="dd/MM/yyyy"/></td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
        
        <div class="card mb-4">
            <div class="card-header">Información de Contexto</div>
            <div class="card-body">
                <p><strong>Context Path:</strong> ${pageContext.request.contextPath}</p>
                <p><strong>Servlet Path:</strong> ${pageContext.request.servletPath}</p>
                <p><strong>Request URI:</strong> ${pageContext.request.requestURI}</p>
                <p><strong>JSP Path:</strong> <%= application.getRealPath(request.getServletPath()) %></p>
                
                <h5 class="mt-3">Parameter Values:</h5>
                <ul>
                    <% 
                    java.util.Enumeration<String> paramNames = request.getParameterNames();
                    while(paramNames.hasMoreElements()) {
                        String paramName = paramNames.nextElement();
                        out.print("<li><strong>" + paramName + ":</strong> " + request.getParameter(paramName) + "</li>");
                    }
                    %>
                </ul>
                
                <h5 class="mt-3">Session Attributes:</h5>
                <ul>
                    <% 
                    if (session != null) {
                        java.util.Enumeration<String> attrNames = session.getAttributeNames();
                        while(attrNames.hasMoreElements()) {
                            String attrName = attrNames.nextElement();
                            out.print("<li><strong>" + attrName + ":</strong> " + session.getAttribute(attrName) + "</li>");
                        }
                    } else {
                        out.print("<li>No hay sesión disponible</li>");
                    }
                    %>
                </ul>
            </div>
        </div>
        
        <div class="card mb-4">
            <div class="card-header">Request Attributes</div>
            <div class="card-body">
                <ul>
                    <% 
                    java.util.Enumeration<String> reqAttrNames = request.getAttributeNames();
                    while(reqAttrNames.hasMoreElements()) {
                        String attrName = reqAttrNames.nextElement();
                        Object value = request.getAttribute(attrName);
                        String strValue;
                        if (value instanceof java.util.Collection) {
                            strValue = "Colección con " + ((java.util.Collection)value).size() + " elementos";
                        } else {
                            strValue = String.valueOf(value);
                        }
                        out.print("<li><strong>" + attrName + ":</strong> " + strValue + "</li>");
                    }
                    %>
                </ul>
            </div>
        </div>
        
        <a href="${pageContext.request.contextPath}/profesor/tareas" class="btn btn-primary">Volver a Tareas</a>
    </div>
    
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
