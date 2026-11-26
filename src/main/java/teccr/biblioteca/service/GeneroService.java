package teccr.biblioteca.service;

import org.springframework.stereotype.Service;
import teccr.biblioteca.data.Genero;
import teccr.biblioteca.repository.GeneroRepository;

import java.util.List;

@Service
public class GeneroService {

    private final GeneroRepository generoRepository;

    public GeneroService(GeneroRepository generoRepository) {
        this.generoRepository = generoRepository;
    }

    public List<Genero> obtenerGeneros() {
        return generoRepository.findAll();
    }
}
