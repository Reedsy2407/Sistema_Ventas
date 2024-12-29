package com.angel.springboot.app.springboot_intellij.controladores;


import com.angel.springboot.app.springboot_intellij.entidades.Usuario;
import com.angel.springboot.app.springboot_intellij.repositorios.RolRepository;
import com.angel.springboot.app.springboot_intellij.repositorios.UsuarioRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolRepository rolRepository;

    @GetMapping
    public String listarUsuarios(
            @RequestParam(value = "username", required = false) String username,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Usuario> usuariosPage = usuarioRepository.findAll(pageable);

        model.addAttribute("usuarios", usuariosPage.getContent());
        model.addAttribute("currentPage", usuariosPage.getNumber());
        model.addAttribute("totalPages", usuariosPage.getTotalPages());
        model.addAttribute("totalItems", usuariosPage.getTotalElements());
        model.addAttribute("size", size);
        model.addAttribute("title", "Usuarios - Sistema de Ventas");
        model.addAttribute("currentPageName", "usuarios");
        model.addAttribute("contentTemplate", "Usuarios/usuarios");
        return "layout";
    }

    @GetMapping("/agregarUsuario")
    public String mostrarUsuarioAgregar(Model model) {
        model.addAttribute("nuevoUsuario", new Usuario());
        model.addAttribute("roles", rolRepository.findAll());
        model.addAttribute("title", "Añadir Usuario - Sistema de Ventas");
        model.addAttribute("currentPage", "usuarios");
        model.addAttribute("contentTemplate", "Usuarios/agregarUsuario");
        return "layout";
    }

    @PostMapping("/agregarUsuario")
    public String crearUsuario(@Validated @ModelAttribute("nuevoUsuario") Usuario usuario,
                               BindingResult bindingResult, RedirectAttributes redirectAttrs, Model model) {
        if (bindingResult.hasErrors() || usuario.getRol() == null || usuario.getRol().getId() == null) {
            model.addAttribute("roles", rolRepository.findAll());
            model.addAttribute("error", "Debe seleccionar un rol válido.");
            model.addAttribute("title", "Añadir Usuario - Sistema de Ventas");
            model.addAttribute("currentPage", "usuarios");
            model.addAttribute("contentTemplate", "Usuarios/agregarUsuario");
            return "layout";
        }

        if (usuarioRepository.findByUsername(usuario.getUsername()) != null) {
            model.addAttribute("roles", rolRepository.findAll());
            model.addAttribute("error", "El nombre de usuario ya está registrado.");
            model.addAttribute("title", "Añadir Usuario - Sistema de Ventas");
            model.addAttribute("currentPage", "usuarios");
            model.addAttribute("contentTemplate", "Usuarios/agregarUsuario");
            return "layout";
        }

        usuarioRepository.save(usuario);
        redirectAttrs.addFlashAttribute("success", "Usuario añadido con éxito.");
        return "redirect:/usuarios";
    }


    @GetMapping("/editarUsuario/{id}")
    public String mostrarUsuarioEditar(@PathVariable Integer id, Model model, RedirectAttributes redirectAttrs) {
        Optional<Usuario> usuario = usuarioRepository.findById(id);
        if (usuario.isPresent()) {
            model.addAttribute("usuarioEditar", usuario.get());
            model.addAttribute("roles", rolRepository.findAll());
            model.addAttribute("title", "Editar Usuario - Sistema de Ventas");
            model.addAttribute("currentPage", "usuarios");
            model.addAttribute("contentTemplate", "Usuarios/editarUsuario");
            return "layout";
        } else {
            redirectAttrs.addFlashAttribute("error", "Usuario no encontrado.");
            return "redirect:/usuarios";
        }
    }

    @PostMapping("/editarUsuario/{id}")
    public String actualizarUsuario(@PathVariable Integer id, @Validated @ModelAttribute("usuarioEditar") Usuario usuario,
                                    BindingResult bindingResult, RedirectAttributes redirectAttrs, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("roles", rolRepository.findAll());
            model.addAttribute("title", "Editar Usuario - Sistema de Ventas");
            model.addAttribute("currentPage", "usuarios");
            model.addAttribute("contentTemplate", "Usuarios/editarUsuario");
            return "layout";
        }

        usuario.setId(id);
        usuarioRepository.save(usuario);
        redirectAttrs.addFlashAttribute("success", "Usuario actualizado con éxito.");
        return "redirect:/usuarios";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarUsuario(@PathVariable Integer id, HttpSession session, RedirectAttributes redirectAttrs) {
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuario");

        if (usuarioLogueado != null && usuarioLogueado.getId().equals(id)) {
            redirectAttrs.addFlashAttribute("error", "No puedes eliminar tu propio usuario.");
            return "redirect:/usuarios";
        }

        Optional<Usuario> usuario = usuarioRepository.findById(id);
        if (usuario.isPresent()) {
            usuarioRepository.deleteById(id);
            redirectAttrs.addFlashAttribute("success", "Usuario eliminado con éxito.");
        } else {
            redirectAttrs.addFlashAttribute("error", "Usuario no encontrado.");
        }
        return "redirect:/usuarios";
    }

}