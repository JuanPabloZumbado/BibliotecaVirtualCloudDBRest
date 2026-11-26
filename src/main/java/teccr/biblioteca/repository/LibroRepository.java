package teccr.biblioteca.repository;

import teccr.biblioteca.data.Libro;

import java.util.List;
import java.util.Optional;

public interface LibroRepository {

    /**
     * Busca libros aplicando los filtros que no sean null.
     *
     * @param idGenero solo libros de ese género
     * @param idAutor  solo libros de ese autor
     * @param idSaga   solo libros de esa saga
     * @param texto    texto contenido en el título o en el nombre de un autor
     */
    List<Libro> findAll(Integer idGenero, Integer idAutor, Integer idSaga, String texto);

    Optional<Libro> findById(Integer idLibro);

    /** Libros de una saga en el orden de lectura (orden_saga). */
    List<Libro> findBySagaId(Integer idSaga);
}
