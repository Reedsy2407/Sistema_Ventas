package com.angel.springboot.app.springboot_intellij.repositorios;


import com.angel.springboot.app.springboot_intellij.entidades.Cliente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Integer> {

    // El uso de Optional sirve para cuando se espera un único resultado
    // Esto permite controlar la ausencia de datos  de forma más limpia

    Optional<Cliente> findByEmail(String email);
    Optional<Cliente> findByTelefono(String telefono);
    Optional<Cliente> findByDni(String dni);


    // Métodos personalizados para buscar por nombre o teléfono
    Page<Cliente> findByNombreContainingIgnoreCase(String nombre, Pageable pageable);
    Page<Cliente> findByTelefonoContaining(String telefono, Pageable pageable);
    Page<Cliente> findByDniContaining(String dni, Pageable pageable);
    //El List espera varios resultados cuando se quieren devolver todas las coincidencias
    // En búsquedas con palabras clave como Containing siempre usar una List ya que se esperan coincidencias multiples
}
