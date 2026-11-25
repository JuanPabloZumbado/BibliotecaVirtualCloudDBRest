package teccr.biblioteca.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Un elemento de un ranking de las estadísticas: por ejemplo, un género y cuántas lecturas tiene. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Conteo {
    private Integer id;
    private String nombre;
    private Integer cantidad;
}
