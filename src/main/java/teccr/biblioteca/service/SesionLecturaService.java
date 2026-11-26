package teccr.biblioteca.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import teccr.biblioteca.data.Lectura;
import teccr.biblioteca.data.SesionLectura;
import teccr.biblioteca.exception.RecursoNoEncontradoException;
import teccr.biblioteca.repository.LecturaRepository;
import teccr.biblioteca.repository.SesionLecturaRepository;

import java.util.List;

/**
 * Lógica de las sesiones de lectura (cada vez que el usuario se sienta a leer).
 */
@Service
public class SesionLecturaService {

    private final SesionLecturaRepository sesionRepository;
    private final LecturaRepository lecturaRepository;

    public SesionLecturaService(SesionLecturaRepository sesionRepository, LecturaRepository lecturaRepository) {
        this.sesionRepository = sesionRepository;
        this.lecturaRepository = lecturaRepository;
    }

    public List<SesionLectura> obtenerSesiones(Integer idLectura) {
        obtenerLectura(idLectura);
        return sesionRepository.findByLecturaId(idLectura);
    }

    /**
     * Registra una sesión. Obligatorios: pagFin, duracion y capActual.
     * pagInicio es opcional: por defecto es la última página registrada (0 si es la primera sesión).
     * Si la lectura estaba "por iniciar", pasa a "en curso".
     */
    @Transactional
    public SesionLectura registrarSesion(Integer idLectura, SesionLectura datos) {
        Lectura lectura = obtenerLectura(idLectura);

        String estado = lectura.getEstado();
        if (estado.equals(LecturaService.TERMINADA) || estado.equals(LecturaService.ABANDONADA)) {
            throw new IllegalArgumentException("No se pueden registrar sesiones en una lectura " + estado);
        }

        int pagInicio = datos.getPagInicio() != null
                ? datos.getPagInicio()
                : sesionRepository.findUltimaPagina(idLectura);

        if (datos.getPagFin() == null || datos.getDuracion() == null || datos.getCapActual() == null) {
            throw new IllegalArgumentException("pagFin, duracion y capActual son obligatorios");
        }
        if (pagInicio < 0 || datos.getPagFin() < pagInicio) {
            throw new IllegalArgumentException("pagFin debe ser mayor o igual que pagInicio (" + pagInicio + ")");
        }
        if (datos.getPagFin() > lectura.getTotPaginas()) {
            throw new IllegalArgumentException(
                    "pagFin no puede superar el total de páginas de la edición (" + lectura.getTotPaginas() + ")");
        }
        if (datos.getDuracion() <= 0) {
            throw new IllegalArgumentException("La duración debe ser mayor que 0 minutos");
        }
        if (datos.getCapActual() < 0) {
            throw new IllegalArgumentException("capActual no puede ser negativo");
        }

        SesionLectura sesion = new SesionLectura();
        sesion.setIdLectura(idLectura);
        sesion.setPagInicio(pagInicio);
        sesion.setPagFin(datos.getPagFin());
        sesion.setDuracion(datos.getDuracion());
        sesion.setCapActual(datos.getCapActual());
        sesionRepository.insert(sesion);

        if (estado.equals(LecturaService.POR_INICIAR)) {
            lectura.setEstado(LecturaService.EN_CURSO);
            lecturaRepository.update(lectura);
        }
        return sesion;
    }

    public void eliminarSesion(Integer idLectura, Integer idSesion) {
        obtenerLectura(idLectura);
        sesionRepository.findById(idSesion)
                .filter(sesion -> sesion.getIdLectura().equals(idLectura))
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Sesión " + idSesion + " no encontrada en la lectura " + idLectura));
        sesionRepository.deleteById(idSesion);
    }

    private Lectura obtenerLectura(Integer idLectura) {
        return lecturaRepository.findById(idLectura)
                .orElseThrow(() -> new RecursoNoEncontradoException("Lectura", idLectura));
    }
}
