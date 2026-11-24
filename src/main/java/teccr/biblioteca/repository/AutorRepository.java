package teccr.biblioteca.repository;

import teccr.biblioteca.data.Autor;

import java.util.List;
import java.util.Optional;

public interface AutorRepository {

    List<Autor> findAll();

    Optional<Autor> findById(Integer idAutor);

    /** Autores de un libro, incluyendo su rol (tabla Libro_Autor). */
    List<Autor> findByLibroId(Integer idLibro);

    /** Autores de una saga (tabla Saga_Autor). */
    List<Autor> findBySagaId(Integer idSaga);
}
