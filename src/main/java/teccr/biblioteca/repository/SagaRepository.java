package teccr.biblioteca.repository;

import teccr.biblioteca.data.Saga;

import java.util.List;
import java.util.Optional;

public interface SagaRepository {

    List<Saga> findAll();

    Optional<Saga> findById(Integer idSaga);
}
