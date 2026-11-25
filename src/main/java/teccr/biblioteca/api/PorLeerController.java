package teccr.biblioteca.api;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import teccr.biblioteca.data.LibroPorLeer;
import teccr.biblioteca.service.PorLeerService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/usuarios/{idUsuario}/por-leer")
public class PorLeerController {

    private final PorLeerService porLeerService;

    public PorLeerController(PorLeerService porLeerService) {
        this.porLeerService = porLeerService;
    }

    /** Lista por leer del usuario. Filtro opcional: ?prioridad=Alta */
    @GetMapping
    public List<LibroPorLeer> getLista(@PathVariable Integer idUsuario,
                                       @RequestParam(name = "prioridad", required = false) String prioridad) {
        return porLeerService.obtenerLista(idUsuario, prioridad);
    }

    @GetMapping("/{idLibro}")
    public LibroPorLeer getLibroDeLista(@PathVariable Integer idUsuario, @PathVariable Integer idLibro) {
        return porLeerService.obtenerLibroDeLista(idUsuario, idLibro);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LibroPorLeer agregarLibro(@PathVariable Integer idUsuario, @RequestBody LibroPorLeer libro) {
        return porLeerService.agregarLibro(idUsuario, libro);
    }

    @PatchMapping("/{idLibro}")
    public LibroPorLeer cambiarPrioridad(@PathVariable Integer idUsuario, @PathVariable Integer idLibro,
                                         @RequestBody LibroPorLeer libro) {
        return porLeerService.cambiarPrioridad(idUsuario, idLibro, libro);
    }

    @DeleteMapping("/{idLibro}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void quitarLibro(@PathVariable Integer idUsuario, @PathVariable Integer idLibro) {
        porLeerService.quitarLibro(idUsuario, idLibro);
    }
}
