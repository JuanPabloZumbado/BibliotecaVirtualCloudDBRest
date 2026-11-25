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
docker-compose.yml  MySQL 8.4 para desarrollo
```

## Base de datos
El modelo corregido está en [docs/diagrama_logico.md](docs/diagrama_logico.md) y [docs/diagrama_er_lecturas.drawio](docs/diagrama_er_lecturas.drawio).

### Opción A: Docker
```cmd
copy .env.example .env
notepad .env
docker compose up -d
```
El contenedor `biblioteca-mysql` crea la base `bibliotecadb` y carga `schema.sql` y `data.sql`.
Para recrear la base desde cero: `docker compose down -v` y luego `docker compose up -d`.

### Opción B: MySQL local
```sql
CREATE DATABASE bibliotecadb CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'biblioteca'@'localhost' IDENTIFIED BY 'su_contraseña';
GRANT ALL PRIVILEGES ON bibliotecadb.* TO 'biblioteca'@'localhost';
```
Las tablas no hace falta crearlas a mano: el backend ejecuta `schema.sql` y `data.sql` cada vez que arranca (`spring.sql.init.mode=always`). Los scripts son idempotentes.

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
| `409 Conflict` | La operación viola una restricción de la base de datos (duplicado o referencia inválida) |

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
```
