package com.angel.springboot.app.springboot_intellij.repositorios;


import com.angel.springboot.app.springboot_intellij.entidades.Venta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface VentaRepository extends JpaRepository<Venta, Integer> {

    @Query("SELECT v FROM Venta v LEFT JOIN FETCH v.detalles WHERE v.id = :id")
    Optional<Venta> findByIdWithDetalles(@Param("id") Integer id);

    @Query("SELECT v FROM Venta v LEFT JOIN FETCH v.detalles")
    Page<Venta> findAllWithDetalles(Pageable pageable);

    Page<Venta> findByFechaBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin, Pageable pageable);

    long countByClienteId(Integer clienteId);

}
