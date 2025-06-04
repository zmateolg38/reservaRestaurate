package com.restaurante.reservas.service.impl;

import java.time.LocalDateTime;

import com.restaurante.reservas.entity.Mesa;
import com.restaurante.reservas.repository.MesaRepository;
import com.restaurante.reservas.service.MesaService;

import lombok.AllArgsConstructor;

import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class MesaServiceImpl implements MesaService {

    private MesaRepository mesaRepository;

    /**
     * Obtiene todas las mesas activas.
     * 
     * @return Lista de mesas activas.
     */
    @Override
    public List<Mesa> obtenerTodasLasMesas() {
        return mesaRepository.findByActivaTrue();
    }

    /**
     * Obtiene las mesas disponibles.
     * 
     * @return Lista de mesas disponibles.
     */
    @Override
    public List<Mesa> obtenerMesasDisponibles() {
        return mesaRepository.findByEstado(Mesa.EstadoMesa.DISPONIBLE);
    }

    /**
     * Verifica la disponibilidad de mesas para una fecha y número de personas.
     * 
     * @param fechaReserva   Fecha de la reserva.
     * @param numeroPersonas Número de personas.
     * @return Lista de mesas disponibles para la fecha y capacidad especificadas.
     */
    @Override
    public List<Mesa> verificarDisponibilidad(LocalDateTime fechaReserva, Integer numeroPersonas) {
        return mesaRepository.findMesasDisponiblesPorFechaYCapacidad(fechaReserva, numeroPersonas);
    }

    /**
     * Crea una nueva mesa.
     * 
     * @param mesa Objeto Mesa a crear.
     * @return Mesa creada.
     */
    @Override
    public Mesa crearMesa(Mesa mesa) {
        mesa.setEstado(Mesa.EstadoMesa.DISPONIBLE);
        mesa.setActiva(true);
        return mesaRepository.save(mesa);
    }

    /**
     * Actualiza el estado de una mesa.
     * 
     * @param id          ID de la mesa a actualizar.
     * @param nuevoEstado Nuevo estado de la mesa.
     * @return Mesa actualizada.
     */
    @Override
    public Mesa actualizarEstadoMesa(Long id, Mesa.EstadoMesa nuevoEstado) {
        Mesa mesa = mesaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mesa no encontrada"));

        mesa.setEstado(nuevoEstado);
        return mesaRepository.save(mesa);
    }

    /**
     * Libera una mesa, cambiando su estado a DISPONIBLE.
     * 
     * @param mesaId ID de la mesa a liberar.
     */
    @Override
    public void liberarMesa(Long mesaId) {
        Mesa mesa = mesaRepository.findById(mesaId)
                .orElseThrow(() -> new RuntimeException("Mesa no encontrada"));

        mesa.setEstado(Mesa.EstadoMesa.DISPONIBLE);
        mesaRepository.save(mesa);
    }

    /**
     * Obtiene una mesa por su ID.
     * 
     * @param id ID de la mesa.
     * @return Mesa encontrada, si existe.
     */
    @Override
    public Optional<Mesa> obtenerMesaPorId(Long id) {
        return mesaRepository.findById(id);
    }

    /**
     * Cuenta el número de mesas activas.
     * 
     * @return Número de mesas activas.
     */
    @Override
    public Long contarMesasActivas() {
        return mesaRepository.countMesasActivas();
    }

    @Override
    public boolean estaDisponible(Mesa mesa, LocalDateTime fechaReserva, Integer numeroPersonas) {
       
        List<Mesa> mesasDisponibles = verificarDisponibilidad(fechaReserva, numeroPersonas);
        return mesasDisponibles.stream().anyMatch(m -> m.getId().equals(mesa.getId()));
        
    }

}
