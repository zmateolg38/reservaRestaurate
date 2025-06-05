package com.restaurante.reservas.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import com.restaurante.reservas.entity.Mesa;
import com.restaurante.reservas.entity.Reserva;
import com.restaurante.reservas.entity.Usuario;
import com.restaurante.reservas.service.impl.EmailServiceImpl;

@ExtendWith(MockitoExtension.class)
public class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailServiceImpl emailService;

    private Reserva crearReservaDePrueba() {
        Usuario cliente = new Usuario();
        cliente.setNombre("Juan Pérez");
        cliente.setEmail("juan.perez@example.com");

        Mesa mesa = new Mesa();
        mesa.setNumero("A1");

        Reserva reserva = new Reserva();
        reserva.setCliente(cliente);
        reserva.setMesa(mesa);
        reserva.setFechaInicioReserva(LocalDateTime.of(2025, 6, 5, 20, 0));
        reserva.setNumeroPersonas(4);

        return reserva;
    }

    @Test
    void testEnviarRecordatorioReserva() {
        Reserva reserva = crearReservaDePrueba();

        emailService.enviarRecordatorioReserva(reserva);

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender, times(1)).send(captor.capture());

        SimpleMailMessage mensaje = captor.getValue();
        assertEquals("juan.perez@example.com", mensaje.getTo()[0]);
        assertEquals("Recordatorio de Reserva - Restaurante", mensaje.getSubject());
        assertTrue(mensaje.getText().contains("Le recordamos que tiene una reserva"));
        assertTrue(mensaje.getText().contains("Juan Pérez"));
    }

    @Test
    void testEnviarConfirmacionReserva() {
        Reserva reserva = crearReservaDePrueba();

        emailService.enviarConfirmacionReserva(reserva);

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender, times(1)).send(captor.capture());

        SimpleMailMessage mensaje = captor.getValue();
        assertEquals("juan.perez@example.com", mensaje.getTo()[0]);
        assertEquals("Confirmación de Reserva - Restaurante", mensaje.getSubject());
        assertTrue(mensaje.getText().contains("Su reserva ha sido confirmada"));
        assertTrue(mensaje.getText().contains("A1"));
        assertTrue(mensaje.getText().contains("4"));
    }


    
}
