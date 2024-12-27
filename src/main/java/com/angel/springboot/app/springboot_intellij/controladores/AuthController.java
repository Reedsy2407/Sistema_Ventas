package com.angel.springboot.app.springboot_intellij.controladores;


import com.angel.springboot.app.springboot_intellij.entidades.Usuario;
import com.angel.springboot.app.springboot_intellij.servicios.AuthService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    @Autowired
    private AuthService authService;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @PostMapping("/login")
    public String autenticar(@RequestParam String username, @RequestParam String password, HttpSession session, Model model) {
        Usuario usuario = authService.autenticar(username, password);
        if (usuario != null) {
            session.setAttribute("usuario", usuario);
            return "redirect:/";
        }
        model.addAttribute("error", "Credenciales inválidas");
        return "login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}
