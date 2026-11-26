# BibliotecaVirtualCloudDBRest
Backend del proyecto de Bases de Datos de una biblioteca Virtual
Fabricio

## Tecnologías
- Java 21 y Spring Boot 4.0.2 (Maven)
- Spring JDBC (`JdbcTemplate`) con el patrón Repositorio
- MySQL 8.4
- Lombok

## Estructura
```
src/main/java/teccr/biblioteca/
  BibliotecaApplication.java   arranque y configuración CORS (/api/**)
  api/         controladores REST (/api/v1)
  service/     reglas de negocio
  repository/  interfaces + implementaciones Jdbc*Repository
  data/        modelos
src/main/resources/
  application.properties
  schema.sql   creación de tablas y vistas
  data.sql     datos de prueba
docs/          diagrama ER (draw.io) y diagrama lógico (Mermaid)
postman/       colección de Postman con todos los endpoints
docker-compose.yml  MySQL 8.4 para desarrollo
```

## Ejecutar el backend
Configure las variables de entorno con las credenciales de la base:

| Variable | Descripción |
|---|---|
| `DB_USR_NAME` | Usuario de MySQL (p. ej. `biblioteca`) |
| `DB_PWD` | Contraseña de ese usuario |
| `DB_PORT` | Opcional, por defecto `3306` |

```cmd
set DB_USR_NAME=biblioteca
set DB_PWD=su_contraseña
mvnw.cmd spring-boot:run
```
En IntelliJ: **Run → Edit Configurations → BibliotecaApplication → Environment variables**, con el formato `DB_USR_NAME=biblioteca;DB_PWD=su_contraseña`.

El servidor queda en `http://localhost:8080`.

## API REST

Todos los endpoints usan el prefijo `/api/v1` y no requieren una sesión iniciada. Los cuerpos de las solicitudes y las respuestas usan JSON.

URL base al ejecutar localmente:
```text
http://localhost:8080
```

### Errores
Cuando ocurre un error, la respuesta tiene este formato:
```json
{ "status": 404, "error": "Not Found", "message": "Libro no encontrado con id: 99" }
```
| Código | Cuándo ocurre |
|---|---|
| `400 Bad Request` | Datos inválidos o un parámetro con el tipo incorrecto (p. ej. `/sagas/abc`) |
| `404 Not Found` | El recurso solicitado no existe |
| `409 Conflict` | El dato ya existe (p. ej. un libro repetido en la lista por leer) o la operación viola una restricción de la base de datos |

### Usuarios

Los usuarios ya existen en la base de datos: no hay registro ni administración de usuarios. El inicio de sesión se hace buscando al usuario por su correo, sin contraseña.

| Método | Endpoint | Descripción | Respuesta exitosa |
|---|---|---|---|
| `GET` | `/api/v1/usuarios/{id}` | Obtener un usuario por su ID. | `200 OK`; `404` si no existe |
| `GET` | `/api/v1/usuarios/by-correo/{correo}` | Buscar un usuario por su correo. Lo usa el login del frontend. | `200 OK`; `404` si no existe |
| `PATCH` | `/api/v1/usuarios/{id}` | Actualizar el perfil. Solo cambian los campos enviados. | `200 OK`; `400` datos inválidos; `404`; `409` correo ya registrado |

Campos JSON de usuario:

| Campo | Tipo | Notas |
|---|---|---|
| `idUsuario` | integer | |
| `nombre` | string | Editable; no puede quedar vacío |
| `correo` | string | Editable; único y con formato válido |
| `fechaRegistro` | string | Formato `YYYY-MM-DD`; no editable |

Actualizar el nombre de un usuario:
```http
PATCH /api/v1/usuarios/3
Content-Type: application/json

{
  "nombre": "Fabricio Castro"
}
```

### Lecturas

Una lectura es un usuario leyendo una **edición** de un libro en un **formato** (físico, electrónico o audiolibro). Todas las rutas validan que la lectura pertenezca al usuario `{idUsuario}`.

| Método | Endpoint | Descripción | Respuesta exitosa |
|---|---|---|---|
| `GET` | `/api/v1/usuarios/{idUsuario}/lecturas` | Lecturas del usuario, de la más reciente a la más antigua. Filtro opcional `?estado=`. | `200 OK`, arreglo JSON |
| `GET` | `/api/v1/usuarios/{idUsuario}/lecturas/{id}` | Detalle de la lectura con sus sesiones. | `200 OK`; `404` |
| `POST` | `/api/v1/usuarios/{idUsuario}/lecturas` | Iniciar una lectura. | `201 Created`; `400`; `404` usuario; `409` ya existe una lectura activa de esa edición |
| `PATCH` | `/api/v1/usuarios/{idUsuario}/lecturas/{id}` | Cambiar el estado, el formato o las fechas. | `200 OK`; `400`; `404` |
| `DELETE` | `/api/v1/usuarios/{idUsuario}/lecturas/{id}` | Eliminar la lectura junto con sus sesiones y valoraciones. | `204 No Content`; `404` |

Estados válidos: `por iniciar`, `en curso`, `terminada`, `abandonada`.

Campos JSON de lectura:

| Campo | Tipo | Notas |
|---|---|---|
| `idLectura` | integer | Lo genera la base de datos |
| `idUsuario` | integer | Se toma de la ruta |
| `idEdicion` | integer | Obligatorio al crear |
| `idFormato` | integer | Obligatorio al crear; editable |
| `fechaInicio` | string | `YYYY-MM-DD`; por defecto, la fecha de hoy |
| `fechaFin` | string o `null` | Se asigna sola al terminar o abandonar la lectura (si no se envía) y se borra si la lectura se reabre |
| `estado` | string | Al crear solo se permite `por iniciar` o `en curso` (por defecto `en curso`) |
| `idLibro`, `titulo`, `tipoEdicion`, `editorial`, `totPaginas`, `tipoFormato` | | Solo lectura (se obtienen con JOIN) |
| `paginaActual` | integer | Solo lectura: mayor `pagFin` registrado en las sesiones |
| `paginasLeidas`, `minutosLeidos` | integer | Solo lectura: sumas de las sesiones (vista `vw_sesion_paginas`) |
| `sesiones` | arreglo | Solo en el detalle |

Iniciar una lectura:
```http
POST /api/v1/usuarios/1/lecturas
Content-Type: application/json

{
  "idEdicion": 5,
  "idFormato": 1
}
```

Terminar una lectura (la `fechaFin` toma la fecha de hoy):
```http
PATCH /api/v1/usuarios/1/lecturas/11
Content-Type: application/json

{
  "estado": "terminada"
}
```

### Sesiones de lectura

| Método | Endpoint | Descripción | Respuesta exitosa |
|---|---|---|---|
| `GET` | `/api/v1/lecturas/{idLectura}/sesiones` | Sesiones de la lectura, en orden de registro. | `200 OK`, arreglo JSON; `404` |
| `POST` | `/api/v1/lecturas/{idLectura}/sesiones` | Registrar una sesión. | `201 Created`; `400`; `404` |
| `DELETE` | `/api/v1/lecturas/{idLectura}/sesiones/{id}` | Eliminar una sesión de la lectura. | `204 No Content`; `404` |

Campos JSON de sesión:

| Campo | Tipo | Notas |
|---|---|---|
| `idSesion` | integer | Lo genera la base de datos |
| `pagInicio` | integer | Opcional; por defecto es la última página registrada (0 en la primera sesión) |
| `pagFin` | integer | Obligatorio; `>= pagInicio` y `<=` total de páginas de la edición |
| `pagLeidas` | integer | Solo lectura: `pagFin - pagInicio` |
| `duracion` | integer | Obligatorio; minutos, `> 0` |
| `capActual` | integer | Obligatorio; capítulo en el que quedó el usuario |

Reglas:
- No se pueden registrar sesiones en una lectura `terminada` o `abandonada`.
- Si la lectura estaba `por iniciar`, la primera sesión la pasa a `en curso`.

Registrar una sesión (`pagInicio` se completa automáticamente):
```http
POST /api/v1/lecturas/11/sesiones
Content-Type: application/json

{
  "pagFin": 150,
  "duracion": 45,
  "capActual": 5
}
```

### Valoraciones

Una valoración es la puntuación y la reseña que el usuario deja sobre una **lectura** terminada o abandonada. Las valoraciones de un libro reúnen las de todas sus ediciones.

| Método | Endpoint | Descripción | Respuesta exitosa |
|---|---|---|---|
| `GET` | `/api/v1/lecturas/{idLectura}/valoraciones` | Valoración de la lectura (arreglo vacío si aún no tiene). | `200 OK`, arreglo JSON; `404` |
| `POST` | `/api/v1/lecturas/{idLectura}/valoraciones` | Valorar una lectura. | `201 Created`; `400`; `404`; `409` la lectura ya tiene valoración |
| `GET` | `/api/v1/libros/{idLibro}/valoraciones` | Valoraciones de todos los usuarios sobre el libro, de la más reciente a la más antigua. | `200 OK`, arreglo JSON; `404` |
| `GET` | `/api/v1/valoraciones/{id}` | Una valoración por su ID. | `200 OK`; `404` |
| `PATCH` | `/api/v1/valoraciones/{id}` | Cambiar la puntuación y/o la reseña. | `200 OK`; `400`; `404` |
| `DELETE` | `/api/v1/valoraciones/{id}` | Eliminar la valoración. | `204 No Content`; `404` |

Campos JSON de valoración:

| Campo | Tipo | Notas |
|---|---|---|
| `idValoracion` | integer | Lo genera la base de datos |
| `idLectura` | integer | Se toma de la ruta |
| `puntuacion` | integer | Obligatorio; de 1 a 5 |
| `resenna` | string | Obligatorio; máximo 2000 caracteres |
| `fecha` | string | `YYYY-MM-DD`; se asigna sola con la fecha de hoy al crear o modificar |
| `idUsuario`, `nombreUsuario`, `idLibro`, `titulo` | | Solo lectura (se obtienen con JOIN) |

Reglas:
- Solo se valoran lecturas `terminada` o `abandonada`.
- Cada lectura tiene como máximo una valoración; para cambiarla se usa `PATCH`.

Valorar una lectura:
```http
POST /api/v1/lecturas/3/valoraciones
Content-Type: application/json

{
  "puntuacion": 5,
  "resenna": "Excelente final de la saga."
}
```

### Lista por leer

Libros que el usuario quiere leer más adelante, cada uno con una prioridad. Un libro aparece una sola vez en la lista de cada usuario.

| Método | Endpoint | Descripción | Respuesta exitosa |
|---|---|---|---|
| `GET` | `/api/v1/usuarios/{idUsuario}/por-leer` | Lista del usuario, ordenada por prioridad (Alta, Media, Baja). Filtro opcional `?prioridad=`. | `200 OK`, arreglo JSON; `404` |
| `GET` | `/api/v1/usuarios/{idUsuario}/por-leer/{idLibro}` | Un libro de la lista. | `200 OK`; `404` |
| `POST` | `/api/v1/usuarios/{idUsuario}/por-leer` | Agregar un libro a la lista. | `201 Created`; `400`; `404` usuario o libro; `409` el libro ya está en la lista |
| `PATCH` | `/api/v1/usuarios/{idUsuario}/por-leer/{idLibro}` | Cambiar la prioridad. | `200 OK`; `400`; `404` |
| `DELETE` | `/api/v1/usuarios/{idUsuario}/por-leer/{idLibro}` | Quitar el libro de la lista. | `204 No Content`; `404` |

Campos JSON de libro por leer:

| Campo | Tipo | Notas |
|---|---|---|
| `idUsuario` | integer | Se toma de la ruta |
| `idLibro` | integer | Obligatorio al agregar |
| `prioridad` | string | `Alta`, `Media` o `Baja` (no distingue mayúsculas); por defecto `Media` |
| `fechaAgregado` | string | `YYYY-MM-DD`; se asigna sola con la fecha de hoy |
| `titulo`, `nombreSaga`, `ordenSaga` | | Solo lectura (se obtienen con JOIN) |

Agregar un libro a la lista:
```http
POST /api/v1/usuarios/1/por-leer
Content-Type: application/json

{
  "idLibro": 6,
  "prioridad": "Alta"
}
```

### Estadísticas

Resumen de la actividad de lectura del usuario, calculado con consultas de agregación (`COUNT`, `SUM`, `AVG`, `GROUP BY`).

| Método | Endpoint | Descripción | Respuesta exitosa |
|---|---|---|---|
| `GET` | `/api/v1/usuarios/{idUsuario}/estadisticas` | Estadísticas del usuario. | `200 OK`; `404` si el usuario no existe |

Campos JSON:

| Campo | Tipo | Notas |
|---|---|---|
| `idUsuario`, `nombre` | integer / string | |
| `totalLecturas` | integer | |
| `lecturasPorEstado` | objeto | Los 4 estados, con `0` si no hay lecturas en ese estado |
| `librosTerminados` | integer | Libros distintos con al menos una lectura terminada |
| `totalSesiones`, `paginasLeidas`, `minutosLeidos` | integer | Sumas de todas las sesiones del usuario |
| `paginasPorHora` | number o `null` | `null` si no hay minutos registrados |
| `totalValoraciones` | integer | |
| `promedioPuntuacion` | number o `null` | Redondeado a 2 decimales; `null` si no ha valorado |
| `librosPorLeer` | integer | Libros en la lista por leer |
| `generosMasLeidos`, `autoresMasLeidos` | arreglo | Los 3 primeros: `id`, `nombre`, `cantidad` (lecturas) |
| `formatosUsados` | arreglo | `id`, `nombre`, `cantidad` (lecturas) |

Ejemplo de respuesta (resumido):
```json
{
  "idUsuario": 1,
  "nombre": "Juan Pablo Zumbado",
  "totalLecturas": 3,
  "lecturasPorEstado": { "por iniciar": 0, "en curso": 1, "terminada": 2, "abandonada": 0 },
  "librosTerminados": 2,
  "totalSesiones": 8,
  "paginasLeidas": 1320,
  "minutosLeidos": 1000,
  "paginasPorHora": 79.2,
  "totalValoraciones": 2,
  "promedioPuntuacion": 4.5,
  "librosPorLeer": 3,
  "generosMasLeidos": [ { "id": 6, "nombre": "Aventura", "cantidad": 3 } ],
  "autoresMasLeidos": [ { "id": 2, "nombre": "J.R.R. Tolkien", "cantidad": 3 } ],
  "formatosUsados": [ { "id": 1, "nombre": "Físico", "cantidad": 2 } ]
}
```

### Catálogo de libros

| Método | Endpoint | Descripción | Respuesta exitosa |
|---|---|---|---|
| `GET` | `/api/v1/libros` | Catálogo de libros con sus autores y géneros. Acepta filtros opcionales. | `200 OK`, arreglo JSON |
| `GET` | `/api/v1/libros/{id}` | Detalle de un libro: autores (con su rol), géneros, saga y ediciones. | `200 OK`; `404` si no existe |
| `GET` | `/api/v1/libros/{id}/ediciones` | Ediciones de un libro. | `200 OK`, arreglo JSON; `404` si el libro no existe |
| `GET` | `/api/v1/ediciones/{id}` | Una edición por su ID. | `200 OK`; `404` si no existe |

Filtros de `GET /api/v1/libros` (opcionales y combinables):

| Parámetro | Tipo | Descripción |
|---|---|---|
| `genero` | integer | ID del género |
| `autor` | integer | ID del autor |
| `saga` | integer | ID de la saga |
| `q` | string | Texto contenido en el título o en el nombre de un autor. No distingue mayúsculas ni acentos. |

Campos JSON de libro:

| Campo | Tipo | Notas |
|---|---|---|
| `idLibro` | integer | |
| `titulo` | string | |
| `idSaga`, `nombreSaga`, `ordenSaga` | integer / string / integer | Se omiten si el libro no pertenece a una saga |
| `autores` | arreglo | `idAutor`, `nombre`, `nacionalidad`, `rol` |
| `generos` | arreglo | `idGenero`, `clasificacion` |
| `ediciones` | arreglo | Solo en el detalle. Ver campos de edición |

Campos JSON de edición: `idEdicion`, `idLibro`, `tipo`, `editorial`, `isbn`, `totPaginas`, `annoPublicacion` (formato `YYYY-MM-DD`).

Ejemplo, `GET /api/v1/libros/3`:
```json
{
  "idLibro": 3,
  "titulo": "El Retorno del Rey",
  "idSaga": 1,
  "nombreSaga": "El Señor de los Anillos",
  "ordenSaga": 3,
  "autores": [{ "idAutor": 2, "nombre": "J.R.R. Tolkien", "nacionalidad": "Británica", "rol": "Escritor" }],
  "generos": [{ "idGenero": 6, "clasificacion": "Aventura" }, { "idGenero": 1, "clasificacion": "Fantasía" }],
  "ediciones": [
    { "idEdicion": 3, "idLibro": 3, "tipo": "Tapa blanda", "editorial": "Minotauro", "isbn": "978-0-000000-03", "totPaginas": 544, "annoPublicacion": "1955-10-20" },
    { "idEdicion": 4, "idLibro": 3, "tipo": "Edición digital", "editorial": "Minotauro", "isbn": "978-0-000000-04", "totPaginas": 560, "annoPublicacion": "2012-03-15" }
  ]
}
```

### Autores

| Método | Endpoint | Descripción | Respuesta exitosa |
|---|---|---|---|
| `GET` | `/api/v1/autores` | Todos los autores, ordenados por nombre. | `200 OK`, arreglo JSON |
| `GET` | `/api/v1/autores/{id}` | Un autor por su ID. | `200 OK`; `404` si no existe |
| `GET` | `/api/v1/autores/{id}/libros` | Libros escritos por el autor. | `200 OK`, arreglo JSON; `404` si el autor no existe |

Campos JSON de autor: `idAutor`, `nombre`, `nacionalidad`.

### Géneros

| Método | Endpoint | Descripción | Respuesta exitosa |
|---|---|---|---|
| `GET` | `/api/v1/generos` | Todos los géneros. | `200 OK`, arreglo JSON |

Campos JSON de género: `idGenero`, `clasificacion`.

### Sagas

| Método | Endpoint | Descripción | Respuesta exitosa |
|---|---|---|---|
| `GET` | `/api/v1/sagas` | Todas las sagas. | `200 OK`, arreglo JSON |
| `GET` | `/api/v1/sagas/{id}` | Detalle de la saga: libros en orden de lectura, autores y géneros. | `200 OK`; `404` si no existe |

Campos JSON de saga: `idSaga`, `nombre`, `descripcion`. Solo en el detalle se agregan `libros`, `autores` y `generos`.

### Formatos

| Método | Endpoint | Descripción | Respuesta exitosa |
|---|---|---|---|
| `GET` | `/api/v1/formatos` | Formatos de lectura (Físico, Electrónico, Audiolibro). | `200 OK`, arreglo JSON |

Campos JSON de formato: `idFormato`, `tipoFormato`.

### Ejemplos con curl
```sh
curl http://localhost:8080/api/v1/libros
curl "http://localhost:8080/api/v1/libros?genero=1&q=anillo"
curl http://localhost:8080/api/v1/sagas/1
curl http://localhost:8080/api/v1/usuarios/1/estadisticas
```

## Uso de las tablas

Todas las tablas del modelo se usan desde la API:

| Tabla | Endpoints que la usan |
|---|---|
| `Usuario` | Usuarios, estadísticas, valoraciones de un libro (nombre del autor de la reseña) |
| `Libro` | Catálogo, autores, sagas, lecturas, lista por leer, valoraciones |
| `Edicion` | Detalle del libro, ediciones, lecturas |
| `Autor`, `Libro_Autor` | Catálogo (filtro `autor` y búsqueda `q`), autores, estadísticas |
| `Genero`, `Libro_Genero` | Catálogo (filtro `genero`), géneros, estadísticas |
| `Saga` | Catálogo (filtro `saga`), sagas |
| `Saga_Autor`, `Saga_Genero` | Detalle de la saga |
| `Formato` | Formatos, lecturas, estadísticas |
| `Lectura` | Lecturas, sesiones, valoraciones, estadísticas |
| `Sesion_Lectura` | Sesiones, progreso de las lecturas y estadísticas (vista `vw_sesion_paginas`) |
| `Valoracion` | Valoraciones, estadísticas |
| `Lista_Por_Leer` | Lista por leer, estadísticas |

## Postman

La colección [postman/BibliotecaVirtualAPICollection.postman_collection.json](postman/BibliotecaVirtualAPICollection.postman_collection.json) tiene los 45 requests, organizados por carpeta, y cada uno verifica el código de respuesta esperado.

1. En Postman: **Import** → seleccione el archivo.
2. Revise las variables de la colección (pestaña **Variables**): `baseUrl` (por defecto `http://localhost:8080`), `userId`, `libroId`, etc.
3. Con el backend corriendo, use **Run collection** para ejecutarla completa.

Las carpetas están en orden: se inicia una lectura de prueba, se le registran sesiones, se termina, se valora y al final se elimina. Los ids que se crean se guardan solos en las variables (`lecturaId`, `sesionId`, `valoracionId`), así que la base de datos queda con los mismos datos al terminar.
