package com.restaurante.reservas.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.restaurante.reservas.entity.Mesa;

public interface MesaService {


    List<Mesa> obtenerTodasLasMesas();
    List<Mesa> obtenerMesasDisponibles();
    List<Mesa> verificarDisponibilidad(LocalDateTime fechaReserva, Integer numeroPersonas);
    boolean estaDisponible(Mesa mesa, LocalDateTime fechaReserva, Integer numeroPersonas);
    Mesa crearMesa(Mesa mesa);
    Mesa actualizarEstadoMesa(Long id, Mesa.EstadoMesa nuevoEstado);
    void liberarMesa(Long mesaId);
    Optional<Mesa> obtenerMesaPorId(Long id);
    Long contarMesasActivas();
    
}
