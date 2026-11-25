package teccr.biblioteca.api;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import teccr.biblioteca.data.Lectura;
import teccr.biblioteca.service.LecturaService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/usuarios/{idUsuario}/lecturas")
public class LecturasController {

    private final LecturaService lecturaService;

    public LecturasController(LecturaService lecturaService) {
        this.lecturaService = lecturaService;
    }

    /** Lecturas del usuario. Filtro opcional: ?estado=en curso */
    @GetMapping
    public List<Lectura> getLecturas(@PathVariable Integer idUsuario,
                                     @RequestParam(name = "estado", required = false) String estado) {
        return lecturaService.obtenerLecturas(idUsuario, estado);
    }

    @GetMapping("/{id}")
    public Lectura getLecturaById(@PathVariable Integer idUsuario, @PathVariable Integer id) {
        return lecturaService.obtenerLectura(idUsuario, id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Lectura iniciarLectura(@PathVariable Integer idUsuario, @RequestBody Lectura lectura) {
        return lecturaService.iniciarLectura(idUsuario, lectura);
    }

    @PatchMapping("/{id}")
    public Lectura actualizarLectura(@PathVariable Integer idUsuario, @PathVariable Integer id,
                                     @RequestBody Lectura lectura) {
        return lecturaService.actualizarLectura(idUsuario, id, lectura);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminarLectura(@PathVariable Integer idUsuario, @PathVariable Integer id) {
        lecturaService.eliminarLectura(idUsuario, id);
    }
}
