<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Asignar Estudiantes al Curso</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons/font/bootstrap-icons.css" rel="stylesheet">
</head>
<body>
    <div class="container py-4">
        <div class="card">
            <div class="card-header bg-primary text-white">
                <h4 class="m-0">Asignar Estudiantes al Curso</h4>
            </div>
            <div class="card-body">
                <form action="${pageContext.request.contextPath}/profesor/cursos/asignar" method="post">
                    <input type="hidden" name="cursoId" value="${cursoId}">
                    
                    <div class="table-responsive">
                        <table class="table table-striped">
                            <thead>
                                <tr>
                                    <th>Seleccionar</th>
                                    <th>Nombre</th>
                                    <th>Correo</th>
                                    <th>Identificación</th>
                                </tr>
                            </thead>
                            <tbody>
                                <!-- Estudiantes ya asignados (checkbox marcado) -->
                                <c:forEach var="est" items="${asignados}">
                                    <tr>
                                        <td>
                                            <div class="form-check">
                                                <input class="form-check-input" type="checkbox"
                                                       name="estudiantes" value="${est.id}" checked>
                                            </div>
                                        </td>
                                        <td>${est.nombre}</td>
                                        <td>${est.correo}</td>
                                        <td>${est.numeroIdentificacion}</td>
                                    </tr>
                                </c:forEach>
                                <!-- Estudiantes disponibles (checkbox desmarcado) -->
                                <c:forEach var="est" items="${disponibles}">
                                    <tr>
                                        <td>
                                            <div class="form-check">
                                                <input class="form-check-input" type="checkbox"
                                                       name="estudiantes" value="${est.id}">
                                            </div>
                                        </td>
                                        <td>${est.nombre}</td>
                                        <td>${est.correo}</td>
                                        <td>${est.numeroIdentificacion}</td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>

                    <div class="mt-3">
                        <button type="submit" class="btn btn-primary">
                            <i class="bi bi-plus-circle me-2"></i>Asignar Seleccionados
                        </button>
                        <a href="${pageContext.request.contextPath}/profesor/cursos" 
                           class="btn btn-secondary ms-2">Cancelar</a>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
