SET NAMES utf8mb4;

-- ---------- Usuario ----------
INSERT IGNORE INTO Usuario (id_usuario, uNombre, correo, fecha_registro) VALUES
(1, 'Juan Pablo Zumbado', 'juan.zumbadoz@correo.cr',   '2026-06-15'),
(2, 'Carlos Araya',       'carlos.araya@correo.cr',    '2026-06-28'),
(3, 'Fabricio Castro',    'fabricio.castro@correo.cr', '2026-07-05');

-- ---------- Autor ----------
INSERT IGNORE INTO Autor (id_autor, aNombre, nacionalidad) VALUES
(1, 'Brandon Sanderson',      'Estadounidense'),
(2, 'J.R.R. Tolkien',         'Británica'),
(3, 'Gabriel García Márquez', 'Colombiana'),
(4, 'Eiichiro Oda',           'Japonesa'),
(5, 'Alan Moore',             'Británica'),
(6, 'Dave Gibbons',           'Británica'),
(7, 'Yuval Noah Harari',      'Israelí'),
(8, 'Carlos Luis Fallas',     'Costarricense');

-- ---------- Genero ----------
INSERT IGNORE INTO Genero (id_genero, clasificacion) VALUES
(1, 'Fantasía'),
(2, 'Realismo mágico'),
(3, 'Manga'),
(4, 'Novela gráfica'),
(5, 'Ensayo'),
(6, 'Aventura'),
(7, 'Novela social');

-- ---------- Saga ----------
INSERT IGNORE INTO Saga (id_saga, sNombre, descripcion) VALUES
(1, 'El Señor de los Anillos', 'Trilogía de fantasía épica sobre la lucha contra el Anillo Único en la Tierra Media.'),
(2, 'Nacimiento Brumoso',      'Saga de fantasía donde un grupo de ladrones desafía a un emperador inmortal.'),
(3, 'One Piece',               'Manga de aventuras sobre un joven pirata que busca el tesoro más grande del mundo.');

-- ---------- Formato ----------
INSERT IGNORE INTO Formato (id_formato, tipo_formato) VALUES
(1, 'Físico'),
(2, 'Electrónico'),
(3, 'Audiolibro');

-- ---------- Libro (C1: sin tipo, tot_paginas ni anno_publicacion) ----------
INSERT IGNORE INTO Libro (id_libro, titulo, id_saga, orden_saga) VALUES
(1,  'La Comunidad del Anillo', 1,    1),
(2,  'Las Dos Torres',          1,    2),
(3,  'El Retorno del Rey',      1,    3),
(4,  'El Imperio Final',        2,    1),
(5,  'El Pozo de la Ascensión', 2,    2),
(6,  'One Piece Vol. 1',        3,    1),
(7,  'One Piece Vol. 2',        3,    2),
(8,  'Watchmen',                NULL, NULL),
(9,  'Cien años de soledad',    NULL, NULL),
(10, 'Sapiens',                 NULL, NULL),
(11, 'Mamita Yunai',            NULL, NULL);

-- ---------- Edicion (C1) ----------
-- Los libros 3 y 9 tienen dos ediciones para mostrar la relación 1:N.
INSERT IGNORE INTO Edicion (id_edicion, id_libro, tipo, editorial, isbn, tot_paginas, anno_publicacion) VALUES
(1,  1,  'Tapa blanda',           'Minotauro',             '978-0-000000-01', 576, '1954-07-29'),
(2,  2,  'Tapa blanda',           'Minotauro',             '978-0-000000-02', 464, '1954-11-11'),
(3,  3,  'Tapa blanda',           'Minotauro',             '978-0-000000-03', 544, '1955-10-20'),
(4,  3,  'Edición digital',       'Minotauro',             '978-0-000000-04', 560, '2012-03-15'),
(5,  4,  'Tapa blanda',           'Ediciones B',           '978-0-000000-05', 672, '2006-07-17'),
(6,  5,  'Tapa blanda',           'Ediciones B',           '978-0-000000-06', 784, '2007-08-21'),
(7,  6,  'Tomo',                  'Planeta Cómic',         '978-0-000000-07', 200, '1997-12-24'),
(8,  7,  'Tomo',                  'Planeta Cómic',         '978-0-000000-08', 192, '1998-04-03'),
(9,  8,  'Edición absoluta',      'ECC Ediciones',         '978-0-000000-09', 416, '1987-09-01'),
(10, 9,  'Tapa dura',             'Sudamericana',          '978-0-000000-10', 471, '1967-05-30'),
(11, 9,  'Edición conmemorativa', 'Alfaguara',             '978-0-000000-11', 760, '2007-03-26'),
(12, 10, 'Tapa blanda',           'Debate',                '978-0-000000-12', 498, '2011-01-01'),
(13, 11, 'Tapa blanda',           'Editorial Costa Rica',  '978-0-000000-13', 232, '1941-01-01');

-- ---------- Libro_Autor ----------
INSERT IGNORE INTO Libro_Autor (id_libro, id_autor, rol) VALUES
(1,  2, 'Escritor'),
(2,  2, 'Escritor'),
(3,  2, 'Escritor'),
(4,  1, 'Escritor'),
(5,  1, 'Escritor'),
(6,  4, 'Autor e ilustrador'),
(7,  4, 'Autor e ilustrador'),
(8,  5, 'Guionista'),
(8,  6, 'Ilustrador'),
(9,  3, 'Escritor'),
(10, 7, 'Escritor'),
(11, 8, 'Escritor');

-- ---------- Libro_Genero ----------
INSERT IGNORE INTO Libro_Genero (id_libro, id_genero) VALUES
(1, 1), (1, 6),
(2, 1), (2, 6),
(3, 1), (3, 6),
(4, 1),
(5, 1),
(6, 3), (6, 6),
(7, 3), (7, 6),
(8, 4),
(9, 2),
(10, 5),
(11, 7);

-- ---------- Saga_Autor (C3) ----------
INSERT IGNORE INTO Saga_Autor (id_saga, id_autor) VALUES
(1, 2),
(2, 1),
(3, 4);

-- ---------- Saga_Genero (C4) ----------
INSERT IGNORE INTO Saga_Genero (id_saga, id_genero) VALUES
(1, 1), (1, 6),
(2, 1),
(3, 3), (3, 6);

-- ---------- Lectura (C2: referencia a Edicion) ----------
INSERT IGNORE INTO Lectura (id_lectura, id_usuario, id_edicion, id_formato, fecha_inicio, fecha_fin, estado) VALUES
(1,  1, 1,  1, '2026-08-01', '2026-08-20', 'terminada'),
(2,  1, 2,  1, '2026-08-21', '2026-09-10', 'terminada'),
(3,  1, 4,  2, '2026-09-15', NULL,         'en curso'),
(4,  2, 7,  1, '2026-09-01', '2026-09-03', 'terminada'),
(5,  2, 8,  1, '2026-09-04', '2026-09-06', 'terminada'),
(6,  2, 9,  2, '2026-09-10', NULL,         'en curso'),
(7,  3, 10, 3, '2026-07-10', '2026-08-15', 'terminada'),
(8,  3, 5,  2, '2026-08-20', '2026-10-01', 'terminada'),
(9,  3, 13, 1, '2026-10-05', NULL,         'en curso'),
(10, 2, 12, 3, '2026-07-01', '2026-07-20', 'abandonada');

-- ---------- Sesion_Lectura (C5: pag_inicio / pag_fin) ----------
-- Las páginas leídas (pag_fin - pag_inicio) son las mismas que tenía pag_leidas.
INSERT IGNORE INTO Sesion_Lectura (id_sesion, id_lectura, pag_inicio, pag_fin, duracion, cap_actual) VALUES
(1,  1,    0, 150, 120,  4),
(2,  1,  150, 350, 150,  9),
(3,  1,  350, 576, 170, 12),
(4,  2,    0, 180, 140,  3),
(5,  2,  180, 330, 110,  6),
(6,  2,  330, 464, 100,  8),
(7,  3,    0, 120,  90,  2),
(8,  3,  120, 280, 120,  5),
(9,  4,    0, 100,  60,  4),
(10, 4,  100, 200,  65,  7),
(11, 5,    0,  90,  55,  3),
(12, 5,   90, 192,  60,  7),
(13, 6,    0,  80,  70,  2),
(14, 6,   80, 176,  80,  4),
(15, 7,    0, 120, 180,  5),
(16, 7,  120, 320, 240, 12),
(17, 7,  320, 471, 200, 20),
(18, 8,    0, 150, 130,  3),
(19, 8,  150, 400, 210, 20),
(20, 8,  400, 672, 220, 38),
(21, 9,    0,  60,  50,  2),
(22, 9,   60, 130,  55,  4),
(23, 10,   0,  60,  45,  1),
(24, 10,  60, 140,  60,  2);

-- ---------- Valoracion ----------
INSERT IGNORE INTO Valoracion (id_valoracion, id_lectura, puntuacion, resenna, fecha) VALUES
(1, 1,  5, 'Una aventura inolvidable, el mundo está construido con muchísimo detalle.',    '2026-08-20'),
(2, 2,  4, 'Más oscura que la primera parte, con una batalla final espectacular.',         '2026-09-10'),
(3, 4,  5, 'Un inicio muy divertido que presenta bien a los personajes.',                  '2026-09-03'),
(4, 5,  4, 'Mantiene el ritmo y el humor del primer tomo.',                                '2026-09-06'),
(5, 7,  5, 'La narración en audio hace que la historia de la familia Buendía cobre vida.', '2026-08-15'),
(6, 8,  5, 'Un sistema de magia original y un final sorprendente.',                        '2026-10-01'),
(7, 10, 2, 'No logré engancharme con el ritmo del libro.',                                 '2026-07-20');

-- ---------- Lista_Por_Leer ----------
INSERT IGNORE INTO Lista_Por_Leer (id_usuario, id_libro, prioridad, fecha_agregado) VALUES
(1, 4,  'Alta',  '2026-09-01'),
(1, 8,  'Media', '2026-09-05'),
(1, 10, 'Baja',  '2026-09-12'),
(2, 1,  'Alta',  '2026-09-07'),
(2, 9,  'Media', '2026-09-20'),
(3, 5,  'Alta',  '2026-10-01'),
(3, 10, 'Baja',  '2026-10-03');
