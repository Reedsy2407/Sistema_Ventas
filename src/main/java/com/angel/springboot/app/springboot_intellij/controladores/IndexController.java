package com.angel.springboot.app.springboot_intellij.controladores;

import com.angel.springboot.app.springboot_intellij.entidades.Usuario;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {

    @GetMapping("/")
    public String index(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) {
            return "redirect:/login";
        }
        model.addAttribute("title", "Sistema de Ventas - Inicio");
        model.addAttribute("currentPage", "index");
        model.addAttribute("contentTemplate", "index");
        model.addAttribute("usuario", usuario);
        return "layout";
    }
}