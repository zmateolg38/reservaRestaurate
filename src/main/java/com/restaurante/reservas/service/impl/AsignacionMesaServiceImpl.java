package com.restaurante.reservas.service.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.restaurante.reservas.entity.AsignacionMesa;
import com.restaurante.reservas.entity.Usuario;
import com.restaurante.reservas.repository.AsignacionMesaRepository;
import com.restaurante.reservas.service.AsignacionMesaService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class AsignacionMesaServiceImpl implements AsignacionMesaService {

    private AsignacionMesaRepository asignacionMesaRepository;
    
    public AsignacionMesa asignarMesaAMesero(AsignacionMesa asignacion) {
        asignacion.setEstado(AsignacionMesa.EstadoAsignacion.ACTIVA);
        return asignacionMesaRepository.save(asignacion);
    }
    
    public List<AsignacionMesa> obtenerAsignacionesPorMesero(Usuario mesero) {
        return asignacionMesaRepository.findByMesero(mesero);
    }
    
    public List<AsignacionMesa> obtenerAsignacionesPorFecha(LocalDate fecha) {
        return asignacionMesaRepository.findAsignacionesActivasPorFecha(fecha);
    }
    
    public List<AsignacionMesa> obtenerAsignacionesMeseroPorFecha(Long meseroId, LocalDate fecha) {
        return asignacionMesaRepository.findByMeseroIdAndFecha(meseroId, fecha);
    }
    
    public void completarAsignacion(Long asignacionId) {
        AsignacionMesa asignacion = asignacionMesaRepository.findById(asignacionId)
            .orElseThrow(() -> new RuntimeException("Asignación no encontrada"));
        
        asignacion.setEstado(AsignacionMesa.EstadoAsignacion.COMPLETADA);
        asignacionMesaRepository.save(asignacion);
    }
    
    public Optional<AsignacionMesa> obtenerAsignacionPorId(Long id) {
        return asignacionMesaRepository.findById(id);
    }

}
