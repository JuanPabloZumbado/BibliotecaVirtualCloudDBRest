package teccr.biblioteca.service;

import org.springframework.stereotype.Service;
import teccr.biblioteca.data.Edicion;
import teccr.biblioteca.data.Libro;
import teccr.biblioteca.exception.RecursoNoEncontradoException;
import teccr.biblioteca.repository.AutorRepository;
import teccr.biblioteca.repository.EdicionRepository;
import teccr.biblioteca.repository.GeneroRepository;
import teccr.biblioteca.repository.LibroRepository;

import java.util.List;

/**
 * Lógica del catálogo de libros: arma cada libro con sus autores, géneros y ediciones.
 */
@Service
public class LibroService {

    private final LibroRepository libroRepository;
    private final AutorRepository autorRepository;
    private final GeneroRepository generoRepository;
    private final EdicionRepository edicionRepository;

    public LibroService(LibroRepository libroRepository,
                        AutorRepository autorRepository,
                        GeneroRepository generoRepository,
                        EdicionRepository edicionRepository) {
        this.libroRepository = libroRepository;
        this.autorRepository = autorRepository;
        this.generoRepository = generoRepository;
        this.edicionRepository = edicionRepository;
    }

    /** Lista de libros con autores y géneros, filtrada por los parámetros que no sean null. */
    public List<Libro> buscarLibros(Integer idGenero, Integer idAutor, Integer idSaga, String texto) {
        List<Libro> libros = libroRepository.findAll(idGenero, idAutor, idSaga, texto);
        libros.forEach(this::cargarAutoresYGeneros);
        return libros;
    }

    /** Detalle completo de un libro: autores, géneros y ediciones. */
    public Libro obtenerLibro(Integer idLibro) {
        Libro libro = libroRepository.findById(idLibro)
                .orElseThrow(() -> new RecursoNoEncontradoException("Libro", idLibro));
        cargarAutoresYGeneros(libro);
        libro.setEdiciones(edicionRepository.findByLibroId(idLibro));
        return libro;
    }

    public List<Edicion> obtenerEdiciones(Integer idLibro) {
        validarLibroExiste(idLibro);
        return edicionRepository.findByLibroId(idLibro);
    }

    public Edicion obtenerEdicion(Integer idEdicion) {
        return edicionRepository.findById(idEdicion)
                .orElseThrow(() -> new RecursoNoEncontradoException("Edición", idEdicion));
    }

    public void validarLibroExiste(Integer idLibro) {
        if (libroRepository.findById(idLibro).isEmpty()) {
            throw new RecursoNoEncontradoException("Libro", idLibro);
        }
    }

    private void cargarAutoresYGeneros(Libro libro) {
        libro.setAutores(autorRepository.findByLibroId(libro.getIdLibro()));
        libro.setGeneros(generoRepository.findByLibroId(libro.getIdLibro()));
    }
}
