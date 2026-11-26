package teccr.biblioteca.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import teccr.biblioteca.data.Edicion;
import teccr.biblioteca.service.LibroService;

@RestController
@RequestMapping("/api/v1/ediciones")
public class EdicionesController {

    private final LibroService libroService;

    public EdicionesController(LibroService libroService) {
        this.libroService = libroService;
    }

    @GetMapping("/{id}")
    public Edicion getEdicionById(@PathVariable Integer id) {
        return libroService.obtenerEdicion(id);
    }
}
