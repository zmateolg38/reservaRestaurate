package com.restaurante.reservas.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.restaurante.reservas.entity.AsignacionMesa;
import com.restaurante.reservas.entity.Usuario;

public interface AsignacionMesaService {
    
    AsignacionMesa asignarMesaAMesero(AsignacionMesa asignacion);
    List<AsignacionMesa> obtenerAsignacionesPorMesero(Usuario mesero);
    List<AsignacionMesa> obtenerAsignacionesPorFecha(LocalDate fecha);
    List<AsignacionMesa> obtenerAsignacionesMeseroPorFecha(Long meseroId, LocalDate fecha);
    void completarAsignacion(Long asignacionId);
    Optional<AsignacionMesa> obtenerAsignacionPorId(Long id);

}
