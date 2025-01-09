package com.angel.springboot.app.springboot_intellij.controladores;
import com.angel.springboot.app.springboot_intellij.entidades.Venta;
import com.angel.springboot.app.springboot_intellij.repositorios.ClienteRepository;
import com.angel.springboot.app.springboot_intellij.repositorios.ProductoRepository;
import com.angel.springboot.app.springboot_intellij.servicios.VentaService;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/ventas")
public class VentaController {

    @Autowired
    private VentaService ventaService;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @GetMapping
    public String listarVentas(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Venta> ventasPage = ventaService.obtenerVentasPaginadas(pageable);

        // Mapear ventas para incluir el DNI
        List<Venta> ventasConDni = ventasPage.getContent().stream()
                .peek(venta -> venta.getCliente().setDni(venta.getCliente().getDni()))
                .collect(Collectors.toList());

        model.addAttribute("ventas", ventasConDni);
        model.addAttribute("currentPage", ventasPage.getNumber());
        model.addAttribute("totalPages", ventasPage.getTotalPages());
        model.addAttribute("totalItems", ventasPage.getTotalElements());

        model.addAttribute("title", "Ventas - Sistema de Ventas");
        model.addAttribute("currentPageName", "ventas");
        model.addAttribute("contentTemplate", "Ventas/ventas");
        return "layout";
    }


    @GetMapping("/agregarVenta")
    public String mostrarFormularioAgregarVenta(Model model) {
        model.addAttribute("venta", new Venta());
        model.addAttribute("clientes", clienteRepository.findAll());
        model.addAttribute("productos", productoRepository.findAll());
        model.addAttribute("title", "Añadir Venta - Sistema de Ventas");
        model.addAttribute("currentPage", "ventas");
        model.addAttribute("contentTemplate", "Ventas/agregarVenta");
        return "layout";
    }

    @PostMapping("/agregarVenta")
    public String crearVenta(@ModelAttribute @Validated Venta venta, Model model) {
        try {
            ventaService.crearVenta(venta);
            return "redirect:/ventas";
        } catch (IllegalArgumentException e) {
            // Agregar el mensaje de error al modelo
            model.addAttribute("error", e.getMessage());
            model.addAttribute("venta", venta); // Mantener los datos de la venta
            model.addAttribute("clientes", clienteRepository.findAll());
            model.addAttribute("productos", productoRepository.findAll());

            // Configurar el layout
            model.addAttribute("title", "Añadir Venta - Sistema de Ventas");
            model.addAttribute("currentPage", "ventas");
            model.addAttribute("contentTemplate", "Ventas/agregarVenta");
            return "layout";
        }
    }


    @GetMapping("/editarVenta/{id}")
    public String mostrarFormularioEditarVenta(@PathVariable("id") Integer id, Model model) {
        Venta venta = ventaService.obtenerVentaPorId(id);
        if (venta == null) {
            return "redirect:/ventas";
        }
        model.addAttribute("venta", venta);
        model.addAttribute("clientes", clienteRepository.findAll());
        model.addAttribute("productos", productoRepository.findAll());
        model.addAttribute("title", "Editar Venta - Sistema de Ventas");
        model.addAttribute("currentPage", "ventas");
        model.addAttribute("contentTemplate", "Ventas/editarVenta");
        return "layout";
    }

    @PostMapping("/editarVenta/{id}")
    public String editarVenta(@PathVariable("id") Integer id, @ModelAttribute @Validated Venta venta, Model model) {
        try {
            ventaService.editarVenta(id, venta);
            return "redirect:/ventas";
        } catch (IllegalArgumentException e) {
            // Agregar el mensaje de error al modelo
            model.addAttribute("error", e.getMessage());
            model.addAttribute("venta", venta); // Mantener los datos de la venta
            model.addAttribute("clientes", clienteRepository.findAll());
            model.addAttribute("productos", productoRepository.findAll());

            // Configurar el layout
            model.addAttribute("title", "Editar Venta - Sistema de Ventas");
            model.addAttribute("currentPage", "ventas");
            model.addAttribute("contentTemplate", "Ventas/editarVenta");
            return "layout";
        }
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarVenta(@PathVariable Integer id, RedirectAttributes redirectAttrs) {
        try {
            Venta venta = ventaService.obtenerVentaPorId(id);
            if (venta == null) {
                redirectAttrs.addFlashAttribute("error", "Venta no encontrada.");
            } else {
                ventaService.eliminarVenta(id);
                redirectAttrs.addFlashAttribute("success", "Venta eliminada con éxito.");
            }
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("error", "Error al eliminar la venta: " + e.getMessage());
        }
        return "redirect:/ventas";
    }

    @GetMapping("/filtrarPorFecha")
    public String filtrarVentasPorFecha(
            @RequestParam("fechaInicio") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam("fechaFin") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {
        Pageable pageable = PageRequest.of(page, size); // Crear el objeto Pageable
        Page<Venta> ventasPage = ventaService.obtenerVentasPorRangoDeFechas(fechaInicio, fechaFin, pageable);

        model.addAttribute("ventas", ventasPage.getContent());
        model.addAttribute("currentPage", ventasPage.getNumber());
        model.addAttribute("totalPages", ventasPage.getTotalPages());
        model.addAttribute("fechaInicio", fechaInicio);
        model.addAttribute("fechaFin", fechaFin);
        model.addAttribute("title", "Ventas Filtradas por Fecha");
        model.addAttribute("contentTemplate", "Ventas/ventas");

        return "layout";
    }

    @GetMapping("/reporte/pdf")
    public ResponseEntity<byte[]> generarReportePDF() {
        try {
            byte[] pdf = ventaService.generarReporteVentaPDF();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "reporte_ventas.pdf");

            return new ResponseEntity<>(pdf, headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Error al generar el reporte: " + e.getMessage()).getBytes());
        }
    }


    @GetMapping("/limpiarFiltros")
    public String limpiarFiltros() {
        return "redirect:/ventas";
    }
}
