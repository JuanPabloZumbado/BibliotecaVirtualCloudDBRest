package teccr.biblioteca.data;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Saga {
    private Integer idSaga;
    private String nombre;
    private String descripcion;
    // Solo se llenan en el detalle de la saga (GET /sagas/{id})
    private List<Libro> libros;
    private List<Autor> autores;
    private List<Genero> generos;
}
