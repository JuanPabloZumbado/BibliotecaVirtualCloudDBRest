package teccr.biblioteca.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import teccr.biblioteca.data.LibroPorLeer;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class JdbcPorLeerRepository implements PorLeerRepository {

    private static final String SELECT_POR_LEER = """
            SELECT p.id_usuario, p.id_libro, p.prioridad, p.fecha_agregado,
                   l.titulo, s.sNombre, l.orden_saga
            FROM Lista_Por_Leer p
            JOIN Libro l     ON l.id_libro = p.id_libro
            LEFT JOIN Saga s ON s.id_saga  = l.id_saga
            """;
    private static final RowMapper<LibroPorLeer> POR_LEER_ROW_MAPPER = JdbcPorLeerRepository::mapLibroPorLeer;

    private final JdbcTemplate jdbcTemplate;

    public JdbcPorLeerRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<LibroPorLeer> findByUsuarioId(Integer idUsuario, String prioridad) {
        StringBuilder sql = new StringBuilder(SELECT_POR_LEER).append(" WHERE p.id_usuario = ?");
        List<Object> params = new ArrayList<>();
        params.add(idUsuario);
        if (prioridad != null) {
            sql.append(" AND p.prioridad = ?");
            params.add(prioridad);
        }
        // Primero la prioridad Alta, luego Media y Baja; dentro de cada una, lo agregado primero
        sql.append(" ORDER BY FIELD(p.prioridad, 'Alta', 'Media', 'Baja'), p.fecha_agregado, l.titulo");
        return jdbcTemplate.query(sql.toString(), POR_LEER_ROW_MAPPER, params.toArray());
    }

    @Override
    public Optional<LibroPorLeer> findById(Integer idUsuario, Integer idLibro) {
        return jdbcTemplate.query(SELECT_POR_LEER + " WHERE p.id_usuario = ? AND p.id_libro = ?",
                        POR_LEER_ROW_MAPPER, idUsuario, idLibro)
                .stream()
                .findFirst();
    }

    @Override
    public void insert(LibroPorLeer libroPorLeer) {
        jdbcTemplate.update(
                "INSERT INTO Lista_Por_Leer (id_usuario, id_libro, prioridad, fecha_agregado) VALUES (?, ?, ?, ?)",
                libroPorLeer.getIdUsuario(),
                libroPorLeer.getIdLibro(),
                libroPorLeer.getPrioridad(),
                Date.valueOf(libroPorLeer.getFechaAgregado()));
    }

    @Override
    public void updatePrioridad(Integer idUsuario, Integer idLibro, String prioridad) {
        jdbcTemplate.update("UPDATE Lista_Por_Leer SET prioridad = ? WHERE id_usuario = ? AND id_libro = ?",
                prioridad, idUsuario, idLibro);
    }

    @Override
    public void delete(Integer idUsuario, Integer idLibro) {
        jdbcTemplate.update("DELETE FROM Lista_Por_Leer WHERE id_usuario = ? AND id_libro = ?", idUsuario, idLibro);
    }

    private static LibroPorLeer mapLibroPorLeer(ResultSet resultSet, int rowNumber) throws SQLException {
        LibroPorLeer libro = new LibroPorLeer();
        libro.setIdUsuario(resultSet.getInt("id_usuario"));
        libro.setIdLibro(resultSet.getInt("id_libro"));
        libro.setPrioridad(resultSet.getString("prioridad"));
        libro.setFechaAgregado(resultSet.getDate("fecha_agregado").toLocalDate());
        libro.setTitulo(resultSet.getString("titulo"));
        libro.setNombreSaga(resultSet.getString("sNombre"));
        libro.setOrdenSaga(resultSet.getObject("orden_saga", Integer.class));
        return libro;
    }
}
