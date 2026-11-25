package teccr.biblioteca.api;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import teccr.biblioteca.data.SesionLectura;
import teccr.biblioteca.service.SesionLecturaService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/lecturas/{idLectura}/sesiones")
public class SesionesController {

    private final SesionLecturaService sesionService;

    public SesionesController(SesionLecturaService sesionService) {
        this.sesionService = sesionService;
    }

    @GetMapping
    public List<SesionLectura> getSesiones(@PathVariable Integer idLectura) {
        return sesionService.obtenerSesiones(idLectura);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SesionLectura registrarSesion(@PathVariable Integer idLectura, @RequestBody SesionLectura sesion) {
        return sesionService.registrarSesion(idLectura, sesion);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminarSesion(@PathVariable Integer idLectura, @PathVariable Integer id) {
        sesionService.eliminarSesion(idLectura, id);
    }
}
