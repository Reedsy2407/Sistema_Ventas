package com.angel.springboot.app.springboot_intellij.entidades;

import jakarta.persistence.*;
import lombok.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "clientes")
@Data
@NoArgsConstructor
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "El nombre es obligatorio.")
    @Size(max = 80, message = "El nombre no puede exceder los 80 caracteres.")
    @Pattern(regexp = "^[A-Za-zÁÉÍÓÚáéíóúÑñ ]+$", message = "El nombre solo puede contener letras y espacios.")
    private String nombre;

    @NotBlank(message = "El email es obligatorio.")
    @Email(message = "Debe ser un email válido.")
    private String email;

    @NotBlank(message = "El teléfono es obligatorio.")
    @Pattern(regexp = "^[0-9]{9}$", message = "El teléfono debe tener exactamente 9 dígitos.")
    private String telefono;

    @NotBlank(message = "El dni es obligatorio.")
    @Pattern(regexp = "^[0-8]{8}$", message = "El dni debe tener exactamente 8 dígitos.")
    private String dni;


    @NotBlank(message = "La dirección es obligatoria.")
    @Size(max = 120, message = "La dirección no puede exceder los 120 caracteres.")
    @Pattern(regexp = "^[A-Za-zÁÉÍÓÚáéíóúÑñ0-9 ]+$", message = "La dirección solo puede contener letras, números y espacios.")
    private String direccion;
}
