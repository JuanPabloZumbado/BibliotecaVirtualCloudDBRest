package teccr.biblioteca.service;

import org.springframework.stereotype.Service;
import teccr.biblioteca.data.EstadisticasUsuario;
import teccr.biblioteca.data.Usuario;
import teccr.biblioteca.repository.EstadisticasRepository;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Arma el resumen de estadísticas de un usuario a partir de varias consultas de agregación.
 */
@Service
public class EstadisticasService {

    private static final int TOP = 3;
    private static final List<String> ESTADOS = List.of(
            LecturaService.POR_INICIAR, LecturaService.EN_CURSO, LecturaService.TERMINADA, LecturaService.ABANDONADA);

    private final EstadisticasRepository estadisticasRepository;
    private final UsuarioService usuarioService;

    public EstadisticasService(EstadisticasRepository estadisticasRepository, UsuarioService usuarioService) {
        this.estadisticasRepository = estadisticasRepository;
        this.usuarioService = usuarioService;
    }

    public EstadisticasUsuario obtenerEstadisticas(Integer idUsuario) {
        Usuario usuario = usuarioService.obtenerUsuario(idUsuario);

        EstadisticasUsuario estadisticas = new EstadisticasUsuario();
        estadisticas.setIdUsuario(usuario.getIdUsuario());
        estadisticas.setNombre(usuario.getNombre());

        // Siempre se devuelven los 4 estados, aunque tengan 0 lecturas
        Map<String, Integer> encontrados = estadisticasRepository.contarLecturasPorEstado(idUsuario);
        Map<String, Integer> porEstado = new LinkedHashMap<>();
        ESTADOS.forEach(estado -> porEstado.put(estado, encontrados.getOrDefault(estado, 0)));
        estadisticas.setLecturasPorEstado(porEstado);
        estadisticas.setTotalLecturas(porEstado.values().stream().mapToInt(Integer::intValue).sum());
        estadisticas.setLibrosTerminados(estadisticasRepository.contarLibrosTerminados(idUsuario));

        int[] sesiones = estadisticasRepository.resumirSesiones(idUsuario);
        estadisticas.setTotalSesiones(sesiones[0]);
        estadisticas.setPaginasLeidas(sesiones[1]);
        estadisticas.setMinutosLeidos(sesiones[2]);
        estadisticas.setPaginasPorHora(sesiones[2] == 0 ? null
                : Math.round(sesiones[1] * 60.0 / sesiones[2] * 10) / 10.0);

        estadisticas.setTotalValoraciones(estadisticasRepository.contarValoraciones(idUsuario));
        estadisticas.setPromedioPuntuacion(estadisticasRepository.promedioPuntuacion(idUsuario));
        estadisticas.setLibrosPorLeer(estadisticasRepository.contarLibrosPorLeer(idUsuario));

        estadisticas.setGenerosMasLeidos(estadisticasRepository.generosMasLeidos(idUsuario, TOP));
        estadisticas.setAutoresMasLeidos(estadisticasRepository.autoresMasLeidos(idUsuario, TOP));
        estadisticas.setFormatosUsados(estadisticasRepository.formatosUsados(idUsuario));
        return estadisticas;
    }
}
