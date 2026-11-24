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

## Endpoints
*La documentación de los endpoints se agregará conforme se implementen los módulos.*
