package teccr.biblioteca.repository;

import teccr.biblioteca.data.SesionLectura;

import java.util.List;
import java.util.Optional;

public interface SesionLecturaRepository {

    List<SesionLectura> findByLecturaId(Integer idLectura);

    Optional<SesionLectura> findById(Integer idSesion);

    /** Mayor pag_fin registrado para la lectura (0 si no tiene sesiones). */
    int findUltimaPagina(Integer idLectura);

    SesionLectura insert(SesionLectura sesion);

    void deleteById(Integer idSesion);
}
