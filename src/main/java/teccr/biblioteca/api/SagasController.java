package teccr.biblioteca.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import teccr.biblioteca.data.Saga;
import teccr.biblioteca.service.SagaService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/sagas")
public class SagasController {

    private final SagaService sagaService;

    public SagasController(SagaService sagaService) {
        this.sagaService = sagaService;
    }

    @GetMapping
    public List<Saga> getSagas() {
        return sagaService.obtenerSagas();
    }

    @GetMapping("/{id}")
    public Saga getSagaById(@PathVariable Integer id) {
        return sagaService.obtenerSaga(id);
    }
}
