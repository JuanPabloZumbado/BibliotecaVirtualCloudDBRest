package teccr.biblioteca.data;

import lombok.Data;

import java.time.LocalDate;

/** Un libro dentro de la lista "por leer" de un usuario (tabla Lista_Por_Leer). */
@Data
public class LibroPorLeer {

    // Columnas de la tabla Lista_Por_Leer
    private Integer idUsuario;
    private Integer idLibro;
    private String prioridad;           // Alta | Media | Baja
    private LocalDate fechaAgregado;

    // Datos de solo lectura que se obtienen con JOIN (Libro, Saga)
    private String titulo;
    private String nombreSaga;
    private Integer ordenSaga;
}
