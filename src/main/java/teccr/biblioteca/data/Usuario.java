package teccr.biblioteca.data;

import lombok.Data;

import java.time.LocalDate;

@Data
public class Usuario {
    private Integer idUsuario;
    private String nombre;
    private String correo;
    private LocalDate fechaRegistro;
}
