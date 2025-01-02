package com.angel.springboot.app.springboot_intellij.controladores;


import com.angel.springboot.app.springboot_intellij.entidades.Producto;
import com.angel.springboot.app.springboot_intellij.repositorios.CategoriaRepository;
import com.angel.springboot.app.springboot_intellij.repositorios.DetalleVentaRepository;
import com.angel.springboot.app.springboot_intellij.repositorios.ProductoRepository;
import com.angel.springboot.app.springboot_intellij.servicios.VentaService;
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
@RequestMapping("/productos")
public class ProductoController {

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private DetalleVentaRepository detalleVentaRepository;

    @Autowired
    private VentaService ventaService;

    @GetMapping
    public String listarProductos(
            @RequestParam(value = "nombre", required = false) String nombre,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Producto> productosPage;

        if (nombre != null && !nombre.isEmpty()) {
            productosPage = productoRepository.findByNombreContainingIgnoreCase(nombre, pageable);
        } else {
            productosPage = productoRepository.findAll(pageable);
        }

        model.addAttribute("productos", productosPage.getContent()); // Lista de productos
        model.addAttribute("currentPage", productosPage.getNumber()); // Página actual
        model.addAttribute("totalPages", productosPage.getTotalPages()); // Total de páginas
        model.addAttribute("totalItems", productosPage.getTotalElements()); // Total de elementos
        model.addAttribute("size", size); // Tamaño de la página
        model.addAttribute("title", "Productos - Sistema de Ventas");
        model.addAttribute("currentPageName", "productos");
        model.addAttribute("contentTemplate", "Productos/productos");
        return "layout"; // Plantilla principal
    }

    @GetMapping("/bajoStock")
    public String listarProductosBajoStock(@RequestParam(defaultValue = "0") int page,
                                           @RequestParam(defaultValue = "10") int size,
                                           @RequestParam(defaultValue = "10") int stockMinimo, Model model) {

        Pageable pageable = PageRequest.of(page, size); // Crear objeto Pageable
        Page<Producto> productosBajoStock = productoRepository.findByStockLessThan(stockMinimo, pageable);

        model.addAttribute("productos", productosBajoStock.getContent());
        model.addAttribute("currentPage", productosBajoStock.getNumber()); // Página actual
        model.addAttribute("totalPages", productosBajoStock.getTotalPages()); // Total de páginas
        model.addAttribute("totalItems", productosBajoStock.getTotalElements()); // Total de elementos
        model.addAttribute("size", size); // Tamaño de la página
        model.addAttribute("mensaje", "Productos con stock menor a " + stockMinimo);
        model.addAttribute("title", "Productos con bajo stock - Sistema de Ventas");
        model.addAttribute("currentPageName", "productos");
        model.addAttribute("contentTemplate", "Productos/productos");
        return "layout"; // Plantilla principal
    }


    @GetMapping("/agregarProducto")
    public String mostrarProductoAgregar(Model model) {
        model.addAttribute("nuevoProducto", new Producto());
        model.addAttribute("categorias", categoriaRepository.findAll()); // Cargar categorías disponibles
        model.addAttribute("title", "Añadir Producto - Sistema de Ventas");
        model.addAttribute("currentPage", "productos");
        model.addAttribute("contentTemplate", "Productos/agregarProducto");
        return "layout";
    }


    @PostMapping("/agregarProducto")
    public String crearProducto(@Validated @ModelAttribute("nuevoProducto") Producto producto,
                                BindingResult bindingResult, RedirectAttributes redirectAttrs, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("categorias", categoriaRepository.findAll()); // Volver a cargar categorías en caso de error
            model.addAttribute("title", "Añadir Producto - Sistema de Ventas");
            model.addAttribute("currentPage", "productos");
            model.addAttribute("contentTemplate", "Productos/agregarProducto");
            return "layout";
        }

        if (productoRepository.findByNombre(producto.getNombre()).isPresent()) {
            model.addAttribute("categorias", categoriaRepository.findAll());
            model.addAttribute("error", "El nombre ya está registrado.");
            model.addAttribute("title", "Añadir Producto - Sistema de Ventas");
            model.addAttribute("currentPage", "productos");
            model.addAttribute("contentTemplate", "Productos/agregarProducto");
            return "layout";
        }

        productoRepository.save(producto);
        redirectAttrs.addFlashAttribute("success", "Producto añadido con éxito.");
        return "redirect:/productos";
    }

    @GetMapping("/editarProducto/{id}")
    public String mostrarProductoEditar(@PathVariable Integer id, Model model, RedirectAttributes redirectAttrs) {
        Optional<Producto> producto = productoRepository.findById(id);
        if (producto.isPresent()) {
            model.addAttribute("productoEditar", producto.get());
            model.addAttribute("categorias", categoriaRepository.findAll()); // Cargar categorías disponibles
            model.addAttribute("title", "Editar Producto - Sistema de Ventas");
            model.addAttribute("currentPage", "productos");
            model.addAttribute("contentTemplate", "Productos/editarProducto");
            return "layout";
        } else {
            redirectAttrs.addFlashAttribute("error", "Producto no encontrado.");
            return "redirect:/productos";
        }
    }

    @PostMapping("/editarProducto/{id}")
    public String actualizarProducto(@PathVariable Integer id, @Validated @ModelAttribute("productoEditar") Producto producto,
                                     BindingResult bindingResult, RedirectAttributes redirectAttrs, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("categorias", categoriaRepository.findAll()); // Volver a cargar categorías en caso de error
            model.addAttribute("title", "Editar Producto - Sistema de Ventas");
            model.addAttribute("currentPage", "productos");
            model.addAttribute("contentTemplate", "Productos/editarProducto");
            return "layout";
        }

        Optional<Producto> productoExistente = productoRepository.findById(id);
        if (productoExistente.isPresent()) {
            Optional<Producto> productoNombre = productoRepository.findByNombre(producto.getNombre());
            if (productoNombre.isPresent() && !productoNombre.get().getId().equals(id)) {
                model.addAttribute("categorias", categoriaRepository.findAll()); // Volver a cargar categorías en caso de error
                model.addAttribute("error", "El nombre ya está registrado en otro producto.");
                model.addAttribute("title", "Editar Producto - Sistema de Ventas");
                model.addAttribute("currentPage", "productos");
                model.addAttribute("contentTemplate", "Productos/editarProducto");
                return "layout";
            }

            producto.setId(id);
            productoRepository.save(producto);

            ventaService.actualizarPreciosVentaPorProducto(producto);

            redirectAttrs.addFlashAttribute("success", "Producto actualizado con éxito.");
        } else {
            model.addAttribute("error", "Producto no encontrado.");
            model.addAttribute("title", "Editar Producto - Sistema de Ventas");
            model.addAttribute("currentPage", "productos");
            model.addAttribute("contentTemplate", "Productos/editarProducto");
            return "layout";
        }

        return "redirect:/productos";
    }


    @GetMapping("/eliminar/{id}")
    public String eliminarProducto(@PathVariable Integer id, RedirectAttributes redirectAttrs) {
        Optional<Producto> productoOpt = productoRepository.findById(id);
        if (productoOpt.isPresent()) {
            long countDetalles = detalleVentaRepository.countByProductoId(id);
            if (countDetalles > 0) {
                redirectAttrs.addFlashAttribute("error", "No se puede eliminar el producto porque está asociado a ventas.");
            } else {
                productoRepository.deleteById(id);
                redirectAttrs.addFlashAttribute("success", "Producto eliminado con éxito.");
            }
        } else {
            redirectAttrs.addFlashAttribute("error", "Producto no encontrado.");
        }
        return "redirect:/productos";
    }

    @GetMapping("/buscar")
    public String buscarProductos(
            @RequestParam(value = "nombre", required = false) String nombre,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Producto> productosPage;
        if (nombre != null && !nombre.isEmpty()) {
            productosPage = productoRepository.findByNombreContainingIgnoreCase(nombre, pageable);
        } else {
            productosPage = productoRepository.findAll(pageable);
        }
        model.addAttribute("productos", productosPage.getContent());
        model.addAttribute("currentPage", productosPage.getNumber());
        model.addAttribute("totalPages", productosPage.getTotalPages());
        model.addAttribute("totalItems", productosPage.getTotalElements());
        model.addAttribute("size", size);
        model.addAttribute("title", "Buscar Productos - Sistema de Ventas");
        model.addAttribute("currentPageName", "productos");
        model.addAttribute("contentTemplate", "Productos/productos");
        return "layout";
    }

}