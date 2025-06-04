package com.restaurante.reservas.service;

import com.restaurante.reservas.entity.Reserva;

public interface EmailService {

    void enviarRecordatorioReserva(Reserva reserva);
    void enviarConfirmacionReserva(Reserva reserva);
    
}
