package com.angel.springboot.app.springboot_intellij.repositorios;


import com.angel.springboot.app.springboot_intellij.entidades.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RolRepository extends JpaRepository<Rol, Integer> {
}
