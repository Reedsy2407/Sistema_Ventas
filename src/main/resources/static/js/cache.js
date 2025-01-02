document.addEventListener("DOMContentLoaded", () => {
    // Verificar si se está intentando retroceder
    if (performance.navigation.type === 2) { // 2 = back/forward
        fetch('/api/usuarios/sesion-activa') // Endpoint para verificar sesión
            .then(response => {
                if (!response.ok) {
                    // Si la sesión no está activa, redirigir al login
                    window.location.href = '/login';
                }
            })
            .catch(() => {
                // En caso de error, redirigir al login como fallback
                window.location.href = '/login';
            });
    }

    // Prevenir navegación hacia atrás si está en login
    if (window.location.pathname === "/login") {
        history.pushState(null, null, window.location.href); // Agregar estado al historial
        window.onpopstate = () => {
            history.pushState(null, null, window.location.href); // Forzar quedarse en login
        };
    }
});
