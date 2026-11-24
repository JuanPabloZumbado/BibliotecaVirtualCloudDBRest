package teccr.biblioteca.service;

import org.springframework.stereotype.Service;
import teccr.biblioteca.data.Usuario;
import teccr.biblioteca.exception.ConflictoException;
import teccr.biblioteca.exception.RecursoNoEncontradoException;
import teccr.biblioteca.repository.UsuarioRepository;

/**
 * Lógica de usuarios: inicio de sesión por correo y edición del perfil.
 * Los usuarios ya existen en la base de datos (no hay registro ni administración).
 */
@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public Usuario obtenerUsuario(Integer idUsuario) {
        return usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario", idUsuario));
    }

    /** Inicio de sesión: busca al usuario por su correo (sin contraseña). */
    public Usuario obtenerPorCorreo(String correo) {
        if (correo == null || correo.isBlank()) {
            throw new IllegalArgumentException("El correo es obligatorio");
        }
        String correoLimpio = correo.trim();
        return usuarioRepository.findByCorreo(correoLimpio)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe un usuario con el correo " + correoLimpio));
    }

    /** Actualiza solo los campos enviados (nombre y/o correo). */
    public Usuario actualizarPerfil(Integer idUsuario, Usuario cambios) {
        Usuario usuario = obtenerUsuario(idUsuario);

        if (cambios.getNombre() != null) {
            String nombre = cambios.getNombre().trim();
            if (nombre.isEmpty()) {
                throw new IllegalArgumentException("El nombre no puede estar vacío");
            }
            usuario.setNombre(nombre);
        }

        if (cambios.getCorreo() != null) {
            String correo = cambios.getCorreo().trim();
            if (!correo.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
                throw new IllegalArgumentException("El correo no tiene un formato válido");
            }
            // El correo es único: no puede pertenecer a otro usuario
            usuarioRepository.findByCorreo(correo)
                    .filter(otro -> !otro.getIdUsuario().equals(idUsuario))
                    .ifPresent(otro -> {
                        throw new ConflictoException("El correo " + correo + " ya está registrado");
                    });
            usuario.setCorreo(correo);
        }

        return usuarioRepository.update(usuario);
    }

    /** Usado por otros servicios (lecturas, lista por leer) para validar el {idUsuario} de la ruta. */
    public void validarUsuarioExiste(Integer idUsuario) {
        obtenerUsuario(idUsuario);
    }
}
