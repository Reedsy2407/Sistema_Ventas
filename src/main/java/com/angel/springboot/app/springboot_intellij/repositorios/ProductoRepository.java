package com.angel.springboot.app.springboot_intellij.repositorios;


import com.angel.springboot.app.springboot_intellij.entidades.Producto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductoRepository extends JpaRepository<Producto,Integer> {


        Optional<Producto> findByNombre(String nombre);
        Page<Producto> findByNombreContainingIgnoreCase(String nombre, Pageable pageable);
        Page<Producto> findByStockLessThan(int stockMinimo, Pageable pageable);
}
