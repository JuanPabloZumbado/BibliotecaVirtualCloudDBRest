package teccr.biblioteca.service;

import org.springframework.stereotype.Service;
import teccr.biblioteca.data.Lectura;
import teccr.biblioteca.exception.ConflictoException;
import teccr.biblioteca.exception.RecursoNoEncontradoException;
import teccr.biblioteca.repository.EdicionRepository;
import teccr.biblioteca.repository.FormatoRepository;
import teccr.biblioteca.repository.LecturaRepository;
import teccr.biblioteca.repository.SesionLecturaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

/**
 * Lógica de las lecturas de un usuario: iniciar, cambiar estado, terminar y eliminar.
 */
@Service
public class LecturaService {

    public static final String POR_INICIAR = "por iniciar";
    public static final String EN_CURSO = "en curso";
    public static final String TERMINADA = "terminada";
    public static final String ABANDONADA = "abandonada";
    private static final Set<String> ESTADOS = Set.of(POR_INICIAR, EN_CURSO, TERMINADA, ABANDONADA);

    private final LecturaRepository lecturaRepository;
    private final SesionLecturaRepository sesionRepository;
    private final EdicionRepository edicionRepository;
    private final FormatoRepository formatoRepository;
    private final UsuarioService usuarioService;

    public LecturaService(LecturaRepository lecturaRepository,
                          SesionLecturaRepository sesionRepository,
                          EdicionRepository edicionRepository,
                          FormatoRepository formatoRepository,
                          UsuarioService usuarioService) {
        this.lecturaRepository = lecturaRepository;
        this.sesionRepository = sesionRepository;
        this.edicionRepository = edicionRepository;
        this.formatoRepository = formatoRepository;
        this.usuarioService = usuarioService;
    }

    public List<Lectura> obtenerLecturas(Integer idUsuario, String estado) {
        usuarioService.validarUsuarioExiste(idUsuario);
        String estadoFiltro = estado == null || estado.isBlank() ? null : validarEstado(estado);
        return lecturaRepository.findByUsuarioId(idUsuario, estadoFiltro);
    }

    /** Detalle de una lectura del usuario, con sus sesiones. */
    public Lectura obtenerLectura(Integer idUsuario, Integer idLectura) {
        Lectura lectura = obtenerLecturaDeUsuario(idUsuario, idLectura);
        lectura.setSesiones(sesionRepository.findByLecturaId(idLectura));
        return lectura;
    }

    /**
     * Inicia una lectura. Obligatorios: idEdicion e idFormato.
     * Opcionales: fechaInicio (por defecto hoy) y estado ("por iniciar" o "en curso", por defecto "en curso").
     */
    public Lectura iniciarLectura(Integer idUsuario, Lectura datos) {
        usuarioService.validarUsuarioExiste(idUsuario);

        if (datos.getIdEdicion() == null) {
            throw new IllegalArgumentException("idEdicion es obligatorio");
        }
        if (edicionRepository.findById(datos.getIdEdicion()).isEmpty()) {
            throw new IllegalArgumentException("La edición " + datos.getIdEdicion() + " no existe");
        }
        validarFormato(datos.getIdFormato());

        String estado = datos.getEstado() == null ? EN_CURSO : validarEstado(datos.getEstado());
        if (!estado.equals(POR_INICIAR) && !estado.equals(EN_CURSO)) {
            throw new IllegalArgumentException("Una lectura nueva solo puede iniciar como 'por iniciar' o 'en curso'");
        }
        if (lecturaRepository.existeLecturaActiva(idUsuario, datos.getIdEdicion())) {
            throw new ConflictoException("El usuario ya tiene una lectura activa de esa edición");
        }

        Lectura nueva = new Lectura();
        nueva.setIdUsuario(idUsuario);
        nueva.setIdEdicion(datos.getIdEdicion());
        nueva.setIdFormato(datos.getIdFormato());
        nueva.setFechaInicio(datos.getFechaInicio() == null ? LocalDate.now() : datos.getFechaInicio());
        nueva.setEstado(estado);
        lecturaRepository.insert(nueva);

        return obtenerLecturaDeUsuario(idUsuario, nueva.getIdLectura());
    }

    /**
     * Actualiza los campos enviados: estado, idFormato, fechaInicio y/o fechaFin.
     * - Si queda "terminada" o "abandonada" sin fechaFin, se usa la fecha de hoy.
     * - Si vuelve a "por iniciar" o "en curso", se borra la fechaFin.
     */
    public Lectura actualizarLectura(Integer idUsuario, Integer idLectura, Lectura cambios) {
        Lectura lectura = obtenerLecturaDeUsuario(idUsuario, idLectura);

        if (cambios.getEstado() != null) {
            lectura.setEstado(validarEstado(cambios.getEstado()));
        }
        if (cambios.getIdFormato() != null) {
            validarFormato(cambios.getIdFormato());
            lectura.setIdFormato(cambios.getIdFormato());
        }
        if (cambios.getFechaInicio() != null) {
            lectura.setFechaInicio(cambios.getFechaInicio());
        }
        if (cambios.getFechaFin() != null) {
            lectura.setFechaFin(cambios.getFechaFin());
        }

        boolean finalizada = lectura.getEstado().equals(TERMINADA) || lectura.getEstado().equals(ABANDONADA);
        if (finalizada && lectura.getFechaFin() == null) {
            lectura.setFechaFin(LocalDate.now());
        }
        if (!finalizada) {
            lectura.setFechaFin(null);
        }
        if (lectura.getFechaFin() != null && lectura.getFechaFin().isBefore(lectura.getFechaInicio())) {
            throw new IllegalArgumentException("La fecha de fin no puede ser anterior a la fecha de inicio");
        }

        lecturaRepository.update(lectura);
        return obtenerLecturaDeUsuario(idUsuario, idLectura);
    }

    public void eliminarLectura(Integer idUsuario, Integer idLectura) {
        obtenerLecturaDeUsuario(idUsuario, idLectura);
        lecturaRepository.deleteById(idLectura);
    }

    /** Busca la lectura y valida que pertenezca al usuario de la ruta. */
    public Lectura obtenerLecturaDeUsuario(Integer idUsuario, Integer idLectura) {
        usuarioService.validarUsuarioExiste(idUsuario);
        return lecturaRepository.findById(idLectura)
                .filter(lectura -> lectura.getIdUsuario().equals(idUsuario))
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Lectura " + idLectura + " no encontrada para el usuario " + idUsuario));
    }

    private void validarFormato(Integer idFormato) {
        if (idFormato == null) {
            throw new IllegalArgumentException("idFormato es obligatorio");
        }
        if (formatoRepository.findById(idFormato).isEmpty()) {
            throw new IllegalArgumentException("El formato " + idFormato + " no existe");
        }
    }

    private static String validarEstado(String estado) {
        String limpio = estado.trim().toLowerCase();
        if (!ESTADOS.contains(limpio)) {
            throw new IllegalArgumentException(
                    "Estado inválido. Valores permitidos: por iniciar, en curso, terminada, abandonada");
        }
        return limpio;
    }
}
