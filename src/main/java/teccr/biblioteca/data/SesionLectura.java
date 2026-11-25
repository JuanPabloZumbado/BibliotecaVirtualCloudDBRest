package teccr.biblioteca.data;

import lombok.Data;

@Data
public class SesionLectura {
    private Integer idSesion;
    private Integer idLectura;
    private Integer pagInicio;
    private Integer pagFin;
    private Integer pagLeidas;   // calculado: pag_fin - pag_inicio (vista vw_sesion_paginas)
    private Integer duracion;    // minutos
    private Integer capActual;
}
