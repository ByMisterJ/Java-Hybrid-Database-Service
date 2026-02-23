-- Conectarse a la base de datos postgres
\c postgres;

SELECT 'CREATE DATABASE tunombre'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'tunombre')\gexec

\c tunombre;

-- Borrar tablas si existen
DROP TABLE IF EXISTS usuarios, contenidos_reproducidos;

-- Crear tabla Usuarios
CREATE TABLE IF NOT EXISTS usuarios (
    id_usuario SERIAL PRIMARY KEY,
    nombre_completo VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE
);

-- Crear tabla Contenidos Reproducidos
CREATE TABLE IF NOT EXISTS contenidos_reproducidos (
    id SERIAL PRIMARY KEY,
    id_usuario INT NOT NULL,
    contenido VARCHAR(100) NOT NULL,
    fecha TIMESTAMP NOT NULL,
    FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario)
);