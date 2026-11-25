package teccr.biblioteca.api;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import teccr.biblioteca.data.Valoracion;
import teccr.biblioteca.service.ValoracionService;

import java.util.List;

/**
 * Las valoraciones se consultan desde la lectura o desde el libro, y se modifican por su propio ID.
 */
@RestController
@RequestMapping("/api/v1")
public class ValoracionesController {

    private final ValoracionService valoracionService;

    public ValoracionesController(ValoracionService valoracionService) {
        this.valoracionService = valoracionService;
    }

    @GetMapping("/lecturas/{idLectura}/valoraciones")
    public List<Valoracion> getValoracionesDeLectura(@PathVariable Integer idLectura) {
        return valoracionService.obtenerValoracionesDeLectura(idLectura);
    }

    @PostMapping("/lecturas/{idLectura}/valoraciones")
    @ResponseStatus(HttpStatus.CREATED)
    public Valoracion crearValoracion(@PathVariable Integer idLectura, @RequestBody Valoracion valoracion) {
        return valoracionService.crearValoracion(idLectura, valoracion);
    }

    @GetMapping("/libros/{idLibro}/valoraciones")
    public List<Valoracion> getValoracionesDeLibro(@PathVariable Integer idLibro) {
        return valoracionService.obtenerValoracionesDeLibro(idLibro);
    }

    @GetMapping("/valoraciones/{id}")
    public Valoracion getValoracionById(@PathVariable Integer id) {
        return valoracionService.obtenerValoracion(id);
    }

    @PatchMapping("/valoraciones/{id}")
    public Valoracion actualizarValoracion(@PathVariable Integer id, @RequestBody Valoracion valoracion) {
        return valoracionService.actualizarValoracion(id, valoracion);
    }

    @DeleteMapping("/valoraciones/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminarValoracion(@PathVariable Integer id) {
        valoracionService.eliminarValoracion(id);
    }
}
