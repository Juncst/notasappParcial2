-- Crear la base de datos si todavía no existe
SELECT 'CREATE DATABASE notasapp'
WHERE NOT EXISTS (
    SELECT 1 FROM pg_database WHERE datname = 'notasapp'
)
\gexec

-- Seleccionar la base de datos
\connect notasapp

-- Habilitar funciones para proteger contraseñas
CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- Tabla de usuarios
CREATE TABLE IF NOT EXISTS usuarios (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    email VARCHAR(150) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabla de notas
CREATE TABLE IF NOT EXISTS notas (
    id SERIAL PRIMARY KEY,
    usuario_id INTEGER NOT NULL,
    titulo VARCHAR(200) NOT NULL,
    contenido TEXT NOT NULL,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
        ON DELETE CASCADE
);

-- Las cuentas se crean con backend/crearUsuario.js y credenciales del entorno.
-- No se insertan usuarios ni contraseñas predeterminados.

-- Verificar usuarios registrados
SELECT id, nombre, email, fecha_creacion
FROM usuarios
ORDER BY id;