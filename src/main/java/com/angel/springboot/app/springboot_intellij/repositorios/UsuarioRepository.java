package com.angel.springboot.app.springboot_intellij.repositorios;

import com.angel.springboot.app.springboot_intellij.entidades.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    Usuario findByUsername(String username);

}
