package com.restaurante.reservas.service.impl;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.restaurante.reservas.entity.Reserva;
import com.restaurante.reservas.service.EmailService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class EmailServiceImpl implements EmailService {

    private JavaMailSender mailSender;

    /**
     * Envía un recordatorio de reserva al cliente.
     * 
     * @param reserva Objeto Reserva con los detalles de la reserva.
     */
    @Override
    public void enviarRecordatorioReserva(Reserva reserva) {
        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setTo(reserva.getCliente().getEmail());
        mensaje.setSubject("Recordatorio de Reserva - Restaurante");
        mensaje.setText(String.format(
                "Estimado/a %s,\n\n" +
                        "Le recordamos que tiene una reserva para %d personas " +
                        "el %s en la mesa %s.\n\n" +
                        "¡Esperamos verle pronto!\n\n" +
                        "Saludos,\n" +
                        "Equipo del Restaurante",
                reserva.getCliente().getNombre(),
                reserva.getNumeroPersonas(),
                reserva.getFechaInicioReserva().toString(),
                reserva.getMesa().getNumero()));

        mailSender.send(mensaje);
    }

    /**
     * Envía una confirmación de reserva al cliente.
     * 
     * @param reserva Objeto Reserva con los detalles de la reserva.
     */
    @Override
    public void enviarConfirmacionReserva(Reserva reserva) {
        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setTo(reserva.getCliente().getEmail());
        mensaje.setSubject("Confirmación de Reserva - Restaurante");
        mensaje.setText(String.format(
                "Estimado/a %s,\n\n" +
                        "Su reserva ha sido confirmada:\n" +
                        "Fecha: %s\n" +
                        "Mesa: %s\n" +
                        "Personas: %d\n\n" +
                        "¡Esperamos verle pronto!\n\n" +
                        "Saludos,\n" +
                        "Equipo del Restaurante",
                reserva.getCliente().getNombre(),
                reserva.getFechaInicioReserva().toString(),
                reserva.getMesa().getNumero(),
                reserva.getNumeroPersonas()));

        mailSender.send(mensaje);
    }

}