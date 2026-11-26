SET NAMES utf8mb4;

-- ---------------------------------------------------------------------
-- Tablas de catálogo
-- ---------------------------------------------------------------------

CREATE TABLE IF NOT EXISTS Usuario (
    id_usuario      INT             NOT NULL AUTO_INCREMENT,
    uNombre         VARCHAR(255)    NOT NULL,
    correo          VARCHAR(320)    NOT NULL,
    fecha_registro  DATE            NOT NULL DEFAULT (CURRENT_DATE),

    PRIMARY KEY (id_usuario),
    CONSTRAINT uq_usuario_correo UNIQUE (correo)
);

CREATE TABLE IF NOT EXISTS Autor (
    id_autor        INT             NOT NULL AUTO_INCREMENT,
    aNombre         VARCHAR(255)    NOT NULL,
    nacionalidad    VARCHAR(100)    NOT NULL,

    PRIMARY KEY (id_autor)
);

CREATE TABLE IF NOT EXISTS Genero (
    id_genero       INT             NOT NULL AUTO_INCREMENT,
    clasificacion   VARCHAR(150)    NOT NULL,

    PRIMARY KEY (id_genero)
);

CREATE TABLE IF NOT EXISTS Saga (
    id_saga         INT             NOT NULL AUTO_INCREMENT,
    sNombre         VARCHAR(175)    NOT NULL,
    descripcion     VARCHAR(1000)   NOT NULL,

    PRIMARY KEY (id_saga)
);

CREATE TABLE IF NOT EXISTS Formato (
    id_formato      INT             NOT NULL AUTO_INCREMENT,
    tipo_formato    VARCHAR(175)    NOT NULL,

    PRIMARY KEY (id_formato)
);

-- ---------------------------------------------------------------------
-- Libro y Edicion (C1)
-- ---------------------------------------------------------------------

CREATE TABLE IF NOT EXISTS Libro (
    id_libro        INT             NOT NULL AUTO_INCREMENT,
    titulo          VARCHAR(500)    NOT NULL,
    id_saga         INT             NULL,
    orden_saga      INT             NULL,

    PRIMARY KEY (id_libro),

    CONSTRAINT ck_libro_orden_saga CHECK (orden_saga IS NULL OR orden_saga > 0),

    CONSTRAINT fk_libro_saga
        FOREIGN KEY (id_saga)
        REFERENCES Saga (id_saga)
        ON UPDATE CASCADE
        ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS Edicion (
    id_edicion        INT           NOT NULL AUTO_INCREMENT,
    id_libro          INT           NOT NULL,
    tipo              VARCHAR(325)  NOT NULL,
    editorial         VARCHAR(255)  NOT NULL,
    isbn              VARCHAR(20)   NOT NULL,
    tot_paginas       INT           NOT NULL,
    anno_publicacion  DATE          NOT NULL,

    PRIMARY KEY (id_edicion),

    CONSTRAINT uq_edicion_isbn UNIQUE (isbn),
    CONSTRAINT ck_edicion_paginas CHECK (tot_paginas > 0),

    CONSTRAINT fk_edicion_libro
        FOREIGN KEY (id_libro)
        REFERENCES Libro (id_libro)
        ON UPDATE CASCADE
        ON DELETE CASCADE
);

-- ---------------------------------------------------------------------
-- Relaciones N:M
-- ---------------------------------------------------------------------

CREATE TABLE IF NOT EXISTS Libro_Autor (
    id_libro        INT             NOT NULL,
    id_autor        INT             NOT NULL,
    rol             VARCHAR(250)    NOT NULL,

    PRIMARY KEY (id_libro, id_autor),

    CONSTRAINT fk_libro_autor_libro
        FOREIGN KEY (id_libro)
        REFERENCES Libro (id_libro)
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    CONSTRAINT fk_libro_autor_autor
        FOREIGN KEY (id_autor)
        REFERENCES Autor (id_autor)
        ON UPDATE CASCADE
        ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS Libro_Genero (
    id_libro        INT             NOT NULL,
    id_genero       INT             NOT NULL,

    PRIMARY KEY (id_libro, id_genero),

    CONSTRAINT fk_libro_genero_libro
        FOREIGN KEY (id_libro)
        REFERENCES Libro (id_libro)
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    CONSTRAINT fk_libro_genero_genero
        FOREIGN KEY (id_genero)
        REFERENCES Genero (id_genero)
        ON UPDATE CASCADE
        ON DELETE CASCADE
);

-- C3
CREATE TABLE IF NOT EXISTS Saga_Autor (
    id_saga         INT             NOT NULL,
    id_autor        INT             NOT NULL,

    PRIMARY KEY (id_saga, id_autor),

    CONSTRAINT fk_saga_autor_saga
        FOREIGN KEY (id_saga)
        REFERENCES Saga (id_saga)
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    CONSTRAINT fk_saga_autor_autor
        FOREIGN KEY (id_autor)
        REFERENCES Autor (id_autor)
        ON UPDATE CASCADE
        ON DELETE CASCADE
);

-- C4
CREATE TABLE IF NOT EXISTS Saga_Genero (
    id_saga         INT             NOT NULL,
    id_genero       INT             NOT NULL,

    PRIMARY KEY (id_saga, id_genero),

    CONSTRAINT fk_saga_genero_saga
        FOREIGN KEY (id_saga)
        REFERENCES Saga (id_saga)
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    CONSTRAINT fk_saga_genero_genero
        FOREIGN KEY (id_genero)
        REFERENCES Genero (id_genero)
        ON UPDATE CASCADE
        ON DELETE CASCADE
);

-- ---------------------------------------------------------------------
-- Actividad del usuario
-- ---------------------------------------------------------------------

-- C2: Lectura ahora referencia a Edicion
CREATE TABLE IF NOT EXISTS Lectura (
    id_lectura      INT             NOT NULL AUTO_INCREMENT,
    id_usuario      INT             NOT NULL,
    id_edicion      INT             NOT NULL,
    id_formato      INT             NOT NULL,
    fecha_inicio    DATE            NOT NULL,
    fecha_fin       DATE            NULL,
    estado          VARCHAR(150)    NOT NULL,

    PRIMARY KEY (id_lectura),

    CONSTRAINT ck_lectura_estado
        CHECK (estado IN ('por iniciar', 'en curso', 'terminada', 'abandonada')),
    CONSTRAINT ck_lectura_fechas
        CHECK (fecha_fin IS NULL OR fecha_fin >= fecha_inicio),

    CONSTRAINT fk_lectura_usuario
        FOREIGN KEY (id_usuario)
        REFERENCES Usuario (id_usuario)
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    CONSTRAINT fk_lectura_edicion
        FOREIGN KEY (id_edicion)
        REFERENCES Edicion (id_edicion)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,
    CONSTRAINT fk_lectura_formato
        FOREIGN KEY (id_formato)
        REFERENCES Formato (id_formato)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
);

-- C5: pag_inicio / pag_fin (páginas leídas = pag_fin - pag_inicio)
CREATE TABLE IF NOT EXISTS Sesion_Lectura (
    id_sesion       INT             NOT NULL AUTO_INCREMENT,
    id_lectura      INT             NOT NULL,
    pag_inicio      INT             NOT NULL,
    pag_fin         INT             NOT NULL,
    duracion        INT             NOT NULL,   -- minutos
    cap_actual      INT             NOT NULL,

    PRIMARY KEY (id_sesion),

    CONSTRAINT ck_sesion_paginas  CHECK (pag_inicio >= 0 AND pag_fin >= pag_inicio),
    CONSTRAINT ck_sesion_duracion CHECK (duracion > 0),

    CONSTRAINT fk_sesion_lectura
        FOREIGN KEY (id_lectura)
        REFERENCES Lectura (id_lectura)
        ON UPDATE CASCADE
        ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS Valoracion (
    id_valoracion   INT             NOT NULL AUTO_INCREMENT,
    id_lectura      INT             NOT NULL,
    puntuacion      INT             NOT NULL,
    resenna         VARCHAR(2000)   NOT NULL,
    fecha           DATE            NOT NULL DEFAULT (CURRENT_DATE),

    PRIMARY KEY (id_valoracion),

    CONSTRAINT ck_valoracion_puntuacion CHECK (puntuacion BETWEEN 1 AND 5),

    CONSTRAINT fk_valoracion_lectura
        FOREIGN KEY (id_lectura)
        REFERENCES Lectura (id_lectura)
        ON UPDATE CASCADE
        ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS Lista_Por_Leer (
    id_usuario      INT             NOT NULL,
    id_libro        INT             NOT NULL,
    prioridad       VARCHAR(125)    NOT NULL,
    fecha_agregado  DATE            NOT NULL DEFAULT (CURRENT_DATE),

    PRIMARY KEY (id_usuario, id_libro),

    CONSTRAINT ck_lista_prioridad CHECK (prioridad IN ('Alta', 'Media', 'Baja')),

    CONSTRAINT fk_lista_usuario
        FOREIGN KEY (id_usuario)
        REFERENCES Usuario (id_usuario)
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    CONSTRAINT fk_lista_libro
        FOREIGN KEY (id_libro)
        REFERENCES Libro (id_libro)
        ON UPDATE CASCADE
        ON DELETE CASCADE
);

-- ---------------------------------------------------------------------
-- Vistas
-- ---------------------------------------------------------------------

-- Libros con sus autores
CREATE OR REPLACE VIEW vw_libro_autores AS
SELECT
    b.id_libro,
    b.titulo,
    a.id_autor,
    a.aNombre       AS autor,
    a.nacionalidad,
    la.rol
FROM Libro_Autor la
JOIN Libro b ON b.id_libro = la.id_libro
JOIN Autor a ON a.id_autor = la.id_autor;

-- Libros con sus géneros
CREATE OR REPLACE VIEW vw_libro_generos AS
SELECT
    b.id_libro,
    b.titulo,
    g.id_genero,
    g.clasificacion AS genero
FROM Libro_Genero lg
JOIN Libro b  ON b.id_libro  = lg.id_libro
JOIN Genero g ON g.id_genero = lg.id_genero;

-- Sesiones con el campo calculado pag_leidas (C5)
CREATE OR REPLACE VIEW vw_sesion_paginas AS
SELECT
    s.id_sesion,
    s.id_lectura,
    s.pag_inicio,
    s.pag_fin,
    (s.pag_fin - s.pag_inicio) AS pag_leidas,
    s.duracion,
    s.cap_actual
FROM Sesion_Lectura s;
