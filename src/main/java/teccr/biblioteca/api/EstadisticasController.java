package teccr.biblioteca.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import teccr.biblioteca.data.EstadisticasUsuario;
import teccr.biblioteca.service.EstadisticasService;

@RestController
@RequestMapping("/api/v1/usuarios/{idUsuario}/estadisticas")
public class EstadisticasController {

    private final EstadisticasService estadisticasService;

    public EstadisticasController(EstadisticasService estadisticasService) {
        this.estadisticasService = estadisticasService;
    }

    @GetMapping
    public EstadisticasUsuario getEstadisticas(@PathVariable Integer idUsuario) {
        return estadisticasService.obtenerEstadisticas(idUsuario);
    }
}
