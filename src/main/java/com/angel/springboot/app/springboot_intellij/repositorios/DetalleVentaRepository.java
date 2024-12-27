package com.angel.springboot.app.springboot_intellij.repositorios;


import com.angel.springboot.app.springboot_intellij.entidades.DetalleVenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DetalleVentaRepository extends JpaRepository<DetalleVenta, Integer> {

    List<DetalleVenta> findByProductoId(Integer productoId);
    long countByProductoId(Integer productoId);
}
