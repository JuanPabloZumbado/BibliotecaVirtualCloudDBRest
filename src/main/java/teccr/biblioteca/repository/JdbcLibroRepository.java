package teccr.biblioteca.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import teccr.biblioteca.data.Libro;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class JdbcLibroRepository implements LibroRepository {

    private static final String SELECT_LIBRO = """
            SELECT l.id_libro, l.titulo, l.id_saga, s.sNombre, l.orden_saga
            FROM Libro l
            LEFT JOIN Saga s ON s.id_saga = l.id_saga
            """;
    private static final RowMapper<Libro> LIBRO_ROW_MAPPER = JdbcLibroRepository::mapLibro;

    private final JdbcTemplate jdbcTemplate;

    public JdbcLibroRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Libro> findAll(Integer idGenero, Integer idAutor, Integer idSaga, String texto) {
        // Se arma el WHERE solo con los filtros recibidos; los valores siempre van como parámetros (?)
        StringBuilder sql = new StringBuilder(SELECT_LIBRO).append(" WHERE 1 = 1");
        List<Object> params = new ArrayList<>();

        if (idGenero != null) {
            sql.append(" AND EXISTS (SELECT 1 FROM Libro_Genero lg WHERE lg.id_libro = l.id_libro AND lg.id_genero = ?)");
            params.add(idGenero);
        }
        if (idAutor != null) {
            sql.append(" AND EXISTS (SELECT 1 FROM Libro_Autor la WHERE la.id_libro = l.id_libro AND la.id_autor = ?)");
            params.add(idAutor);
        }
        if (idSaga != null) {
            sql.append(" AND l.id_saga = ?");
            params.add(idSaga);
        }
        if (texto != null && !texto.isBlank()) {
            String patron = "%" + texto.trim() + "%";
            sql.append("""
                     AND (l.titulo LIKE ?
                          OR EXISTS (SELECT 1 FROM Libro_Autor la2
                                     JOIN Autor a ON a.id_autor = la2.id_autor
                                     WHERE la2.id_libro = l.id_libro AND a.aNombre LIKE ?))
                    """);
            params.add(patron);
            params.add(patron);
        }
        sql.append(" ORDER BY l.titulo");

        return jdbcTemplate.query(sql.toString(), LIBRO_ROW_MAPPER, params.toArray());
    }

    @Override
    public Optional<Libro> findById(Integer idLibro) {
        return jdbcTemplate.query(SELECT_LIBRO + " WHERE l.id_libro = ?", LIBRO_ROW_MAPPER, idLibro)
                .stream()
                .findFirst();
    }

    @Override
    public List<Libro> findBySagaId(Integer idSaga) {
        return jdbcTemplate.query(SELECT_LIBRO + " WHERE l.id_saga = ? ORDER BY l.orden_saga",
                LIBRO_ROW_MAPPER, idSaga);
    }

    private static Libro mapLibro(ResultSet resultSet, int rowNumber) throws SQLException {
        Libro libro = new Libro();
        libro.setIdLibro(resultSet.getInt("id_libro"));
        libro.setTitulo(resultSet.getString("titulo"));
        // getObject devuelve null cuando la columna es NULL (getInt devolvería 0)
        libro.setIdSaga(resultSet.getObject("id_saga", Integer.class));
        libro.setNombreSaga(resultSet.getString("sNombre"));
        libro.setOrdenSaga(resultSet.getObject("orden_saga", Integer.class));
        return libro;
    }
}
