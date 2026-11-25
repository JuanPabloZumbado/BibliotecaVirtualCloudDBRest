package teccr.biblioteca.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import teccr.biblioteca.data.SesionLectura;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Repository
public class JdbcSesionLecturaRepository implements SesionLecturaRepository {

    // Se lee de la vista vw_sesion_paginas, que ya incluye la columna calculada pag_leidas
    private static final String SELECT_SESION =
            "SELECT id_sesion, id_lectura, pag_inicio, pag_fin, pag_leidas, duracion, cap_actual FROM vw_sesion_paginas";
    private static final String INSERT_SESION = """
            INSERT INTO Sesion_Lectura (id_lectura, pag_inicio, pag_fin, duracion, cap_actual)
            VALUES (?, ?, ?, ?, ?)
            """;
    private static final RowMapper<SesionLectura> SESION_ROW_MAPPER = JdbcSesionLecturaRepository::mapSesion;

    private final JdbcTemplate jdbcTemplate;

    public JdbcSesionLecturaRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<SesionLectura> findByLecturaId(Integer idLectura) {
        return jdbcTemplate.query(SELECT_SESION + " WHERE id_lectura = ? ORDER BY id_sesion",
                SESION_ROW_MAPPER, idLectura);
    }

    @Override
    public Optional<SesionLectura> findById(Integer idSesion) {
        return jdbcTemplate.query(SELECT_SESION + " WHERE id_sesion = ?", SESION_ROW_MAPPER, idSesion)
                .stream()
                .findFirst();
    }

    @Override
    public int findUltimaPagina(Integer idLectura) {
        Integer ultima = jdbcTemplate.queryForObject(
                "SELECT COALESCE(MAX(pag_fin), 0) FROM Sesion_Lectura WHERE id_lectura = ?",
                Integer.class, idLectura);
        return ultima == null ? 0 : ultima;
    }

    @Override
    public SesionLectura insert(SesionLectura sesion) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(INSERT_SESION, Statement.RETURN_GENERATED_KEYS);
            statement.setInt(1, sesion.getIdLectura());
            statement.setInt(2, sesion.getPagInicio());
            statement.setInt(3, sesion.getPagFin());
            statement.setInt(4, sesion.getDuracion());
            statement.setInt(5, sesion.getCapActual());
            return statement;
        }, keyHolder);
        Number key = keyHolder.getKey();
        if (key == null) {
            throw new IllegalStateException("La base de datos no devolvió el id de la sesión insertada");
        }
        sesion.setIdSesion(key.intValue());
        sesion.setPagLeidas(sesion.getPagFin() - sesion.getPagInicio());
        return sesion;
    }

    @Override
    public void deleteById(Integer idSesion) {
        jdbcTemplate.update("DELETE FROM Sesion_Lectura WHERE id_sesion = ?", idSesion);
    }

    private static SesionLectura mapSesion(ResultSet resultSet, int rowNumber) throws SQLException {
        SesionLectura sesion = new SesionLectura();
        sesion.setIdSesion(resultSet.getInt("id_sesion"));
        sesion.setIdLectura(resultSet.getInt("id_lectura"));
        sesion.setPagInicio(resultSet.getInt("pag_inicio"));
        sesion.setPagFin(resultSet.getInt("pag_fin"));
        sesion.setPagLeidas(resultSet.getInt("pag_leidas"));
        sesion.setDuracion(resultSet.getInt("duracion"));
        sesion.setCapActual(resultSet.getInt("cap_actual"));
        return sesion;
    }
}
