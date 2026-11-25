package teccr.biblioteca.repository;

import teccr.biblioteca.data.Lectura;

import java.util.List;
import java.util.Optional;

public interface LecturaRepository {

    /** Lecturas de un usuario; si estado no es null, solo las de ese estado. */
    List<Lectura> findByUsuarioId(Integer idUsuario, String estado);

    Optional<Lectura> findById(Integer idLectura);

    /** true si el usuario ya tiene una lectura activa (por iniciar / en curso) de esa edición. */
    boolean existeLecturaActiva(Integer idUsuario, Integer idEdicion);

    Lectura insert(Lectura lectura);

    /** Actualiza formato, fechas y estado. */
    void update(Lectura lectura);

    void deleteById(Integer idLectura);
}
