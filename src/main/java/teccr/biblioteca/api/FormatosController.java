package teccr.biblioteca.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import teccr.biblioteca.data.Formato;
import teccr.biblioteca.service.FormatoService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/formatos")
public class FormatosController {

    private final FormatoService formatoService;

    public FormatosController(FormatoService formatoService) {
        this.formatoService = formatoService;
    }

    @GetMapping
    public List<Formato> getFormatos() {
        return formatoService.obtenerFormatos();
    }
}
