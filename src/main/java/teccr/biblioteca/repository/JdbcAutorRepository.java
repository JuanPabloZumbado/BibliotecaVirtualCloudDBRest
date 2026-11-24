package teccr.biblioteca.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import teccr.biblioteca.data.Autor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class JdbcAutorRepository implements AutorRepository {

    private static final String SELECT_COLUMNS = "a.id_autor, a.aNombre, a.nacionalidad";
    private static final RowMapper<Autor> AUTOR_ROW_MAPPER = JdbcAutorRepository::mapAutor;

    private final JdbcTemplate jdbcTemplate;

    public JdbcAutorRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Autor> findAll() {
        return jdbcTemplate.query("SELECT " + SELECT_COLUMNS + " FROM Autor a ORDER BY a.aNombre",
                AUTOR_ROW_MAPPER);
    }

    @Override
    public Optional<Autor> findById(Integer idAutor) {
        return jdbcTemplate.query("SELECT " + SELECT_COLUMNS + " FROM Autor a WHERE a.id_autor = ?",
                        AUTOR_ROW_MAPPER, idAutor)
                .stream()
                .findFirst();
    }

    @Override
    public List<Autor> findByLibroId(Integer idLibro) {
        String sql = """
                SELECT a.id_autor, a.aNombre, a.nacionalidad, la.rol
                FROM Libro_Autor la
                JOIN Autor a ON a.id_autor = la.id_autor
                WHERE la.id_libro = ?
                ORDER BY a.aNombre
                """;
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Autor autor = mapAutor(rs, rowNum);
            autor.setRol(rs.getString("rol"));
            return autor;
        }, idLibro);
    }

    @Override
    public List<Autor> findBySagaId(Integer idSaga) {
        String sql = "SELECT " + SELECT_COLUMNS + """
                 FROM Saga_Autor sa
                JOIN Autor a ON a.id_autor = sa.id_autor
                WHERE sa.id_saga = ?
                ORDER BY a.aNombre
                """;
        return jdbcTemplate.query(sql, AUTOR_ROW_MAPPER, idSaga);
    }

    private static Autor mapAutor(ResultSet resultSet, int rowNumber) throws SQLException {
        Autor autor = new Autor();
        autor.setIdAutor(resultSet.getInt("id_autor"));
        autor.setNombre(resultSet.getString("aNombre"));
        autor.setNacionalidad(resultSet.getString("nacionalidad"));
        return autor;
    }
}
