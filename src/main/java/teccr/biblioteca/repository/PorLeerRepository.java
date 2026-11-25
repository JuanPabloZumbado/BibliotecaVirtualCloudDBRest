package teccr.biblioteca.repository;

import teccr.biblioteca.data.LibroPorLeer;

import java.util.List;
import java.util.Optional;

public interface PorLeerRepository {

    List<LibroPorLeer> findByUsuarioId(Integer idUsuario, String prioridad);

    Optional<LibroPorLeer> findById(Integer idUsuario, Integer idLibro);

    void insert(LibroPorLeer libroPorLeer);

    void updatePrioridad(Integer idUsuario, Integer idLibro, String prioridad);

    void delete(Integer idUsuario, Integer idLibro);
}
