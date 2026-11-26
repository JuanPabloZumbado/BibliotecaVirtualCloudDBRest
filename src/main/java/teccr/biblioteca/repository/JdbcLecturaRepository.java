package teccr.biblioteca.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import teccr.biblioteca.data.Lectura;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class JdbcLecturaRepository implements LecturaRepository {

    // Une la lectura con su edición, libro y formato, y resume sus sesiones usando la vista vw_sesion_paginas
    private static final String SELECT_LECTURA = """
            SELECT le.id_lectura, le.id_usuario, le.id_edicion, le.id_formato,
                   le.fecha_inicio, le.fecha_fin, le.estado,
                   e.id_libro, l.titulo, e.tipo AS tipo_edicion, e.editorial, e.tot_paginas,
                   f.tipo_formato,
                   COALESCE(s.pagina_actual, 0)  AS pagina_actual,
                   COALESCE(s.paginas_leidas, 0) AS paginas_leidas,
                   COALESCE(s.minutos_leidos, 0) AS minutos_leidos
            FROM Lectura le
            JOIN Edicion e ON e.id_edicion = le.id_edicion
            JOIN Libro l   ON l.id_libro   = e.id_libro
            JOIN Formato f ON f.id_formato = le.id_formato
            LEFT JOIN (SELECT id_lectura,
                              MAX(pag_fin)    AS pagina_actual,
                              SUM(pag_leidas) AS paginas_leidas,
                              SUM(duracion)   AS minutos_leidos
                       FROM vw_sesion_paginas
                       GROUP BY id_lectura) s ON s.id_lectura = le.id_lectura
            """;
    private static final String INSERT_LECTURA = """
            INSERT INTO Lectura (id_usuario, id_edicion, id_formato, fecha_inicio, fecha_fin, estado)
            VALUES (?, ?, ?, ?, ?, ?)
            """;
    private static final String UPDATE_LECTURA = """
            UPDATE Lectura
            SET id_formato = ?, fecha_inicio = ?, fecha_fin = ?, estado = ?
            WHERE id_lectura = ?
            """;
    private static final RowMapper<Lectura> LECTURA_ROW_MAPPER = JdbcLecturaRepository::mapLectura;

    private final JdbcTemplate jdbcTemplate;

    public JdbcLecturaRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Lectura> findByUsuarioId(Integer idUsuario, String estado) {
        StringBuilder sql = new StringBuilder(SELECT_LECTURA).append(" WHERE le.id_usuario = ?");
        List<Object> params = new ArrayList<>();
        params.add(idUsuario);
        if (estado != null) {
            sql.append(" AND le.estado = ?");
            params.add(estado);
        }
        sql.append(" ORDER BY le.fecha_inicio DESC, le.id_lectura DESC");
        return jdbcTemplate.query(sql.toString(), LECTURA_ROW_MAPPER, params.toArray());
    }

    @Override
    public Optional<Lectura> findById(Integer idLectura) {
        return jdbcTemplate.query(SELECT_LECTURA + " WHERE le.id_lectura = ?", LECTURA_ROW_MAPPER, idLectura)
                .stream()
                .findFirst();
    }

    @Override
    public boolean existeLecturaActiva(Integer idUsuario, Integer idEdicion) {
        Integer cantidad = jdbcTemplate.queryForObject("""
                SELECT COUNT(*) FROM Lectura
                WHERE id_usuario = ? AND id_edicion = ? AND estado IN ('por iniciar', 'en curso')
                """, Integer.class, idUsuario, idEdicion);
        return cantidad != null && cantidad > 0;
    }

    @Override
    public Lectura insert(Lectura lectura) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(INSERT_LECTURA, Statement.RETURN_GENERATED_KEYS);
            statement.setInt(1, lectura.getIdUsuario());
            statement.setInt(2, lectura.getIdEdicion());
            statement.setInt(3, lectura.getIdFormato());
            statement.setDate(4, Date.valueOf(lectura.getFechaInicio()));
            statement.setDate(5, lectura.getFechaFin() == null ? null : Date.valueOf(lectura.getFechaFin()));
            statement.setString(6, lectura.getEstado());
            return statement;
        }, keyHolder);
        Number key = keyHolder.getKey();
        if (key == null) {
            throw new IllegalStateException("La base de datos no devolvió el id de la lectura insertada");
        }
        lectura.setIdLectura(key.intValue());
        return lectura;
    }

    @Override
    public void update(Lectura lectura) {
        jdbcTemplate.update(UPDATE_LECTURA,
                lectura.getIdFormato(),
                Date.valueOf(lectura.getFechaInicio()),
                lectura.getFechaFin() == null ? null : Date.valueOf(lectura.getFechaFin()),
                lectura.getEstado(),
                lectura.getIdLectura());
    }

    @Override
    public void deleteById(Integer idLectura) {
        // Las sesiones y valoraciones de la lectura se borran por ON DELETE CASCADE
        jdbcTemplate.update("DELETE FROM Lectura WHERE id_lectura = ?", idLectura);
    }

    private static Lectura mapLectura(ResultSet resultSet, int rowNumber) throws SQLException {
        Lectura lectura = new Lectura();
        lectura.setIdLectura(resultSet.getInt("id_lectura"));
        lectura.setIdUsuario(resultSet.getInt("id_usuario"));
        lectura.setIdEdicion(resultSet.getInt("id_edicion"));
        lectura.setIdFormato(resultSet.getInt("id_formato"));
        lectura.setFechaInicio(resultSet.getDate("fecha_inicio").toLocalDate());
        Date fechaFin = resultSet.getDate("fecha_fin");
        lectura.setFechaFin(fechaFin == null ? null : fechaFin.toLocalDate());
        lectura.setEstado(resultSet.getString("estado"));
        lectura.setIdLibro(resultSet.getInt("id_libro"));
        lectura.setTitulo(resultSet.getString("titulo"));
        lectura.setTipoEdicion(resultSet.getString("tipo_edicion"));
        lectura.setEditorial(resultSet.getString("editorial"));
        lectura.setTotPaginas(resultSet.getInt("tot_paginas"));
        lectura.setTipoFormato(resultSet.getString("tipo_formato"));
        lectura.setPaginaActual(resultSet.getInt("pagina_actual"));
        lectura.setPaginasLeidas(resultSet.getInt("paginas_leidas"));
        lectura.setMinutosLeidos(resultSet.getInt("minutos_leidos"));
        return lectura;
    }
}
