package teccr.biblioteca.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import teccr.biblioteca.data.Usuario;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

@Repository
public class JdbcUsuarioRepository implements UsuarioRepository {

    private static final String SELECT_COLUMNS = "id_usuario, uNombre, correo, fecha_registro";
    private static final String UPDATE_USUARIO = """
            UPDATE Usuario
            SET uNombre = ?, correo = ?
            WHERE id_usuario = ?
            """;
    private static final RowMapper<Usuario> USUARIO_ROW_MAPPER = JdbcUsuarioRepository::mapUsuario;

    private final JdbcTemplate jdbcTemplate;

    public JdbcUsuarioRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<Usuario> findById(Integer idUsuario) {
        return jdbcTemplate.query("SELECT " + SELECT_COLUMNS + " FROM Usuario WHERE id_usuario = ?",
                        USUARIO_ROW_MAPPER, idUsuario)
                .stream()
                .findFirst();
    }

    @Override
    public Optional<Usuario> findByCorreo(String correo) {
        return jdbcTemplate.query("SELECT " + SELECT_COLUMNS + " FROM Usuario WHERE correo = ?",
                        USUARIO_ROW_MAPPER, correo)
                .stream()
                .findFirst();
    }

    @Override
    public Usuario update(Usuario usuario) {
        jdbcTemplate.update(UPDATE_USUARIO, usuario.getNombre(), usuario.getCorreo(), usuario.getIdUsuario());
        return usuario;
    }

    private static Usuario mapUsuario(ResultSet resultSet, int rowNumber) throws SQLException {
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(resultSet.getInt("id_usuario"));
        usuario.setNombre(resultSet.getString("uNombre"));
        usuario.setCorreo(resultSet.getString("correo"));
        Date fechaRegistro = resultSet.getDate("fecha_registro");
        usuario.setFechaRegistro(fechaRegistro == null ? null : fechaRegistro.toLocalDate());
        return usuario;
    }
}
