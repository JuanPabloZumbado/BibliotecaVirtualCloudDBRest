package teccr.biblioteca.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import teccr.biblioteca.data.Saga;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class JdbcSagaRepository implements SagaRepository {

    private static final String SELECT_COLUMNS = "id_saga, sNombre, descripcion";
    private static final RowMapper<Saga> SAGA_ROW_MAPPER = JdbcSagaRepository::mapSaga;

    private final JdbcTemplate jdbcTemplate;

    public JdbcSagaRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Saga> findAll() {
        return jdbcTemplate.query("SELECT " + SELECT_COLUMNS + " FROM Saga ORDER BY sNombre", SAGA_ROW_MAPPER);
    }

    @Override
    public Optional<Saga> findById(Integer idSaga) {
        return jdbcTemplate.query("SELECT " + SELECT_COLUMNS + " FROM Saga WHERE id_saga = ?",
                        SAGA_ROW_MAPPER, idSaga)
                .stream()
                .findFirst();
    }

    private static Saga mapSaga(ResultSet resultSet, int rowNumber) throws SQLException {
        Saga saga = new Saga();
        saga.setIdSaga(resultSet.getInt("id_saga"));
        saga.setNombre(resultSet.getString("sNombre"));
        saga.setDescripcion(resultSet.getString("descripcion"));
        return saga;
    }
}
