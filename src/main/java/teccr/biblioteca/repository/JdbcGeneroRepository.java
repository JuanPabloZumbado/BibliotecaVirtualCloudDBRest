package teccr.biblioteca.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import teccr.biblioteca.data.Genero;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class JdbcGeneroRepository implements GeneroRepository {

    private static final String SELECT_COLUMNS = "g.id_genero, g.clasificacion";
    private static final RowMapper<Genero> GENERO_ROW_MAPPER = JdbcGeneroRepository::mapGenero;

    private final JdbcTemplate jdbcTemplate;

    public JdbcGeneroRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Genero> findAll() {
        return jdbcTemplate.query("SELECT " + SELECT_COLUMNS + " FROM Genero g ORDER BY g.clasificacion",
                GENERO_ROW_MAPPER);
    }

    @Override
    public Optional<Genero> findById(Integer idGenero) {
        return jdbcTemplate.query("SELECT " + SELECT_COLUMNS + " FROM Genero g WHERE g.id_genero = ?",
                        GENERO_ROW_MAPPER, idGenero)
                .stream()
                .findFirst();
    }

    @Override
    public List<Genero> findByLibroId(Integer idLibro) {
        String sql = "SELECT " + SELECT_COLUMNS + """
                 FROM Libro_Genero lg
                JOIN Genero g ON g.id_genero = lg.id_genero
                WHERE lg.id_libro = ?
                ORDER BY g.clasificacion
                """;
        return jdbcTemplate.query(sql, GENERO_ROW_MAPPER, idLibro);
    }

    @Override
    public List<Genero> findBySagaId(Integer idSaga) {
        String sql = "SELECT " + SELECT_COLUMNS + """
                 FROM Saga_Genero sg
                JOIN Genero g ON g.id_genero = sg.id_genero
                WHERE sg.id_saga = ?
                ORDER BY g.clasificacion
                """;
        return jdbcTemplate.query(sql, GENERO_ROW_MAPPER, idSaga);
    }

    private static Genero mapGenero(ResultSet resultSet, int rowNumber) throws SQLException {
        Genero genero = new Genero();
        genero.setIdGenero(resultSet.getInt("id_genero"));
        genero.setClasificacion(resultSet.getString("clasificacion"));
        return genero;
    }
}
