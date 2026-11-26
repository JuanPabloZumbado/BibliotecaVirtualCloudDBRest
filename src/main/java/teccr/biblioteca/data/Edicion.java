package teccr.biblioteca.data;

import lombok.Data;

import java.time.LocalDate;

@Data
public class Edicion {
    private Integer idEdicion;
    private Integer idLibro;
    private String tipo;
    private String editorial;
    private String isbn;
    private Integer totPaginas;
    private LocalDate annoPublicacion;
}
