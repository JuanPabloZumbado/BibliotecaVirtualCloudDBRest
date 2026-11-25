package teccr.biblioteca.repository;

import teccr.biblioteca.data.Valoracion;

import java.util.List;
import java.util.Optional;

public interface ValoracionRepository {

    List<Valoracion> findByLecturaId(Integer idLectura);

    List<Valoracion> findByLibroId(Integer idLibro);

    Optional<Valoracion> findById(Integer idValoracion);

    boolean existeValoracionDeLectura(Integer idLectura);

    Valoracion insert(Valoracion valoracion);

    void update(Valoracion valoracion);

    void deleteById(Integer idValoracion);
}
