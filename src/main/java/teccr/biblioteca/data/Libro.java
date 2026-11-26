package teccr.biblioteca.data;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Libro {
    private Integer idLibro;
    private String titulo;
    private Integer idSaga;       // null si el libro no pertenece a una saga
    private String nombreSaga;
    private Integer ordenSaga;
    private List<Autor> autores;
    private List<Genero> generos;
    // Solo se llena en el detalle del libro (GET /libros/{id})
    private List<Edicion> ediciones;
}
