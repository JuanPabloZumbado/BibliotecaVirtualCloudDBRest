package teccr.biblioteca.repository;

import teccr.biblioteca.data.Usuario;

import java.util.Optional;

public interface UsuarioRepository {

    Optional<Usuario> findById(Integer idUsuario);

    Optional<Usuario> findByCorreo(String correo);

    /** Actualiza nombre y correo de un usuario existente. */
    Usuario update(Usuario usuario);
}
