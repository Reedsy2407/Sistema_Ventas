package com.angel.springboot.app.springboot_intellij.servicios;

import com.angel.springboot.app.springboot_intellij.entidades.DetalleVenta;
import com.angel.springboot.app.springboot_intellij.entidades.Venta;
import com.angel.springboot.app.springboot_intellij.repositorios.VentaRepository;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Service
public class ReporteService {

    @Autowired
    private VentaRepository ventaRepository;

    public JasperPrint generarReporteVentas() throws Exception {
        // Cambiar la ruta de acceso al archivo para que funcione correctamente desde resources
        InputStream fileStream = getClass().getResourceAsStream("/reporte_ventas.jrxml");
        if (fileStream == null) {
            throw new FileNotFoundException("El archivo de reporte no se encuentra en el directorio 'resources'.");
        }

        try {
            // Compilar el reporte desde el archivo en resources
            JasperReport jasperReport = JasperCompileManager.compileReport(fileStream);

            // Obtener todas las ventas
            List<Venta> ventas = ventaRepository.findAll();
            if (ventas.isEmpty()) {
                throw new IllegalStateException("No hay ventas registradas para generar el reporte.");
            }

            // Generar los detalles de ventas
            List<DetalleVenta> detalleVentas = new ArrayList<>();
            for (Venta venta : ventas) {
                if (venta.getDetalles() != null && !venta.getDetalles().isEmpty()) {
                    detalleVentas.addAll(venta.getDetalles());
                }
            }

            // Crear las fuentes de datos
            JRBeanCollectionDataSource ventasDataSource = new JRBeanCollectionDataSource(ventas);
            JRBeanCollectionDataSource detallesDataSource = new JRBeanCollectionDataSource(detalleVentas);

            // Mapear parámetros
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("ventasData", ventasDataSource);
            parametros.put("detalleVentasData", detallesDataSource);

            // Llenar el reporte con datos y parámetros
            return JasperFillManager.fillReport(jasperReport, parametros, new JREmptyDataSource());
        } catch (Exception e) {
            throw new RuntimeException("Error generando el reporte: " + e.getMessage(), e);
        }
    }
}

