# Diagrama lógico: Biblioteca Virtual (Entrega #2)

Este es el modelo corregido de la Entrega #1. Las tablas nuevas son **Edicion**, **Saga_Autor** y **Saga_Genero**.

```mermaid
erDiagram
    Usuario {
        INT id_usuario PK
        VARCHAR uNombre
        VARCHAR correo UK
        DATE fecha_registro
    }
    Autor {
        INT id_autor PK
        VARCHAR aNombre
        VARCHAR nacionalidad
    }
    Genero {
        INT id_genero PK
        VARCHAR clasificacion
    }
    Saga {
        INT id_saga PK
        VARCHAR sNombre
        VARCHAR descripcion
    }
    Formato {
        INT id_formato PK
        VARCHAR tipo_formato
    }
    Libro {
        INT id_libro PK
        VARCHAR titulo
        INT id_saga FK "nulo"
        INT orden_saga "nulo"
    }
    Edicion {
        INT id_edicion PK
        INT id_libro FK
        VARCHAR tipo
        VARCHAR editorial
        VARCHAR isbn UK
        INT tot_paginas
        DATE anno_publicacion
    }
    Libro_Autor {
        INT id_libro PK,FK
        INT id_autor PK,FK
        VARCHAR rol
    }
    Libro_Genero {
        INT id_libro PK,FK
        INT id_genero PK,FK
    }
    Saga_Autor {
        INT id_saga PK,FK
        INT id_autor PK,FK
    }
    Saga_Genero {
        INT id_saga PK,FK
        INT id_genero PK,FK
    }
    Lectura {
        INT id_lectura PK
        INT id_usuario FK
        INT id_edicion FK
        INT id_formato FK
        DATE fecha_inicio
        DATE fecha_fin "nulo"
        VARCHAR estado
    }
    Sesion_Lectura {
        INT id_sesion PK
        INT id_lectura FK
        INT pag_inicio
        INT pag_fin
        INT duracion
        INT cap_actual
    }
    Valoracion {
        INT id_valoracion PK
        INT id_lectura FK
        INT puntuacion "1-5"
        VARCHAR resenna
        DATE fecha
    }
    Lista_Por_Leer {
        INT id_usuario PK,FK
        INT id_libro PK,FK
        VARCHAR prioridad
        DATE fecha_agregado
    }

    Saga  |o--o{ Libro        : "pertenece_a"
    Libro ||--|{ Edicion      : "tiene"
    Libro ||--o{ Libro_Autor  : ""
    Autor ||--o{ Libro_Autor  : "escrito_por"
    Libro ||--o{ Libro_Genero : ""
    Genero ||--o{ Libro_Genero : "clasificado_en"
    Saga  ||--o{ Saga_Autor   : ""
    Autor ||--o{ Saga_Autor   : "saga_escrita_por"
    Saga  ||--o{ Saga_Genero  : ""
    Genero ||--o{ Saga_Genero : "saga_clasificada_en"
    Usuario ||--o{ Lista_Por_Leer : "por_leer"
    Libro   ||--o{ Lista_Por_Leer : ""
    Usuario ||--o{ Lectura    : "realiza"
    Edicion ||--o{ Lectura    : "es_leida"
    Formato ||--o{ Lectura    : "en_formato"
    Lectura ||--o{ Sesion_Lectura : "registra"
    Lectura ||--o{ Valoracion : "se_valora"
```

## Restricciones
- `Usuario.correo` y `Edicion.isbn` son únicos.
- `Valoracion.puntuacion` va de 1 a 5.
- `Lectura.estado` ∈ {por iniciar, en curso, terminada, abandonada}.
- `Lista_Por_Leer.prioridad` ∈ {Alta, Media, Baja}.
- `Sesion_Lectura`: `pag_fin ≥ pag_inicio`. Las páginas leídas se calculan como `pag_fin − pag_inicio` (vista `vw_sesion_paginas`).
- `fecha_registro`, `Valoracion.fecha` y `fecha_agregado` toman la fecha actual por defecto.
- Campos opcionales: `Lectura.fecha_fin`, `Libro.id_saga` y `Libro.orden_saga`.
