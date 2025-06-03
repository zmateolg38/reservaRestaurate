-- Tabla de usuarios
CREATE TABLE usuarios (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    nombre VARCHAR(255) NOT NULL,
    telefono VARCHAR(20) NOT NULL,
    tipo_usuario VARCHAR(20) NOT NULL CHECK (tipo_usuario IN ('CLIENTE', 'ADMINISTRADOR', 'MESERO')),
    fecha_registro TIMESTAMP,
    activo BOOLEAN DEFAULT TRUE
);

CREATE INDEX idx_email ON usuarios(email);
CREATE INDEX idx_tipo_usuario ON usuarios(tipo_usuario);
CREATE INDEX idx_activo ON usuarios(activo);

-- Tabla de mesas
CREATE TABLE mesas (
    id BIGSERIAL PRIMARY KEY,
    numero VARCHAR(10) NOT NULL UNIQUE,
    capacidad INT NOT NULL,
    estado VARCHAR(20) NOT NULL DEFAULT 'DISPONIBLE' CHECK (estado IN ('DISPONIBLE', 'OCUPADA', 'RESERVADA', 'FUERA_DE_SERVICIO')),
    activa BOOLEAN DEFAULT TRUE
);

CREATE INDEX idx_numero ON mesas(numero);
CREATE INDEX idx_capacidad ON mesas(capacidad);
CREATE INDEX idx_estado ON mesas(estado);
CREATE INDEX idx_activa ON mesas(activa);

-- Tabla de reservas
CREATE TABLE reservas (
    id BIGSERIAL PRIMARY KEY,
    cliente_id BIGINT NOT NULL,
    mesa_id BIGINT NOT NULL,
    fecha_inicio_reserva TIMESTAMP NOT NULL,
    fecha_fin_reserva TIMESTAMP NOT NULL,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    numero_personas INT NOT NULL,
    estado VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE' CHECK (estado IN ('PENDIENTE', 'CONFIRMADA', 'CANCELADA', 'COMPLETADA', 'NO_ASISTIO')),
    comentarios TEXT,
    recordatorio_enviado BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (cliente_id) REFERENCES usuarios(id) ON DELETE CASCADE,
    FOREIGN KEY (mesa_id) REFERENCES mesas(id) ON DELETE CASCADE
);

CREATE INDEX idx_cliente_id ON reservas(cliente_id);
CREATE INDEX idx_mesa_id ON reservas(mesa_id);
CREATE INDEX idx_fecha_reserva ON reservas(fecha_reserva);
CREATE INDEX idx_estado ON reservas(estado);
CREATE INDEX idx_recordatorio_enviado ON reservas(recordatorio_enviado);

-- Tabla de asignaciones de mesas
CREATE TABLE asignaciones_mesas (
    id BIGSERIAL PRIMARY KEY,
    mesero_id BIGINT NOT NULL,
    mesa_id BIGINT NOT NULL,
    fecha DATE NOT NULL,
    hora_inicio TIME NOT NULL,
    hora_fin TIME NOT NULL,
    estado VARCHAR(20) DEFAULT 'ACTIVA' CHECK (estado IN ('ACTIVA', 'COMPLETADA', 'CANCELADA')),
    FOREIGN KEY (mesero_id) REFERENCES usuarios(id) ON DELETE CASCADE,
    FOREIGN KEY (mesa_id) REFERENCES mesas(id) ON DELETE CASCADE,
    UNIQUE (mesa_id, fecha, hora_inicio, hora_fin)
);

CREATE INDEX idx_mesero_id ON asignaciones_mesas(mesero_id);
CREATE INDEX idx_mesa_id ON asignaciones_mesas(mesa_id);
CREATE INDEX idx_fecha ON asignaciones_mesas(fecha);
CREATE INDEX idx_estado ON asignaciones_mesas(estado);

-- Tabla de configuración de horarios
CREATE TABLE configuracion_horarios (
    id BIGSERIAL PRIMARY KEY,
    dia_semana VARCHAR(10) NOT NULL CHECK (dia_semana IN ('LUNES', 'MARTES', 'MIERCOLES', 'JUEVES', 'VIERNES', 'SABADO', 'DOMINGO')),
    hora_inicio TIME NOT NULL,
    hora_fin TIME NOT NULL,
    mesas_disponibles INT NOT NULL,
    duracion_reserva_minutos INT NOT NULL DEFAULT 120,
    activo BOOLEAN DEFAULT TRUE
);

CREATE INDEX idx_dia_semana ON configuracion_horarios(dia_semana);
CREATE INDEX idx_activo ON configuracion_horarios(activo);