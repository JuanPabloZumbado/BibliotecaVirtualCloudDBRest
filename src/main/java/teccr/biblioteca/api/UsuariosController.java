package teccr.biblioteca.api;

import org.springframework.web.bind.annotation.*;
import teccr.biblioteca.data.Usuario;
import teccr.biblioteca.service.UsuarioService;

@RestController
@RequestMapping("/api/v1/usuarios")
public class UsuariosController {

    private final UsuarioService usuarioService;

    public UsuariosController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/{id}")
    public Usuario getUsuarioById(@PathVariable Integer id) {
        return usuarioService.obtenerUsuario(id);
    }

    /** Usado por el login del frontend. */
    @GetMapping("/by-correo/{correo}")
    public Usuario getUsuarioByCorreo(@PathVariable String correo) {
        return usuarioService.obtenerPorCorreo(correo);
    }

    @PatchMapping("/{id}")
    public Usuario updateUsuario(@PathVariable Integer id, @RequestBody Usuario usuario) {
        return usuarioService.actualizarPerfil(id, usuario);
    }
}
