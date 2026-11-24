package teccr.biblioteca.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import teccr.biblioteca.data.Autor;
import teccr.biblioteca.data.Libro;
import teccr.biblioteca.service.AutorService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/autores")
public class AutoresController {

    private final AutorService autorService;

    public AutoresController(AutorService autorService) {
        this.autorService = autorService;
    }

    @GetMapping
    public List<Autor> getAutores() {
        return autorService.obtenerAutores();
    }

    @GetMapping("/{id}")
    public Autor getAutorById(@PathVariable Integer id) {
        return autorService.obtenerAutor(id);
    }

    @GetMapping("/{id}/libros")
    public List<Libro> getLibrosDeAutor(@PathVariable Integer id) {
        return autorService.obtenerLibrosDeAutor(id);
    }
}
