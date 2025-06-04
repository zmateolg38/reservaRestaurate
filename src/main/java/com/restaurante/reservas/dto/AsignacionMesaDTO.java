package com.restaurante.reservas.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import com.restaurante.reservas.entity.AsignacionMesa.EstadoAsignacion;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AsignacionMesaDTO {
    private Long id;

    private Long meseroId;
    private String meseroNombre;

    private Long mesaId;
    private String numeroMesa;

    private LocalDate fecha;
    private LocalTime horaInicio;
    private LocalTime horaFin;

    private EstadoAsignacion estado;
}
