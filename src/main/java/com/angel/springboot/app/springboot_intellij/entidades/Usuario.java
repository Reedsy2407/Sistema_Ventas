package com.angel.springboot.app.springboot_intellij.entidades;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "usuarios")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id; // Cambiado a int

    private String username;
    private String password;

    @ManyToOne
    @JoinColumn(name = "rol_id")
    private Rol rol;
}
