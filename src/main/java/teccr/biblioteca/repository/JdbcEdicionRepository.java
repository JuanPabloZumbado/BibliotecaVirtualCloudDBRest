package teccr.biblioteca.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import teccr.biblioteca.data.Edicion;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class JdbcEdicionRepository implements EdicionRepository {

    private static final String SELECT_COLUMNS =
            "id_edicion, id_libro, tipo, editorial, isbn, tot_paginas, anno_publicacion";
    private static final RowMapper<Edicion> EDICION_ROW_MAPPER = JdbcEdicionRepository::mapEdicion;

    private final JdbcTemplate jdbcTemplate;

    public JdbcEdicionRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<Edicion> findById(Integer idEdicion) {
        return jdbcTemplate.query("SELECT " + SELECT_COLUMNS + " FROM Edicion WHERE id_edicion = ?",
                        EDICION_ROW_MAPPER, idEdicion)
                .stream()
                .findFirst();
    }

    @Override
    public List<Edicion> findByLibroId(Integer idLibro) {
        return jdbcTemplate.query(
                "SELECT " + SELECT_COLUMNS + " FROM Edicion WHERE id_libro = ? ORDER BY anno_publicacion",
                EDICION_ROW_MAPPER, idLibro);
    }

    private static Edicion mapEdicion(ResultSet resultSet, int rowNumber) throws SQLException {
        Edicion edicion = new Edicion();
        edicion.setIdEdicion(resultSet.getInt("id_edicion"));
        edicion.setIdLibro(resultSet.getInt("id_libro"));
        edicion.setTipo(resultSet.getString("tipo"));
        edicion.setEditorial(resultSet.getString("editorial"));
        edicion.setIsbn(resultSet.getString("isbn"));
        edicion.setTotPaginas(resultSet.getInt("tot_paginas"));
        Date anno = resultSet.getDate("anno_publicacion");
        edicion.setAnnoPublicacion(anno == null ? null : anno.toLocalDate());
        return edicion;
    }
}
