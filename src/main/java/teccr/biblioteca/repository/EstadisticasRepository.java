package teccr.biblioteca.repository;

import teccr.biblioteca.data.Conteo;

import java.util.List;
import java.util.Map;

/** Consultas de agregación (COUNT, SUM, AVG, GROUP BY) sobre la actividad de un usuario. */
public interface EstadisticasRepository {

    Map<String, Integer> contarLecturasPorEstado(Integer idUsuario);

    int contarLibrosTerminados(Integer idUsuario);

    /** Devuelve [totalSesiones, paginasLeidas, minutosLeidos]. */
    int[] resumirSesiones(Integer idUsuario);

    int contarValoraciones(Integer idUsuario);

    Double promedioPuntuacion(Integer idUsuario);

    int contarLibrosPorLeer(Integer idUsuario);

    List<Conteo> generosMasLeidos(Integer idUsuario, int limite);

    List<Conteo> autoresMasLeidos(Integer idUsuario, int limite);

    List<Conteo> formatosUsados(Integer idUsuario);
}
