package teccr.biblioteca.service;

import org.springframework.stereotype.Service;
import teccr.biblioteca.data.Formato;
import teccr.biblioteca.repository.FormatoRepository;

import java.util.List;

@Service
public class FormatoService {

    private final FormatoRepository formatoRepository;

    public FormatoService(FormatoRepository formatoRepository) {
        this.formatoRepository = formatoRepository;
    }

    public List<Formato> obtenerFormatos() {
        return formatoRepository.findAll();
    }
}
