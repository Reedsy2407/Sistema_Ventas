package com.angel.springboot.app.springboot_intellij.controladores;


import com.angel.springboot.app.springboot_intellij.entidades.Categoria;
import com.angel.springboot.app.springboot_intellij.repositorios.CategoriaRepository;
import com.angel.springboot.app.springboot_intellij.repositorios.ProductoRepository;
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
@RequestMapping("/categorias")
public class CategoriaController {

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @GetMapping
    public String listarCategorias(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Categoria> categoriasPage = categoriaRepository.findAll(pageable);

        model.addAttribute("categorias", categoriasPage.getContent());
        model.addAttribute("currentPage", categoriasPage.getNumber());
        model.addAttribute("totalPages", categoriasPage.getTotalPages());
        model.addAttribute("size", size);
        model.addAttribute("title", "Categorias - Sistema de Ventas");
        model.addAttribute("currentPageName", "categorias");
        model.addAttribute("contentTemplate", "Categorias/categorias");
        return "layout"; // Plantilla principal
    }

    @GetMapping("/agregarCategoria")
    public String mostrarFormularioAgregar(Model model) {
        model.addAttribute("nuevaCategoria", new Categoria());
        model.addAttribute("title", "Agregar Categoría - Sistema de Ventas");
        model.addAttribute("currentPage", "categorias");
        model.addAttribute("contentTemplate", "Categorias/agregarCategoria");
        return "layout";
    }

    @PostMapping("/agregarCategoria")
    public String agregarCategoria(@Validated @ModelAttribute("nuevaCategoria") Categoria categoria,
                                   BindingResult bindingResult, RedirectAttributes redirectAttrs, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("title", "Agregar Categoría - Sistema de Ventas");
            model.addAttribute("currentPage", "categorias");
            model.addAttribute("contentTemplate", "Categorias/agregarCategoria");
            return "layout";
        }

        if (categoriaRepository.existsByNombre(categoria.getNombre())) {
            model.addAttribute("error", "El nombre ya está registrado.");
            model.addAttribute("title", "Agregar Categoría - Sistema de Ventas");
            model.addAttribute("currentPage", "categorias");
            model.addAttribute("contentTemplate", "Categorias/agregarCategoria");
            return "layout";
        }

        categoriaRepository.save(categoria);
        redirectAttrs.addFlashAttribute("success", "Categoría agregada con éxito.");
        return "redirect:/categorias";
    }

    @GetMapping("/editarCategoria/{id}")
    public String mostrarFormularioEditar(@PathVariable Integer id, Model model, RedirectAttributes redirectAttrs) {
        Optional<Categoria> categoria = categoriaRepository.findById(id);
        if (categoria.isPresent()) {
            model.addAttribute("categoriaEditar", categoria.get());
            model.addAttribute("title", "Editar Categoría - Sistema de Ventas");
            model.addAttribute("currentPage", "categorias");
            model.addAttribute("contentTemplate", "Categorias/editarCategoria");
            return "layout";
        } else {
            redirectAttrs.addFlashAttribute("error", "Categoría no encontrada.");
            return "redirect:/categorias";
        }
    }

    @PostMapping("/editarCategoria/{id}")
    public String actualizarCategoria(@PathVariable Integer id, @Validated @ModelAttribute("categoriaEditar") Categoria categoria,
                                      BindingResult bindingResult, RedirectAttributes redirectAttrs, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("title", "Editar Categoría - Sistema de Ventas");
            model.addAttribute("currentPage", "categorias");
            model.addAttribute("contentTemplate", "Categorias/editarCategoria");
            return "layout";
        }

        if (!categoriaRepository.existsById(id)) {
            model.addAttribute("error", "Categoría no encontrada.");
            model.addAttribute("title", "Editar Categoría - Sistema de Ventas");
            model.addAttribute("currentPage", "categorias");
            model.addAttribute("contentTemplate", "Categorias/editarCategoria");
            return "layout";
        }

        categoria.setId(id);
        categoriaRepository.save(categoria);
        redirectAttrs.addFlashAttribute("success", "Categoría actualizada con éxito.");
        return "redirect:/categorias";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarCategoria(@PathVariable Integer id, RedirectAttributes redirectAttrs) {
        if (productoRepository.countByCategoriaId(id) > 0) {
            redirectAttrs.addFlashAttribute("error", "No se puede eliminar porque está asociada a algun producto.");
        } else {
            categoriaRepository.deleteById(id);
            redirectAttrs.addFlashAttribute("success", "Categoría eliminada con éxito.");
        }
        return "redirect:/categorias";
    }
}