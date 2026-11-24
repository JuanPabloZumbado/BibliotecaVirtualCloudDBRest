package teccr.biblioteca.service;

import org.springframework.stereotype.Service;
import teccr.biblioteca.data.Autor;
import teccr.biblioteca.data.Libro;
import teccr.biblioteca.exception.RecursoNoEncontradoException;
import teccr.biblioteca.repository.AutorRepository;

import java.util.List;

@Service
public class AutorService {

    private final AutorRepository autorRepository;
    private final LibroService libroService;

    public AutorService(AutorRepository autorRepository, LibroService libroService) {
        this.autorRepository = autorRepository;
        this.libroService = libroService;
    }

    public List<Autor> obtenerAutores() {
        return autorRepository.findAll();
    }

    public Autor obtenerAutor(Integer idAutor) {
        return autorRepository.findById(idAutor)
                .orElseThrow(() -> new RecursoNoEncontradoException("Autor", idAutor));
    }

    public List<Libro> obtenerLibrosDeAutor(Integer idAutor) {
        obtenerAutor(idAutor); // valida que exista
        return libroService.buscarLibros(null, idAutor, null, null);
    }
}
