package teccr.biblioteca.service;

import org.springframework.stereotype.Service;
import teccr.biblioteca.data.LibroPorLeer;
import teccr.biblioteca.exception.ConflictoException;
import teccr.biblioteca.exception.RecursoNoEncontradoException;
import teccr.biblioteca.repository.PorLeerRepository;

import java.time.LocalDate;
import java.util.List;

/**
 * Lógica de la lista "por leer": libros que el usuario quiere leer más adelante, con una prioridad.
 */
@Service
public class PorLeerService {

    private static final List<String> PRIORIDADES = List.of("Alta", "Media", "Baja");

    private final PorLeerRepository porLeerRepository;
    private final UsuarioService usuarioService;
    private final LibroService libroService;

    public PorLeerService(PorLeerRepository porLeerRepository,
                          UsuarioService usuarioService,
                          LibroService libroService) {
        this.porLeerRepository = porLeerRepository;
        this.usuarioService = usuarioService;
        this.libroService = libroService;
    }

    /** Lista del usuario, ordenada por prioridad. Filtro opcional por prioridad. */
    public List<LibroPorLeer> obtenerLista(Integer idUsuario, String prioridad) {
        usuarioService.validarUsuarioExiste(idUsuario);
        String filtro = prioridad == null || prioridad.isBlank() ? null : validarPrioridad(prioridad);
        return porLeerRepository.findByUsuarioId(idUsuario, filtro);
    }

    public LibroPorLeer obtenerLibroDeLista(Integer idUsuario, Integer idLibro) {
        usuarioService.validarUsuarioExiste(idUsuario);
        return porLeerRepository.findById(idUsuario, idLibro)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "El libro " + idLibro + " no está en la lista por leer del usuario " + idUsuario));
    }

    /** Agrega un libro. Obligatorio: idLibro. Opcional: prioridad (por defecto "Media"). */
    public LibroPorLeer agregarLibro(Integer idUsuario, LibroPorLeer datos) {
        usuarioService.validarUsuarioExiste(idUsuario);

        if (datos.getIdLibro() == null) {
            throw new IllegalArgumentException("idLibro es obligatorio");
        }
        libroService.validarLibroExiste(datos.getIdLibro());
        if (porLeerRepository.findById(idUsuario, datos.getIdLibro()).isPresent()) {
            throw new ConflictoException("El libro " + datos.getIdLibro() + " ya está en la lista por leer");
        }

        LibroPorLeer nuevo = new LibroPorLeer();
        nuevo.setIdUsuario(idUsuario);
        nuevo.setIdLibro(datos.getIdLibro());
        nuevo.setPrioridad(datos.getPrioridad() == null ? "Media" : validarPrioridad(datos.getPrioridad()));
        nuevo.setFechaAgregado(LocalDate.now());
        porLeerRepository.insert(nuevo);

        return obtenerLibroDeLista(idUsuario, datos.getIdLibro());
    }

    public LibroPorLeer cambiarPrioridad(Integer idUsuario, Integer idLibro, LibroPorLeer cambios) {
        obtenerLibroDeLista(idUsuario, idLibro);
        if (cambios.getPrioridad() == null) {
            throw new IllegalArgumentException("prioridad es obligatoria");
        }
        porLeerRepository.updatePrioridad(idUsuario, idLibro, validarPrioridad(cambios.getPrioridad()));
        return obtenerLibroDeLista(idUsuario, idLibro);
    }

    public void quitarLibro(Integer idUsuario, Integer idLibro) {
        obtenerLibroDeLista(idUsuario, idLibro);
        porLeerRepository.delete(idUsuario, idLibro);
    }

    /** Acepta la prioridad sin importar mayúsculas ("alta", "ALTA") y la guarda como "Alta". */
    private static String validarPrioridad(String prioridad) {
        String limpia = prioridad.trim();
        return PRIORIDADES.stream()
                .filter(valida -> valida.equalsIgnoreCase(limpia))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Prioridad inválida. Valores permitidos: Alta, Media, Baja"));
    }
}
