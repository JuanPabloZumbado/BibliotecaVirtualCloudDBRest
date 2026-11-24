package teccr.biblioteca.data;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Autor {
    private Integer idAutor;
    private String nombre;
    private String nacionalidad;
    // Solo se llena cuando el autor se consulta en el contexto de un libro (tabla Libro_Autor)
    private String rol;
}
