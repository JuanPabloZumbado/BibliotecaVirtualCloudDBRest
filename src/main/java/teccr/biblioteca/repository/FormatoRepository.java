package teccr.biblioteca.repository;

import teccr.biblioteca.data.Formato;

import java.util.List;
import java.util.Optional;

public interface FormatoRepository {

    List<Formato> findAll();

    Optional<Formato> findById(Integer idFormato);
}
