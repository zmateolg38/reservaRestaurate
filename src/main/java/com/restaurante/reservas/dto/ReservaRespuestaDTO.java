package com.restaurante.reservas.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReservaRespuestaDTO {
    private String clienteNombre;
    private String clienteEmail;
    private Long mesaId;
    private LocalDateTime fechaInicioReserva;
    private LocalDateTime fechaFinReserva;
    private Integer numeroPersonas;
    private String estado;
    private String comentarios;
}
