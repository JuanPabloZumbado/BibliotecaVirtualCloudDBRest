package teccr.biblioteca.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import teccr.biblioteca.data.Valoracion;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Repository
public class JdbcValoracionRepository implements ValoracionRepository {

    // La valoración se une con su lectura para saber quién la escribió y de qué libro es
    private static final String SELECT_VALORACION = """
            SELECT v.id_valoracion, v.id_lectura, v.puntuacion, v.resenna, v.fecha,
                   le.id_usuario, u.uNombre, ed.id_libro, li.titulo
            FROM Valoracion v
            JOIN Lectura le ON le.id_lectura = v.id_lectura
            JOIN Usuario u  ON u.id_usuario  = le.id_usuario
            JOIN Edicion ed ON ed.id_edicion = le.id_edicion
            JOIN Libro li   ON li.id_libro   = ed.id_libro
            """;
    private static final String INSERT_VALORACION =
            "INSERT INTO Valoracion (id_lectura, puntuacion, resenna, fecha) VALUES (?, ?, ?, ?)";
    private static final String UPDATE_VALORACION =
            "UPDATE Valoracion SET puntuacion = ?, resenna = ?, fecha = ? WHERE id_valoracion = ?";
    private static final RowMapper<Valoracion> VALORACION_ROW_MAPPER = JdbcValoracionRepository::mapValoracion;

    private final JdbcTemplate jdbcTemplate;

    public JdbcValoracionRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Valoracion> findByLecturaId(Integer idLectura) {
        return jdbcTemplate.query(SELECT_VALORACION + " WHERE v.id_lectura = ? ORDER BY v.fecha DESC, v.id_valoracion DESC",
                VALORACION_ROW_MAPPER, idLectura);
    }

    @Override
    public List<Valoracion> findByLibroId(Integer idLibro) {
        // Incluye las valoraciones de todas las ediciones del libro
        return jdbcTemplate.query(SELECT_VALORACION + " WHERE ed.id_libro = ? ORDER BY v.fecha DESC, v.id_valoracion DESC",
                VALORACION_ROW_MAPPER, idLibro);
    }

    @Override
    public Optional<Valoracion> findById(Integer idValoracion) {
        return jdbcTemplate.query(SELECT_VALORACION + " WHERE v.id_valoracion = ?", VALORACION_ROW_MAPPER, idValoracion)
                .stream()
                .findFirst();
    }

    @Override
    public boolean existeValoracionDeLectura(Integer idLectura) {
        Integer total = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM Valoracion WHERE id_lectura = ?", Integer.class, idLectura);
        return total != null && total > 0;
    }

    @Override
    public Valoracion insert(Valoracion valoracion) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(INSERT_VALORACION, Statement.RETURN_GENERATED_KEYS);
            statement.setInt(1, valoracion.getIdLectura());
            statement.setInt(2, valoracion.getPuntuacion());
            statement.setString(3, valoracion.getResenna());
            statement.setDate(4, Date.valueOf(valoracion.getFecha()));
            return statement;
        }, keyHolder);
        Number key = keyHolder.getKey();
        if (key == null) {
            throw new IllegalStateException("La base de datos no devolvió el id de la valoración insertada");
        }
        valoracion.setIdValoracion(key.intValue());
        return valoracion;
    }

    @Override
    public void update(Valoracion valoracion) {
        jdbcTemplate.update(UPDATE_VALORACION,
                valoracion.getPuntuacion(),
                valoracion.getResenna(),
                Date.valueOf(valoracion.getFecha()),
                valoracion.getIdValoracion());
    }

    @Override
    public void deleteById(Integer idValoracion) {
        jdbcTemplate.update("DELETE FROM Valoracion WHERE id_valoracion = ?", idValoracion);
    }

    private static Valoracion mapValoracion(ResultSet resultSet, int rowNumber) throws SQLException {
        Valoracion valoracion = new Valoracion();
        valoracion.setIdValoracion(resultSet.getInt("id_valoracion"));
        valoracion.setIdLectura(resultSet.getInt("id_lectura"));
        valoracion.setPuntuacion(resultSet.getInt("puntuacion"));
        valoracion.setResenna(resultSet.getString("resenna"));
        valoracion.setFecha(resultSet.getDate("fecha").toLocalDate());
        valoracion.setIdUsuario(resultSet.getInt("id_usuario"));
        valoracion.setNombreUsuario(resultSet.getString("uNombre"));
        valoracion.setIdLibro(resultSet.getInt("id_libro"));
        valoracion.setTitulo(resultSet.getString("titulo"));
        return valoracion;
    }
}
