package teccr.biblioteca.service;

import org.springframework.stereotype.Service;
import teccr.biblioteca.data.Libro;
import teccr.biblioteca.data.Saga;
import teccr.biblioteca.exception.RecursoNoEncontradoException;
import teccr.biblioteca.repository.AutorRepository;
import teccr.biblioteca.repository.GeneroRepository;
import teccr.biblioteca.repository.LibroRepository;
import teccr.biblioteca.repository.SagaRepository;

import java.util.List;

@Service
public class SagaService {

    private final SagaRepository sagaRepository;
    private final LibroRepository libroRepository;
    private final AutorRepository autorRepository;
    private final GeneroRepository generoRepository;

    public SagaService(SagaRepository sagaRepository,
                       LibroRepository libroRepository,
                       AutorRepository autorRepository,
                       GeneroRepository generoRepository) {
        this.sagaRepository = sagaRepository;
        this.libroRepository = libroRepository;
        this.autorRepository = autorRepository;
        this.generoRepository = generoRepository;
    }

    public List<Saga> obtenerSagas() {
        return sagaRepository.findAll();
    }

    /** Detalle de la saga: libros en orden de lectura, autores (Saga_Autor) y géneros (Saga_Genero). */
    public Saga obtenerSaga(Integer idSaga) {
        Saga saga = sagaRepository.findById(idSaga)
                .orElseThrow(() -> new RecursoNoEncontradoException("Saga", idSaga));
        List<Libro> libros = libroRepository.findBySagaId(idSaga);
        saga.setLibros(libros);
        saga.setAutores(autorRepository.findBySagaId(idSaga));
        saga.setGeneros(generoRepository.findBySagaId(idSaga));
        return saga;
    }
}
