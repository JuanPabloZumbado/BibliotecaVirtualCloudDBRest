package teccr.biblioteca.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import teccr.biblioteca.data.Genero;
import teccr.biblioteca.service.GeneroService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/generos")
public class GenerosController {

    private final GeneroService generoService;

    public GenerosController(GeneroService generoService) {
        this.generoService = generoService;
    }

    @GetMapping
    public List<Genero> getGeneros() {
        return generoService.obtenerGeneros();
    }
}
