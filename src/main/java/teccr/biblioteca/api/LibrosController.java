package teccr.biblioteca.api;

import org.springframework.web.bind.annotation.*;
import teccr.biblioteca.data.Edicion;
import teccr.biblioteca.data.Libro;
import teccr.biblioteca.service.LibroService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/libros")
public class LibrosController {

    private final LibroService libroService;

    public LibrosController(LibroService libroService) {
        this.libroService = libroService;
    }

    /**
     * Catálogo de libros. Todos los filtros son opcionales y se pueden combinar.
     * Ejemplo: /api/v1/libros?genero=1&q=anillo
     */
    @GetMapping
    public List<Libro> getLibros(@RequestParam(name = "genero", required = false) Integer idGenero,
                                 @RequestParam(name = "autor", required = false) Integer idAutor,
                                 @RequestParam(name = "saga", required = false) Integer idSaga,
                                 @RequestParam(name = "q", required = false) String texto) {
        return libroService.buscarLibros(idGenero, idAutor, idSaga, texto);
    }

    @GetMapping("/{id}")
    public Libro getLibroById(@PathVariable Integer id) {
        return libroService.obtenerLibro(id);
    }

    @GetMapping("/{id}/ediciones")
    public List<Edicion> getEdicionesDeLibro(@PathVariable Integer id) {
        return libroService.obtenerEdiciones(id);
    }
}
