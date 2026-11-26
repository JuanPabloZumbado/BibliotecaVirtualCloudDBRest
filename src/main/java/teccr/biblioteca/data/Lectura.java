package teccr.biblioteca.data;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class Lectura {

    // Columnas de la tabla Lectura
    private Integer idLectura;
    private Integer idUsuario;
    private Integer idEdicion;
    private Integer idFormato;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;       // null mientras la lectura no termine
    private String estado;            // por iniciar | en curso | terminada | abandonada

    // Datos de solo lectura que se obtienen con JOIN (Edicion, Libro, Formato, vw_sesion_paginas)
    private Integer idLibro;
    private String titulo;
    private String tipoEdicion;
    private String editorial;
    private Integer totPaginas;
    private String tipoFormato;
    private Integer paginaActual;     // mayor pag_fin registrado en las sesiones
    private Integer paginasLeidas;    // suma de (pag_fin - pag_inicio) de las sesiones
    private Integer minutosLeidos;    // suma de la duración de las sesiones

    // Solo se llena en el detalle (GET /usuarios/{uid}/lecturas/{id})
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<SesionLectura> sesiones;
}
