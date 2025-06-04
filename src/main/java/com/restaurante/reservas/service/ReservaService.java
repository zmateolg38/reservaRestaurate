package com.restaurante.reservas.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.restaurante.reservas.entity.Mesa;
import com.restaurante.reservas.entity.Reserva;
import com.restaurante.reservas.entity.Usuario;

public interface ReservaService {
    
    Reserva crearReserva(Reserva reserva);
    List<Reserva> obtenerReservasPorCliente(Usuario cliente);
    List<Reserva> obtenerTodasLasReservas();
    List<Reserva> obtenerReservasDelDia(LocalDateTime fecha);
    Reserva modificarReserva(Long id, Reserva reservaModificada);
    void cancelarReserva(Long id);
    void confirmarReserva(Long id);
    void enviarRecordatorios();
    List<Object[]> obtenerEstadisticasHorarios(LocalDateTime inicio, LocalDateTime fin);
    Optional<Reserva> obtenerReservaPorId(Long id);
    boolean estaDisponible(Mesa mesa, LocalDateTime inicio, LocalDateTime fin);

}
