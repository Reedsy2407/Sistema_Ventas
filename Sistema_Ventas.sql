CREATE DATABASE IF NOT EXISTS Sistema_Ventas;
USE Sistema_Ventas;

-- Tabla de roles
CREATE TABLE roles (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL UNIQUE
);

-- Tabla de usuarios
CREATE TABLE usuarios (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL, -- Texto plano para pruebas
    rol_id INT NOT NULL,
    FOREIGN KEY (rol_id) REFERENCES roles(id) ON DELETE CASCADE
);

-- Insertar roles de prueba
INSERT INTO roles (nombre) VALUES ('ADMIN'), ('VENDEDOR');

-- Insertar usuario de prueba
INSERT INTO usuarios (username, password, rol_id) VALUES ('Jorgito Admin', '12345', 1), ('Pepito Vendedor', '1234', 2);

CREATE TABLE categoria (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL
);

-- Tabla de productos
CREATE TABLE productos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    descripcion TEXT,
    precio DECIMAL(10, 2) NOT NULL,
    stock INT NOT NULL,
    categoria_id INT NOT NULL,
    FOREIGN KEY (categoria_id) REFERENCES categoria(id) ON DELETE CASCADE
);

-- Tabla de clientes
CREATE TABLE clientes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(80) NOT NULL, 
    email VARCHAR(100) UNIQUE NOT NULL,
    telefono VARCHAR(9) NOT NULL, 
	dni varchar(12) not null,
    direccion VARCHAR(120) NOT NULL
);


-- Tabla de ventas
CREATE TABLE ventas (
    id INT AUTO_INCREMENT PRIMARY KEY,
    cliente_id INT NOT NULL, 
    fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    total DECIMAL(10, 2),
    FOREIGN KEY (cliente_id) REFERENCES clientes(id) ON DELETE CASCADE 
);

-- Tabla de detalles de ventas
CREATE TABLE detalles_venta (
    id INT AUTO_INCREMENT PRIMARY KEY,
    venta_id INT NOT NULL, 
    producto_id INT , 
    cantidad INT NOT NULL,
    precio_unitario DECIMAL(10, 2) NOT NULL,
    subtotal DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (venta_id) REFERENCES ventas(id) ON DELETE CASCADE,
    FOREIGN KEY (producto_id) REFERENCES productos(id) ON DELETE SET NULL
);


INSERT INTO categoria (nombre) VALUES
('Lacteos'),
('Carnes'),
('Frutas');


-- Insertar productos
INSERT INTO productos (nombre, categoria_id, descripcion, precio, stock) VALUES
('Producto A',1, 'Descripción del producto A', 10.99, 50),
('Producto B',2, 'Descripción del producto B', 25.50, 30),
('Producto C',3, 'Descripción del producto C', 7.75, 100);

-- Insertar clientes
INSERT INTO clientes (nombre, email, telefono, dni, direccion) VALUES
('Juan Pérez', 'juan.perez@example.com', '123456789','12345678', 'Calle Falsa 123'),
('Ana Gómez', 'ana.gomez@example.com', '987654321','46374837', 'Avenida Siempreviva 456');

-- Insertar ventas y detalles de ventas
INSERT INTO ventas (cliente_id, total) VALUES (1, 54.97);
INSERT INTO detalles_venta (venta_id, producto_id, cantidad, precio_unitario, subtotal) VALUES
(1, 1, 2, 10.99, 21.98),
(1, 2, 1, 25.50, 25.50),
(1, 3, 1, 7.75, 7.75);

select * from clientes;
select * from categoria;
select * from productos;
select * from ventas;
select * from detalles_venta;
select * from usuarios;
select * from roles;

