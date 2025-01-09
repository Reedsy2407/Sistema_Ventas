package com.angel.springboot.app.springboot_intellij.servicios;

import com.angel.springboot.app.springboot_intellij.entidades.Venta;
import com.angel.springboot.app.springboot_intellij.repositorios.VentaRepository;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanArrayDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReporteService {

    @Autowired
    private VentaRepository ventaRepository;

    public byte[] generarReporteVentasPDF() throws Exception {
        // Cargar el archivo compilado Jasper desde resources/reports
        InputStream reporteStream = getClass().getResourceAsStream("/reports/Reporte_Ventas.jasper");
        if (reporteStream == null) {
            throw new FileNotFoundException("El archivo 'Reporte_Ventas.jasper' no se encuentra en 'resources/reports'.");
        }

        try {
            // Obtener todas las ventas
            List<Venta> ventas = ventaRepository.findAll();
            if (ventas.isEmpty()) {
                throw new IllegalStateException("No hay ventas registradas para generar el reporte.");
            }

            // Crear un JRBeanArrayDataSource con las ventas
            JRBeanArrayDataSource ds = new JRBeanArrayDataSource(ventas.toArray());

            // Cargar las imágenes dinámicas
            InputStream logoStream = getClass().getResourceAsStream("/static/images/rocketCompany.jpg");
            InputStream checkStream = getClass().getResourceAsStream("/static/images/check.jpg");

            if (logoStream == null || checkStream == null) {
                throw new FileNotFoundException("No se encontraron las imágenes necesarias para el reporte.");
            }

            // Parámetros del reporte
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("ds", ds);
            parametros.put("logoEmpresa", logoStream);  // Mantén la carga como InputStream
            parametros.put("imagenAlternativa", checkStream);  // Mantén la carga como InputStream

            // Llenar el reporte con datos
            JasperPrint jasperPrint = JasperFillManager.fillReport(reporteStream, parametros, new JREmptyDataSource());

            // Exportar a PDF
            return JasperExportManager.exportReportToPdf(jasperPrint);
        } catch (Exception e) {
            throw new RuntimeException("Error generando el reporte: " + e.getMessage(), e);
        }
    }
}