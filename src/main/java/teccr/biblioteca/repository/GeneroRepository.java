package teccr.biblioteca.repository;

import teccr.biblioteca.data.Genero;

import java.util.List;
import java.util.Optional;

public interface GeneroRepository {

    List<Genero> findAll();

    Optional<Genero> findById(Integer idGenero);

    /** Géneros de un libro (tabla Libro_Genero). */
    List<Genero> findByLibroId(Integer idLibro);

    /** Géneros de una saga (tabla Saga_Genero). */
    List<Genero> findBySagaId(Integer idSaga);
}
