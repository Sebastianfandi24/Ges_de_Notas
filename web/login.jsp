<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Iniciar Sesión | Registro Académico</title>
    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">

    <style>
        /* Fondo full‑screen */
        body {
            margin: 0;
            padding: 0;
            background: url('https://wallpapers.com/images/featured/4k-blanco-i308ljad82204626.jpg') 
                        no-repeat center center fixed;
            background-size: cover;
            font-family: 'Segoe UI', sans-serif;
        }

        /* Tarjeta glassmorphism */
        .login-container {
            max-width: 400px;
            margin: 6% auto 0;
            padding: 2.5rem;
            background: rgba(0, 0, 0, 0.5);
            backdrop-filter: blur(10px);
            border: 1px solid rgba(255, 255, 255, 0.2);
            border-radius: 16px;
            box-shadow: 0 8px 32px rgba(0, 0, 0, 0.37);
            color: #fff;
        }

        /* Títulos y textos */
        .login-container h3 {
            color: #fff;
            margin-bottom: 0.5rem;
        }
        .login-container p,
        .login-container label {
            color: #ddd;
        }
        .login-container .text-muted {
            color: #ccc !important;
        }
        .login-container a {
            color: #fff;
            text-decoration: none;
        }
        .login-container a:hover {
            text-decoration: underline;
        }

        /* Inputs */
        .form-control {
            background: rgba(255, 255, 255, 0.1);
            border: 1px solid rgba(255, 255, 255, 0.3);
            color: #fff;
            border-radius: 50px;
            padding: 0.75rem 1.25rem;
            transition: all 0.3s;
        }
        .form-control::placeholder {
            color: #eee;
        }
        .form-control:focus {
            background: rgba(255, 255, 255, 0.2);
            border-color: rgba(255, 255, 255, 0.5);
            box-shadow: none;
            color: #fff;
        }

        /* Botón */
        .btn-primary {
            display: block;
            width: 100%;
            background: #fff;
            color: #000;
            border: none;
            border-radius: 50px;
            padding: 0.75rem;
            font-weight: 600;
            transition: background 0.3s;
        }
        .btn-primary:hover {
            background: #f0f0f0;
        }

        /* Ajuste de alertas */
        .alert {
            background: rgba(255, 0, 0, 0.6);
            border: none;
        }
    </style>
</head>
<body>

    <div class="container">
        <div class="login-container">
            <h3 class="text-center fw-bold">Iniciar Sesión</h3>
            <p class="text-center text-muted">Ingrese sus credenciales para acceder al sistema</p>

            <% if (request.getAttribute("error") != null) { %>
                <div class="alert alert-danger alert-dismissible fade show" role="alert">
                    <%= request.getAttribute("error") %>
                    <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                </div>
            <% } %>

            <form action="${pageContext.request.contextPath}/login" method="POST" class="needs-validation" novalidate>
                <div class="mb-3">
                    <label class="form-label">Correo Electrónico</label>
                    <input type="email" class="form-control" name="email" placeholder="correo@ejemplo.com" required
                           pattern="[a-z0-9._%+-]+@[a-z0-9.-]+\.[a-z]{2,}$">
                    <div class="invalid-feedback">
                        Por favor ingrese un correo electrónico válido.
                    </div>
                </div>
                <div class="mb-3">
                    <label class="form-label">Contraseña</label>
                    <input type="password" class="form-control" name="password" required minlength="6">
                    <div class="invalid-feedback">
                        La contraseña debe tener al menos 6 caracteres.
                    </div>
                </div>
                <button type="submit" class="btn btn-primary">Iniciar Sesión</button>
            </form>

        </div>
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
