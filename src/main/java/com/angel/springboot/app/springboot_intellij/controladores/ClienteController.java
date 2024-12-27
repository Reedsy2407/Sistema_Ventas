package com.angel.springboot.app.springboot_intellij.controladores;

import com.angel.springboot.app.springboot_intellij.entidades.Cliente;
import com.angel.springboot.app.springboot_intellij.repositorios.ClienteRepository;
import com.angel.springboot.app.springboot_intellij.repositorios.VentaRepository;
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
    @RequestMapping("/clientes")
    public class ClienteController {

        @Autowired
        private ClienteRepository clienteRepository;

        @Autowired
        private VentaRepository ventaRepository;

        @GetMapping
        public String listarClientes(
                @RequestParam(defaultValue = "0") int page,
                @RequestParam(defaultValue = "10") int size,
                Model model) {

            Pageable pageable = PageRequest.of(page, size);
            Page<Cliente> clientesPage = clienteRepository.findAll(pageable);

            model.addAttribute("clientes", clientesPage.getContent());
            model.addAttribute("currentPage", clientesPage.getNumber());
            model.addAttribute("totalPages", clientesPage.getTotalPages());
            model.addAttribute("totalItems", clientesPage.getTotalElements());
            model.addAttribute("size", size);
            model.addAttribute("title", "Clientes - Sistema de Ventas");
            model.addAttribute("currentPageName", "clientes");
            model.addAttribute("contentTemplate", "Clientes/clientes");
            return "layout";
        }

        @GetMapping("/agregarCliente")
        public String mostrarClienteAgregar(Model model) {
            model.addAttribute("nuevoCliente", new Cliente());
            model.addAttribute("title", "Añadir Cliente - Sistema de Ventas");
            model.addAttribute("currentPage", "clientes");
            model.addAttribute("contentTemplate", "Clientes/agregarCliente");
            return "layout"; // Reutiliza el layout principal
        }

        @PostMapping("/agregarCliente")
        public String crearCliente(@Validated @ModelAttribute("nuevoCliente") Cliente cliente,
                                   BindingResult bindingResult, RedirectAttributes redirectAttrs, Model model) {
            if (bindingResult.hasErrors()) {
                model.addAttribute("title", "Añadir Cliente - Sistema de Ventas");
                model.addAttribute("currentPage", "clientes");
                model.addAttribute("contentTemplate", "Clientes/agregarCliente");
                return "layout";
            }

            if (clienteRepository.findByEmail(cliente.getEmail()).isPresent()) {
                model.addAttribute("error", "El email ya está registrado.");
                model.addAttribute("title", "Añadir Cliente - Sistema de Ventas");
                model.addAttribute("currentPage", "clientes");
                model.addAttribute("contentTemplate", "Clientes/agregarCliente");
                return "layout";
            } else if (clienteRepository.findByTelefono(cliente.getTelefono()).isPresent()) {
                model.addAttribute("error", "El número de teléfono ya está registrado.");
                model.addAttribute("title", "Añadir Cliente - Sistema de Ventas");
                model.addAttribute("currentPage", "clientes");
                model.addAttribute("contentTemplate", "Clientes/agregarCliente");
                return "layout";
            } else if (clienteRepository.findByDni(cliente.getDni()).isPresent()) {
                model.addAttribute("error", "El dni ya está registrado.");
                model.addAttribute("title", "Añadir Cliente - Sistema de Ventas");
                model.addAttribute("currentPage", "clientes");
                model.addAttribute("contentTemplate", "Clientes/agregarCliente");
                return "layout";
            }

            clienteRepository.save(cliente);
            redirectAttrs.addFlashAttribute("success", "Cliente añadido con éxito.");
            return "redirect:/clientes";
        }



        @GetMapping("/editarCliente/{id}")
        public String mostrarClienteEditar(@PathVariable Integer id, Model model, RedirectAttributes redirectAttrs) {
            Optional<Cliente> cliente = clienteRepository.findById(id);
            if (cliente.isPresent()) {
                model.addAttribute("clienteEditar", cliente.get());
                model.addAttribute("title", "Editar Cliente - Sistema de Ventas");
                model.addAttribute("currentPage", "clientes");
                model.addAttribute("contentTemplate", "Clientes/editarCliente");
                return "layout";
            } else {
                redirectAttrs.addFlashAttribute("error", "Cliente no encontrado.");
                return "redirect:/clientes";
            }
        }

        @PostMapping("/editarCliente/{id}")
        public String actualizarCliente(@PathVariable Integer id, @Validated @ModelAttribute("clienteEditar") Cliente cliente,
                                        BindingResult bindingResult, RedirectAttributes redirectAttrs, Model model) {
            if (bindingResult.hasErrors()) {
                model.addAttribute("title", "Editar Cliente - Sistema de Ventas");
                model.addAttribute("currentPage", "clientes");
                model.addAttribute("contentTemplate", "Clientes/editarCliente");
                return "layout";
            }

            Optional<Cliente> clienteExistente = clienteRepository.findById(id);
            if (clienteExistente.isPresent()) {
                Optional<Cliente> clienteEmail = clienteRepository.findByEmail(cliente.getEmail());
                Optional<Cliente> clienteTelefono = clienteRepository.findByTelefono(cliente.getTelefono());
                Optional<Cliente> clienteDni = clienteRepository.findByDni(cliente.getDni());

                if (clienteEmail.isPresent() && !clienteEmail.get().getId().equals(id)) {
                    model.addAttribute("error", "El email ya está registrado en otro cliente.");
                    model.addAttribute("title", "Editar Cliente - Sistema de Ventas");
                    model.addAttribute("currentPage", "clientes");
                    model.addAttribute("contentTemplate", "Clientes/editarCliente");
                    return "layout";
                } else if (clienteTelefono.isPresent() && !clienteTelefono.get().getId().equals(id)) {
                    model.addAttribute("error", "El número de teléfono ya está registrado en otro cliente.");
                    model.addAttribute("title", "Editar Cliente - Sistema de Ventas");
                    model.addAttribute("currentPage", "clientes");
                    model.addAttribute("contentTemplate", "Clientes/editarCliente");
                    return "layout";
                } else if (clienteDni.isPresent() && !clienteDni.get().getId().equals(id)) {
                    model.addAttribute("error", "El dni ya está registrado en otro cliente.");
                    model.addAttribute("title", "Editar Cliente - Sistema de Ventas");
                    model.addAttribute("currentPage", "clientes");
                    model.addAttribute("contentTemplate", "Clientes/editarCliente");
                    return "layout";
                }

                cliente.setId(id);
                clienteRepository.save(cliente);
                redirectAttrs.addFlashAttribute("success", "Cliente actualizado con éxito.");
            } else {
                model.addAttribute("error", "Cliente no encontrado.");
                model.addAttribute("title", "Editar Cliente - Sistema de Ventas");
                model.addAttribute("currentPage", "clientes");
                model.addAttribute("contentTemplate", "Clientes/editarCliente");
                return "layout";
            }

            return "redirect:/clientes";
        }


        @GetMapping("/eliminar/{id}")
        public String eliminarCliente(@PathVariable Integer id, RedirectAttributes redirectAttrs) {
            Optional<Cliente> clienteOpt = clienteRepository.findById(id);
            if (clienteOpt.isPresent()) {
                long countVentas = ventaRepository.countByClienteId(id);
                if (countVentas > 0) {
                    redirectAttrs.addFlashAttribute("error", "No se puede eliminar el cliente porque tiene ventas asociadas.");
                } else {
                    clienteRepository.deleteById(id);
                    redirectAttrs.addFlashAttribute("success", "Cliente eliminado con éxito.");
                }
            } else {
                redirectAttrs.addFlashAttribute("error", "Cliente no encontrado.");
            }
            return "redirect:/clientes";
        }


        @GetMapping("/buscar")
        public String buscarClientes(
                @RequestParam(value = "nombre", required = false) String nombre,
                @RequestParam(value = "telefono", required = false) String telefono,
                @RequestParam(value = "dni", required = false) String dni,
                @RequestParam(defaultValue = "0") int page,
                @RequestParam(defaultValue = "10") int size,
                Model model) {

            Pageable pageable = PageRequest.of(page, size);
            Page<Cliente> clientesPage;

            if (nombre != null && !nombre.isEmpty()) {
                clientesPage = clienteRepository.findByNombreContainingIgnoreCase(nombre, pageable);
            } else if (telefono != null && !telefono.isEmpty()) {
                clientesPage = clienteRepository.findByTelefonoContaining(telefono, pageable);
            } else if (dni != null && !dni.isEmpty()) {
                clientesPage = clienteRepository.findByDniContaining(dni, pageable);
            } else {
                clientesPage = clienteRepository.findAll(pageable); // Devuelve todos los clientes si no hay filtros
            }

            model.addAttribute("clientes", clientesPage.getContent());
            model.addAttribute("currentPage", clientesPage.getNumber());
            model.addAttribute("totalPages", clientesPage.getTotalPages());
            model.addAttribute("totalItems", clientesPage.getTotalElements());
            model.addAttribute("size", size);
            model.addAttribute("title", "Clientes - Sistema de Ventas");
            model.addAttribute("currentPageName", "clientes");
            model.addAttribute("contentTemplate", "Clientes/clientes");
            return "layout";
        }
    }
