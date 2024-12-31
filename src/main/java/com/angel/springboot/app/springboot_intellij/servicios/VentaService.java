package com.angel.springboot.app.springboot_intellij.servicios;

import com.angel.springboot.app.springboot_intellij.entidades.DetalleVenta;
import com.angel.springboot.app.springboot_intellij.entidades.Producto;
import com.angel.springboot.app.springboot_intellij.entidades.Venta;
import com.angel.springboot.app.springboot_intellij.repositorios.ClienteRepository;
import com.angel.springboot.app.springboot_intellij.repositorios.ProductoRepository;
import com.angel.springboot.app.springboot_intellij.repositorios.VentaRepository;
import com.angel.springboot.app.springboot_intellij.repositorios.DetalleVentaRepository;
import jakarta.transaction.Transactional;
import net.sf.jasperreports.engine.JasperPrint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


import java.sql.Timestamp;
import java.util.*;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class VentaService {


    @Autowired
    private VentaRepository ventaRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private DetalleVentaRepository detalleVentaRepository;

    @Autowired
    private ReporteService reporteService;

    @Transactional
    public void crearVenta(Venta venta) {
        if (!clienteRepository.existsById(venta.getCliente().getId())) {
            throw new IllegalArgumentException("Cliente no encontrado.");
        }

        venta.setFecha(Timestamp.valueOf(LocalDateTime.now()));

        double total = 0.0;
        StringBuilder erroresStock = new StringBuilder();

        for (DetalleVenta detalle : venta.getDetalles()) {
            Optional<Producto> productoOpt = productoRepository.findById(detalle.getProducto().getId());

            String nombreProducto = productoOpt.map(Producto::getNombre).orElse("producto desconocido");

            if (productoOpt.isEmpty() || productoOpt.get().getStock() < detalle.getCantidad()) {
                erroresStock.append("Stock insuficiente para ").append(nombreProducto).append(". ");
                continue;
            }

            Producto producto = productoOpt.get();
            detalle.setPrecioUnitario(producto.getPrecio());
            detalle.setSubtotal(detalle.getPrecioUnitario() * detalle.getCantidad());
            detalle.setVenta(venta);
            total += detalle.getSubtotal();

            producto.setStock(producto.getStock() - detalle.getCantidad());
            productoRepository.save(producto);
        }

        if (!erroresStock.isEmpty()) {
            throw new IllegalArgumentException(erroresStock.toString().trim());
        }

        venta.setTotal(total);
        ventaRepository.save(venta);
    }

    public Page<Venta> obtenerVentasPaginadas(Pageable pageable) {
        Page<Venta> ventas = ventaRepository.findAllWithDetalles(pageable);
        ventas.forEach(venta -> {
            if (venta.getDetalles() == null) {
                venta.setDetalles(new ArrayList<>());
            }
        });
        return ventas;
    }

    public Venta obtenerVentaPorId(Integer id) {
        Venta venta = ventaRepository.findByIdWithDetalles(id)
                .orElseThrow(() -> new IllegalArgumentException("La venta no existe."));
        if (venta.getDetalles() == null) {
            venta.setDetalles(new ArrayList<>());
        }
        return venta;
    }

    @Transactional
    public void editarVenta(Integer id, Venta ventaActualizada) {
        Venta ventaExistente = obtenerVentaPorId(id);

        // Validar el cliente
        if (!clienteRepository.existsById(ventaActualizada.getCliente().getId())) {
            throw new IllegalArgumentException("Cliente no encontrado.");
        }

        ventaExistente.setCliente(ventaActualizada.getCliente());

        double total = 0.0;
        AtomicInteger tempIdGenerator = new AtomicInteger(-1);

        Map<Integer, DetalleVenta> detallesActualizados = ventaActualizada.getDetalles().stream()
                .collect(Collectors.toMap(
                        detalle -> detalle.getId() != null ? detalle.getId() : tempIdGenerator.getAndDecrement(),
                        detalle -> detalle
                ));

        // Iterar sobre los detalles existentes
        List<DetalleVenta> detallesParaEliminar = new ArrayList<>();
        for (DetalleVenta detalleExistente : ventaExistente.getDetalles()) {
            DetalleVenta detalleActualizado = detallesActualizados.get(detalleExistente.getId());
            Producto producto = productoRepository.findById(detalleExistente.getProducto().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado."));

            // Restaurar el stock original
            producto.setStock(producto.getStock() + detalleExistente.getCantidad());

            if (detalleActualizado == null) {
                detallesParaEliminar.add(detalleExistente); // Marcar para eliminar
            } else {
                // Validar stock para la nueva cantidad
                if (producto.getStock() < detalleActualizado.getCantidad()) {
                    throw new IllegalArgumentException("Stock insuficiente para el producto: " + producto.getNombre());
                }

                // Actualizar detalle y ajustar stock
                producto.setStock(producto.getStock() - detalleActualizado.getCantidad());
                detalleExistente.setCantidad(detalleActualizado.getCantidad());
                detalleExistente.setPrecioUnitario(producto.getPrecio());
                detalleExistente.setSubtotal(detalleExistente.getPrecioUnitario() * detalleExistente.getCantidad());
                total += detalleExistente.getSubtotal();
                detallesActualizados.remove(detalleExistente.getId());
                productoRepository.save(producto);
            }
        }

        // Eliminar los detalles marcados
        ventaExistente.getDetalles().removeAll(detallesParaEliminar);

        // Agregar los nuevos detalles
        for (DetalleVenta nuevoDetalle : detallesActualizados.values()) {
            Producto producto = productoRepository.findById(nuevoDetalle.getProducto().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado."));

            // Validar stock para la nueva cantidad
            if (producto.getStock() < nuevoDetalle.getCantidad()) {
                throw new IllegalArgumentException("Stock insuficiente para el producto: " + producto.getNombre());
            }

            // Ajustar stock
            producto.setStock(producto.getStock() - nuevoDetalle.getCantidad());

            // Guardar detalle
            nuevoDetalle.setPrecioUnitario(producto.getPrecio());
            nuevoDetalle.setSubtotal(nuevoDetalle.getPrecioUnitario() * nuevoDetalle.getCantidad());
            nuevoDetalle.setVenta(ventaExistente);
            ventaExistente.getDetalles().add(nuevoDetalle);
            total += nuevoDetalle.getSubtotal();
            productoRepository.save(producto);
        }

        ventaExistente.setTotal(total);
        ventaRepository.save(ventaExistente);
    }

    @Transactional
    public void eliminarVenta(Integer id) {
        Venta venta = obtenerVentaPorId(id);
        if (venta == null) {
            throw new IllegalArgumentException("Venta no encontrada.");
        }

        // Restaurar el stock de cada producto en los detalles de la venta
        for (DetalleVenta detalle : venta.getDetalles()) {
            Producto producto = detalle.getProducto();
            if (producto != null) {
                producto.setStock(producto.getStock() + detalle.getCantidad());
                productoRepository.save(producto);
            }
        }

        // Eliminar la venta (se eliminarán automáticamente los detalles de venta por ON DELETE CASCADE)
        ventaRepository.delete(venta);
    }

    @Transactional
    public void actualizarPreciosVentaPorProducto(Producto producto) {
        List<DetalleVenta> detalles = detalleVentaRepository.findByProductoId(producto.getId());

        for (DetalleVenta detalle : detalles) {
            detalle.setPrecioUnitario(producto.getPrecio());
            detalle.setSubtotal(detalle.getCantidad() * producto.getPrecio());
            detalleVentaRepository.save(detalle);
        }

        // Actualizar el total de las ventas relacionadas
        Set<Integer> ventasIds = detalles.stream()
                .map(detalle -> detalle.getVenta().getId())
                .collect(Collectors.toSet());

        for (Integer ventaId : ventasIds) {
            Venta venta = ventaRepository.findByIdWithDetalles(ventaId)
                    .orElseThrow(() -> new IllegalArgumentException("Venta no encontrada."));
            double total = venta.getDetalles().stream()
                    .mapToDouble(DetalleVenta::getSubtotal)
                    .sum();
            venta.setTotal(total);
            ventaRepository.save(venta);
        }
    }

    public Page<Venta> obtenerVentasPorRangoDeFechas(LocalDateTime fechaInicio, LocalDateTime fechaFin, Pageable pageable) {
        return ventaRepository.findByFechaBetween(fechaInicio, fechaFin, pageable);
    }

    public JasperPrint generarReporteVenta() throws Exception {
        return reporteService.generarReporteVentas();
    }
}