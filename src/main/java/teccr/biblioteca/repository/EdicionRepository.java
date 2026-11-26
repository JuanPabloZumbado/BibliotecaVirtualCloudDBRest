package teccr.biblioteca.repository;

import teccr.biblioteca.data.Edicion;

import java.util.List;
import java.util.Optional;

public interface EdicionRepository {

    Optional<Edicion> findById(Integer idEdicion);

    List<Edicion> findByLibroId(Integer idLibro);
}
