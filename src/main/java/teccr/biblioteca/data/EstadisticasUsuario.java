package teccr.biblioteca.data;

import lombok.Data;

import java.util.List;
import java.util.Map;

/** Resumen de la actividad de lectura de un usuario (GET /usuarios/{id}/estadisticas). */
@Data
public class EstadisticasUsuario {

    private Integer idUsuario;
    private String nombre;

    // Lecturas
    private Integer totalLecturas;
    private Map<String, Integer> lecturasPorEstado;   // los 4 estados, con 0 si no hay
    private Integer librosTerminados;                 // libros distintos con una lectura terminada

    // Sesiones (vista vw_sesion_paginas)
    private Integer totalSesiones;
    private Integer paginasLeidas;
    private Integer minutosLeidos;
    private Double paginasPorHora;                    // null si no hay minutos registrados

    // Valoraciones y lista por leer
    private Integer totalValoraciones;
    private Double promedioPuntuacion;                // null si no ha valorado nada
    private Integer librosPorLeer;

    // Rankings (los 3 primeros)
    private List<Conteo> generosMasLeidos;
    private List<Conteo> autoresMasLeidos;
    private List<Conteo> formatosUsados;
}
