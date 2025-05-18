<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Diagnóstico de Rutas</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            line-height: 1.6;
            padding: 20px;
        }
        .endpoint {
            margin-bottom: 20px;
            padding: 15px;
            border: 1px solid #ddd;
            border-radius: 5px;
        }
        .endpoint-url {
            font-weight: bold;
            color: #007bff;
        }
        h1, h2 {
            color: #333;
        }
        .success {
            color: green;
        }
        .error {
            color: red;
        }
        pre {
            background: #f5f5f5;
            padding: 10px;
            border-radius: 5px;
            overflow: auto;
        }
    </style>
</head>
<body>
    <h1>Diagnóstico de Rutas para Gestión de Tareas</h1>
    
    <div id="output">Cargando...</div>
    
    <script>
        // Lista de endpoints a probar
        const endpoints = [
            { method: 'GET', url: '/profesor/tareas', description: 'Lista todas las tareas (ProfesorTareasServlet)' },
            { method: 'GET', url: '/profesor/tareas/1', description: 'Obtiene detalles de una tarea por ID (GestionTareasServlet)', expectedResponseCodes: [200, 404] },
            { method: 'GET', url: '/profesor/tareas/1?includeEstudiantes=true', description: 'Obtiene detalles de una tarea con estudiantes incluidos (GestionTareasServlet)', expectedResponseCodes: [200, 404] }
        ];
        
        async function testEndpoints() {
            const baseUrl = window.location.origin;
            const contextPath = window.location.pathname.split('/').slice(0, 2).join('/');
            const output = document.getElementById('output');
            const results = [];
            
            for (const endpoint of endpoints) {
                try {
                    const fullUrl = `${baseUrl}${contextPath}${endpoint.url}`;
                    const result = { endpoint, fullUrl, status: null, data: null, error: null };
                    
                    console.log(`Testing ${endpoint.method} ${fullUrl}`);
                    
                    const options = { method: endpoint.method };
                    const response = await fetch(fullUrl, options);
                    
                    result.status = response.status;
                    
                    try {
                        const text = await response.text();
                        try {
                            result.data = JSON.parse(text);
                        } catch (e) {
                            result.data = text.substring(0, 500) + (text.length > 500 ? '...' : '');
                        }
                    } catch (e) {
                        result.error = `Error reading response: ${e.message}`;
                    }
                    
                    results.push(result);
                } catch (error) {
                    results.push({
                        endpoint,
                        error: `Error fetching endpoint: ${error.message}`
                    });
                }
            }
            
            // Generar salida HTML
            let html = '';
            for (const result of results) {
                const { endpoint, fullUrl, status, data, error } = result;
                const statusClass = error || !status ? 'error' : 
                                    (endpoint.expectedResponseCodes && !endpoint.expectedResponseCodes.includes(status)) ? 'error' : 'success';
                                    
                html += `
                    <div class="endpoint">
                        <h2>${endpoint.description}</h2>
                        <p><strong>Método:</strong> ${endpoint.method}</p>
                        <p><strong>URL:</strong> <span class="endpoint-url">${fullUrl}</span></p>
                        <p><strong>Estado:</strong> <span class="${statusClass}">${status || 'Error'}</span></p>
                        ${error ? `<p><strong>Error:</strong> <span class="error">${error}</span></p>` : ''}
                        <p><strong>Respuesta:</strong></p>
                        <pre>${typeof data === 'object' ? JSON.stringify(data, null, 2) : data || 'Sin datos'}</pre>
                    </div>
                `;
            }
            
            output.innerHTML = html;
        }
        
        // Ejecutar pruebas cuando se cargue la página
        window.addEventListener('DOMContentLoaded', testEndpoints);
    </script>
</body>
</html>
