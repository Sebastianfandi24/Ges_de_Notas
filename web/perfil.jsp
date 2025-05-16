<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%
    if (session == null || session.getAttribute("userId") == null) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }
%>
<c:set var="userRol" value="${sessionScope.userRol}" />
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Mi Perfil</title>
    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Font Awesome -->
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
    <style>
        body {
            background-color: #f8f9fa;
        }
        .profile-container {
            max-width: 800px;
            margin: 2rem auto;
            background: #fff;
            border-radius: 16px;
            box-shadow: 0 4px 12px rgba(0,0,0,0.08);
            padding: 2rem;
        }
        .profile-header {
            border-bottom: 1px solid #eaeaea;
            margin-bottom: 1.5rem;
            padding-bottom: 1rem;
        }
        .profile-header h2 {
            color: #2e6ff8;
            font-weight: 700;
        }
        .form-label {
            font-weight: 600;
            margin-bottom: .3rem;
            color: #444;
        }
        .form-control:focus {
            border-color: #2e6ff8;
            box-shadow: 0 0 0 0.25rem rgba(46, 111, 248, 0.25);
        }
        .section-title {
            margin-top: 1.5rem;
            margin-bottom: 1rem;
            padding-bottom: 0.5rem;
            border-bottom: 1px solid #eaeaea;
            color: #2e6ff8;
            font-weight: 600;
        }
        .btn-save {
            background-color: #2e6ff8;
            border-color: #2e6ff8;
        }
        .btn-save:hover {
            background-color: #235ac4;
            border-color: #235ac4;
        }
    </style>
</head>
<body>
    <div class="profile-container">
        <div class="profile-header">
            <h2><i class="fas fa-user-circle me-2"></i>Mi Perfil</h2>
            <p class="text-muted">Actualiza tu información personal</p>
        </div>
        
        <!-- Alertas de resultado -->
        <c:if test="${not empty mensaje}">
            <div class="alert alert-success alert-dismissible fade show" role="alert">
                ${mensaje}
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
        </c:if>
        
        <c:if test="${not empty error}">
            <div class="alert alert-danger alert-dismissible fade show" role="alert">
                ${error}
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
        </c:if>

        <!-- Formulario de perfil -->
        <form action="${pageContext.request.contextPath}/perfil" method="POST" class="needs-validation" novalidate>
            <h5 class="section-title">Información General</h5>
            <div class="row mb-3">
                <div class="col-md-6">
                    <label for="nombre" class="form-label">Nombre Completo</label>
                    <input type="text" class="form-control" id="nombre" name="nombre" value="${perfil.nombre}" required>
                    <div class="invalid-feedback">
                        Por favor ingresa tu nombre completo.
                    </div>
                </div>
                <div class="col-md-6">
                    <label for="correo" class="form-label">Correo Electrónico</label>
                    <input type="email" class="form-control" id="correo" name="correo" value="${perfil.correo}" required>
                    <div class="invalid-feedback">
                        Por favor ingresa un correo electrónico válido.
                    </div>
                </div>
            </div>            <h5 class="section-title">Cambiar Contraseña</h5>
            <div class="row mb-3">
                <div class="col-md-12">
                    <label for="nueva_contrasena" class="form-label">Nueva Contraseña (dejar vacío para mantener la actual)</label>
                    <input type="password" class="form-control" id="nueva_contrasena" name="nueva_contrasena" minlength="6">
                    <div class="form-text">Si no deseas cambiar tu contraseña, deja este campo en blanco.</div>
                    <div class="invalid-feedback">
                        La contraseña debe tener al menos 6 caracteres.
                    </div>
                </div>
            </div>
            
            <!-- Confirmación de contraseña para validación adicional -->
            <div class="row mb-3">
                <div class="col-md-12">
                    <label for="confirmar_contrasena" class="form-label">Confirmar Nueva Contraseña</label>
                    <input type="password" class="form-control" id="confirmar_contrasena" name="confirmar_contrasena" minlength="6">
                    <div class="form-text">Vuelve a ingresar la contraseña para confirmar.</div>
                    <div class="invalid-feedback" id="confirmar-error">
                        Las contraseñas no coinciden.
                    </div>
                </div>
            </div>
            
            <script>
                document.addEventListener('DOMContentLoaded', function() {
                    // Log para validación del formulario en el navegador
                    console.log('[VALIDACIÓN-BROWSER] Formulario de perfil cargado correctamente');
                    
                    // Agregar validación al campo de contraseña
                    const passwordField = document.getElementById('nueva_contrasena');
                    const confirmField = document.getElementById('confirmar_contrasena');
                    const form = document.querySelector('form');
                    
                    // Validación de la contraseña
                    passwordField.addEventListener('input', function() {
                        console.log('[VALIDACIÓN-BROWSER] Campo de contraseña modificado, longitud: ' + this.value.length);
                        if (this.value.length > 0 && this.value.length < 6) {
                            this.setCustomValidity('La contraseña debe tener al menos 6 caracteres');
                        } else {
                            this.setCustomValidity('');
                        }
                        if (confirmField.value !== this.value) {
                            confirmField.setCustomValidity('Las contraseñas no coinciden');
                        } else {
                            confirmField.setCustomValidity('');
                        }
                    });
                    confirmField.addEventListener('input', function() {
                        if (this.value !== passwordField.value) {
                            this.setCustomValidity('Las contraseñas no coinciden');
                        } else {
                            this.setCustomValidity('');
                            console.log('[VALIDACIÓN-BROWSER] Contraseñas coinciden correctamente');
                        }
                    });
                    // Interceptar envío del formulario para validar
                    form.addEventListener('submit', function(e) {
                        if (passwordField.value.length > 0) {
                            if (passwordField.value !== confirmField.value) {
                                e.preventDefault();
                                confirmField.setCustomValidity('Las contraseñas no coinciden');
                                console.log('[VALIDACIÓN-BROWSER] ERROR: Las contraseñas no coinciden');
                                return false;
                            } else {
                                console.log('[VALIDACIÓN-BROWSER] Las contraseñas coinciden, enviando formulario');
                            }
                        } else {
                            console.log('[VALIDACIÓN-BROWSER] No se enviará nueva contraseña');
                        }
                    });
                });
            </script>
            
            <!-- Campos específicos para Estudiante -->
            <c:if test="${userRol == 1}">
                <h5 class="section-title">Información de Estudiante</h5>
                <div class="row mb-3">
                    <div class="col-md-6">
                        <label for="fecha_nacimiento" class="form-label">Fecha de Nacimiento</label>
                        <input type="date" class="form-control" id="fecha_nacimiento" name="fecha_nacimiento" 
                               value="<fmt:formatDate pattern="yyyy-MM-dd" value="${perfil.fecha_nacimiento}" />">
                    </div>
                    <div class="col-md-6">
                        <label for="numero_identificacion" class="form-label">Número de Identificación</label>
                        <input type="text" class="form-control" id="numero_identificacion" name="numero_identificacion" value="${perfil.numero_identificacion}">
                    </div>
                </div>
                <div class="row mb-3">
                    <div class="col-md-6">
                        <label for="telefono" class="form-label">Teléfono</label>
                        <input type="tel" class="form-control" id="telefono" name="telefono" value="${perfil.telefono}">
                    </div>
                    <div class="col-md-6">
                        <label for="direccion" class="form-label">Dirección</label>
                        <input type="text" class="form-control" id="direccion" name="direccion" value="${perfil.direccion}">
                    </div>
                </div>
            </c:if>
            
            <!-- Campos específicos para Profesor -->
            <c:if test="${userRol == 2}">
                <h5 class="section-title">Información de Profesor</h5>
                <div class="row mb-3">
                    <div class="col-md-6">
                        <label for="fecha_nacimiento" class="form-label">Fecha de Nacimiento</label>
                        <input type="date" class="form-control" id="fecha_nacimiento" name="fecha_nacimiento" 
                               value="<fmt:formatDate pattern="yyyy-MM-dd" value="${perfil.fecha_nacimiento}" />">
                    </div>
                    <div class="col-md-6">
                        <label for="telefono" class="form-label">Teléfono</label>
                        <input type="tel" class="form-control" id="telefono" name="telefono" value="${perfil.telefono}">
                    </div>
                </div>
                <div class="row mb-3">
                    <div class="col-md-6">
                        <label for="grado_academico" class="form-label">Grado Académico</label>
                        <select class="form-select" id="grado_academico" name="grado_academico">
                            <option value="Licenciatura" ${perfil.grado_academico == 'Licenciatura' ? 'selected' : ''}>Licenciatura</option>
                            <option value="Maestría" ${perfil.grado_academico == 'Maestría' ? 'selected' : ''}>Maestría</option>
                            <option value="Doctorado" ${perfil.grado_academico == 'Doctorado' ? 'selected' : ''}>Doctorado</option>
                            <option value="PostDoctorado" ${perfil.grado_academico == 'PostDoctorado' ? 'selected' : ''}>Post-Doctorado</option>
                        </select>
                    </div>
                    <div class="col-md-6">
                        <label for="especializacion" class="form-label">Especialización</label>
                        <input type="text" class="form-control" id="especializacion" name="especializacion" value="${perfil.especializacion}">
                    </div>
                </div>
                <div class="row mb-3">
                    <div class="col-md-12">
                        <label for="direccion" class="form-label">Dirección</label>
                        <input type="text" class="form-control" id="direccion" name="direccion" value="${perfil.direccion}">
                    </div>
                </div>
            </c:if>
            
            <!-- Campos específicos para Administrador -->
            <c:if test="${userRol == 3}">
                <h5 class="section-title">Información de Administrador</h5>
                <div class="row mb-3">
                    <div class="col-md-12">
                        <label for="departamento" class="form-label">Departamento</label>
                        <input type="text" class="form-control" id="departamento" name="departamento" value="${perfil.departamento}">
                    </div>
                </div>
            </c:if>
            
            <div class="d-grid gap-2 mt-4">
                <button type="submit" class="btn btn-primary btn-save">
                    <i class="fas fa-save me-2"></i>Guardar Cambios
                </button>
                <a href="${pageContext.request.contextPath}/menu" class="btn btn-outline-secondary">
                    <i class="fas fa-arrow-left me-2"></i>Volver al Menú
                </a>
            </div>
        </form>
    </div>

    <!-- Bootstrap JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <!-- Validación de Bootstrap -->
    <script>
      (function () {
        'use strict'
        const forms = document.querySelectorAll('.needs-validation');
        Array.from(forms).forEach(form => {
          form.addEventListener('submit', event => {
            if (!form.checkValidity()) {
              event.preventDefault();
              event.stopPropagation();
            }
            form.classList.add('was-validated');
          }, false);
        });
      })();
    </script>
</body>
</html>
