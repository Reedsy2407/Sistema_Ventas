package com.angel.springboot.app.springboot_intellij.interceptor;

import com.angel.springboot.app.springboot_intellij.entidades.Usuario;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Component
public class UsuarioInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession(false); // Evitar crear sesiones
        Usuario usuario = (session != null) ? (Usuario) session.getAttribute("usuario") : null;
        String uri = request.getRequestURI();

        // Permitir acceso sin sesión a rutas públicas
        if (usuario == null && (uri.equals("/login") || uri.startsWith("/css") || uri.startsWith("/js")
                || uri.startsWith("/images") || uri.equals("/robots.txt") || uri.equals("/api/usuarios/sesion-activa"))) {
            return true;
        }

        // Redirigir a login si no hay usuario autenticado
        if (usuario == null) {
            response.sendRedirect("/login");
            return false;
        }

        // Restringir acceso a rutas de administradores
        if (uri.startsWith("/usuarios") && !usuario.getRol().getNombre().equalsIgnoreCase("ADMIN")) {
            response.sendRedirect("/");
            return false;
        }

        return true;
    }



    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response,
                           Object handler, ModelAndView modelAndView) throws Exception {
        HttpSession session = request.getSession();
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (modelAndView != null && usuario != null) {
            modelAndView.addObject("usuario", usuario);
        }
    }
}
