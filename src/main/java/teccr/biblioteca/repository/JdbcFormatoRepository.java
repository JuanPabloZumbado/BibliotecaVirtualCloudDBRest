package teccr.biblioteca.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import teccr.biblioteca.data.Formato;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class JdbcFormatoRepository implements FormatoRepository {

    private static final String SELECT_COLUMNS = "id_formato, tipo_formato";
    private static final RowMapper<Formato> FORMATO_ROW_MAPPER = JdbcFormatoRepository::mapFormato;

    private final JdbcTemplate jdbcTemplate;

    public JdbcFormatoRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Formato> findAll() {
        return jdbcTemplate.query("SELECT " + SELECT_COLUMNS + " FROM Formato ORDER BY id_formato",
                FORMATO_ROW_MAPPER);
    }

    @Override
    public Optional<Formato> findById(Integer idFormato) {
        return jdbcTemplate.query("SELECT " + SELECT_COLUMNS + " FROM Formato WHERE id_formato = ?",
                        FORMATO_ROW_MAPPER, idFormato)
                .stream()
                .findFirst();
    }

    private static Formato mapFormato(ResultSet resultSet, int rowNumber) throws SQLException {
        Formato formato = new Formato();
        formato.setIdFormato(resultSet.getInt("id_formato"));
        formato.setTipoFormato(resultSet.getString("tipo_formato"));
        return formato;
    }
}
