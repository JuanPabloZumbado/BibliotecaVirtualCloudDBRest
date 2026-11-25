package teccr.biblioteca.data;

import lombok.Data;

import java.time.LocalDate;

@Data
public class Valoracion {

    // Columnas de la tabla Valoracion
    private Integer idValoracion;
    private Integer idLectura;
    private Integer puntuacion;     // 1 a 5
    private String resenna;
    private LocalDate fecha;

    // Datos de solo lectura que se obtienen con JOIN (Lectura, Usuario, Edicion, Libro)
    private Integer idUsuario;
    private String nombreUsuario;
    private Integer idLibro;
    private String titulo;
}
