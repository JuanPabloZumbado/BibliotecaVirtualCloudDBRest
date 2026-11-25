package teccr.biblioteca.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import teccr.biblioteca.data.Conteo;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Repository
public class JdbcEstadisticasRepository implements EstadisticasRepository {

    private static final RowMapper<Conteo> CONTEO_ROW_MAPPER = (resultSet, rowNumber) -> new Conteo(
            resultSet.getInt("id"), resultSet.getString("nombre"), resultSet.getInt("cantidad"));

    private final JdbcTemplate jdbcTemplate;

    public JdbcEstadisticasRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Map<String, Integer> contarLecturasPorEstado(Integer idUsuario) {
        Map<String, Integer> conteo = new LinkedHashMap<>();
        jdbcTemplate.query("SELECT estado, COUNT(*) AS cantidad FROM Lectura WHERE id_usuario = ? GROUP BY estado",
                resultSet -> {
                    conteo.put(resultSet.getString("estado"), resultSet.getInt("cantidad"));
                }, idUsuario);
        return conteo;
    }

    @Override
    public int contarLibrosTerminados(Integer idUsuario) {
        return contar("""
                SELECT COUNT(DISTINCT e.id_libro)
                FROM Lectura l
                JOIN Edicion e ON e.id_edicion = l.id_edicion
                WHERE l.id_usuario = ? AND l.estado = 'terminada'
                """, idUsuario);
    }

    @Override
    public int[] resumirSesiones(Integer idUsuario) {
        return jdbcTemplate.queryForObject("""
                SELECT COUNT(*) AS sesiones,
                       COALESCE(SUM(s.pag_leidas), 0) AS paginas,
                       COALESCE(SUM(s.duracion), 0)   AS minutos
                FROM vw_sesion_paginas s
                JOIN Lectura l ON l.id_lectura = s.id_lectura
                WHERE l.id_usuario = ?
                """,
                (resultSet, rowNumber) -> new int[]{
                        resultSet.getInt("sesiones"), resultSet.getInt("paginas"), resultSet.getInt("minutos")},
                idUsuario);
    }

    @Override
    public int contarValoraciones(Integer idUsuario) {
        return contar("""
                SELECT COUNT(*)
                FROM Valoracion v
                JOIN Lectura l ON l.id_lectura = v.id_lectura
                WHERE l.id_usuario = ?
                """, idUsuario);
    }

    @Override
    public Double promedioPuntuacion(Integer idUsuario) {
        // AVG devuelve NULL cuando el usuario no tiene valoraciones
        return jdbcTemplate.queryForObject("""
                SELECT ROUND(AVG(v.puntuacion), 2)
                FROM Valoracion v
                JOIN Lectura l ON l.id_lectura = v.id_lectura
                WHERE l.id_usuario = ?
                """, Double.class, idUsuario);
    }

    @Override
    public int contarLibrosPorLeer(Integer idUsuario) {
        return contar("SELECT COUNT(*) FROM Lista_Por_Leer WHERE id_usuario = ?", idUsuario);
    }

    @Override
    public List<Conteo> generosMasLeidos(Integer idUsuario, int limite) {
        return jdbcTemplate.query("""
                SELECT g.id_genero AS id, g.clasificacion AS nombre, COUNT(DISTINCT l.id_lectura) AS cantidad
                FROM Lectura l
                JOIN Edicion e       ON e.id_edicion = l.id_edicion
                JOIN Libro_Genero lg ON lg.id_libro  = e.id_libro
                JOIN Genero g        ON g.id_genero  = lg.id_genero
                WHERE l.id_usuario = ?
                GROUP BY g.id_genero, g.clasificacion
                ORDER BY cantidad DESC, nombre
                LIMIT ?
                """, CONTEO_ROW_MAPPER, idUsuario, limite);
    }

    @Override
    public List<Conteo> autoresMasLeidos(Integer idUsuario, int limite) {
        return jdbcTemplate.query("""
                SELECT a.id_autor AS id, a.aNombre AS nombre, COUNT(DISTINCT l.id_lectura) AS cantidad
                FROM Lectura l
                JOIN Edicion e      ON e.id_edicion = l.id_edicion
                JOIN Libro_Autor la ON la.id_libro  = e.id_libro
                JOIN Autor a        ON a.id_autor   = la.id_autor
                WHERE l.id_usuario = ?
                GROUP BY a.id_autor, a.aNombre
                ORDER BY cantidad DESC, nombre
                LIMIT ?
                """, CONTEO_ROW_MAPPER, idUsuario, limite);
    }

    @Override
    public List<Conteo> formatosUsados(Integer idUsuario) {
        return jdbcTemplate.query("""
                SELECT f.id_formato AS id, f.tipo_formato AS nombre, COUNT(*) AS cantidad
                FROM Lectura l
                JOIN Formato f ON f.id_formato = l.id_formato
                WHERE l.id_usuario = ?
                GROUP BY f.id_formato, f.tipo_formato
                ORDER BY cantidad DESC, nombre
                """, CONTEO_ROW_MAPPER, idUsuario);
    }

    private int contar(String sql, Integer idUsuario) {
        Integer total = jdbcTemplate.queryForObject(sql, Integer.class, idUsuario);
        return total == null ? 0 : total;
    }
}
