package teccr.biblioteca.service;

import org.springframework.stereotype.Service;
import teccr.biblioteca.data.Lectura;
import teccr.biblioteca.data.Valoracion;
import teccr.biblioteca.exception.ConflictoException;
import teccr.biblioteca.exception.RecursoNoEncontradoException;
import teccr.biblioteca.repository.LecturaRepository;
import teccr.biblioteca.repository.ValoracionRepository;

import java.time.LocalDate;
import java.util.List;

/**
 * Lógica de las valoraciones (puntuación y reseña) que el usuario hace al terminar o abandonar una lectura.
 */
@Service
public class ValoracionService {

    private static final int MAX_RESENNA = 2000;

    private final ValoracionRepository valoracionRepository;
    private final LecturaRepository lecturaRepository;
    private final LibroService libroService;

    public ValoracionService(ValoracionRepository valoracionRepository,
                             LecturaRepository lecturaRepository,
                             LibroService libroService) {
        this.valoracionRepository = valoracionRepository;
        this.lecturaRepository = lecturaRepository;
        this.libroService = libroService;
    }

    public List<Valoracion> obtenerValoracionesDeLectura(Integer idLectura) {
        obtenerLectura(idLectura);
        return valoracionRepository.findByLecturaId(idLectura);
    }

    /** Valoraciones de todos los usuarios sobre un libro (cualquiera de sus ediciones). */
    public List<Valoracion> obtenerValoracionesDeLibro(Integer idLibro) {
        libroService.validarLibroExiste(idLibro);
        return valoracionRepository.findByLibroId(idLibro);
    }

    public Valoracion obtenerValoracion(Integer idValoracion) {
        return valoracionRepository.findById(idValoracion)
                .orElseThrow(() -> new RecursoNoEncontradoException("Valoración", idValoracion));
    }

    /**
     * Crea la valoración de una lectura. Obligatorios: puntuacion (1 a 5) y resenna.
     * Solo se permite si la lectura está "terminada" o "abandonada", y una sola vez por lectura.
     * La fecha es la de hoy.
     */
    public Valoracion crearValoracion(Integer idLectura, Valoracion datos) {
        Lectura lectura = obtenerLectura(idLectura);

        String estado = lectura.getEstado();
        if (!estado.equals(LecturaService.TERMINADA) && !estado.equals(LecturaService.ABANDONADA)) {
            throw new IllegalArgumentException(
                    "Solo se puede valorar una lectura terminada o abandonada (estado actual: " + estado + ")");
        }
        if (valoracionRepository.existeValoracionDeLectura(idLectura)) {
            throw new ConflictoException("La lectura " + idLectura + " ya tiene una valoración; use PATCH para modificarla");
        }

        Valoracion nueva = new Valoracion();
        nueva.setIdLectura(idLectura);
        nueva.setPuntuacion(validarPuntuacion(datos.getPuntuacion()));
        nueva.setResenna(validarResenna(datos.getResenna()));
        nueva.setFecha(LocalDate.now());
        valoracionRepository.insert(nueva);

        return obtenerValoracion(nueva.getIdValoracion());
    }

    /** Cambia la puntuación y/o la reseña enviadas. La fecha pasa a ser la de hoy. */
    public Valoracion actualizarValoracion(Integer idValoracion, Valoracion cambios) {
        Valoracion valoracion = obtenerValoracion(idValoracion);

        if (cambios.getPuntuacion() == null && cambios.getResenna() == null) {
            throw new IllegalArgumentException("Envíe puntuacion y/o resenna para actualizar");
        }
        if (cambios.getPuntuacion() != null) {
            valoracion.setPuntuacion(validarPuntuacion(cambios.getPuntuacion()));
        }
        if (cambios.getResenna() != null) {
            valoracion.setResenna(validarResenna(cambios.getResenna()));
        }
        valoracion.setFecha(LocalDate.now());
        valoracionRepository.update(valoracion);

        return obtenerValoracion(idValoracion);
    }

    public void eliminarValoracion(Integer idValoracion) {
        obtenerValoracion(idValoracion);
        valoracionRepository.deleteById(idValoracion);
    }

    private Lectura obtenerLectura(Integer idLectura) {
        return lecturaRepository.findById(idLectura)
                .orElseThrow(() -> new RecursoNoEncontradoException("Lectura", idLectura));
    }

    private static int validarPuntuacion(Integer puntuacion) {
        if (puntuacion == null || puntuacion < 1 || puntuacion > 5) {
            throw new IllegalArgumentException("La puntuación es obligatoria y debe estar entre 1 y 5");
        }
        return puntuacion;
    }

    private static String validarResenna(String resenna) {
        if (resenna == null || resenna.isBlank()) {
            throw new IllegalArgumentException("La reseña es obligatoria");
        }
        String limpia = resenna.trim();
        if (limpia.length() > MAX_RESENNA) {
            throw new IllegalArgumentException("La reseña no puede superar " + MAX_RESENNA + " caracteres");
        }
        return limpia;
    }
}
