package com.angel.springboot.app.springboot_intellij.entidades;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name = "productos")
@Data
@NoArgsConstructor
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "El nombre es obligatorio.")
    @Size(max = 80, message = "El nombre no puede exceder los 80 caracteres.")
    @Pattern(regexp = "^[A-Za-zÁÉÍÓÚáéíóúÑñ ]+$", message = "El nombre solo puede contener letras y espacios.")
    private String nombre;

    @NotBlank(message = "El nombre es obligatorio.")
    @Size(max = 200, message = "El nombre no puede exceder los 80 caracteres.")
    @Pattern(regexp = "^[A-Za-zÁÉÍÓÚáéíóúÑñ ]+$", message = "El nombre solo puede contener letras y espacios.")
    private String descripcion;

    @Min(value = 1, message = "El precio debe ser al menos 1.")
    @Max(value = 9999, message = "El precio no puede exceder los 9999.")
    private double precio;

    @Min(value = 0, message = "El stock debe ser al menos 1.")
    @Max(value = 500, message = "El stock no puede exceder los 500.")
    private Integer stock;
}
