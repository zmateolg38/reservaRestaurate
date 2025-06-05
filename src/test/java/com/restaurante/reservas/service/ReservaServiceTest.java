package com.restaurante.reservas.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.restaurante.reservas.entity.Mesa;
import com.restaurante.reservas.entity.Reserva;
import com.restaurante.reservas.repository.ReservaRepository;
import com.restaurante.reservas.service.impl.ReservaServiceImpl;

@ExtendWith(MockitoExtension.class)
public class ReservaServiceTest {

    @InjectMocks
    private ReservaServiceImpl reservaService;

    @Mock
    private ReservaRepository reservaRepository;

    @Mock
    private MesaService mesaService;

    @Mock
    private EmailService emailService;

    @Test
    void testCrearReserva_ExcedeCapacidad() {
        Mesa mesa = new Mesa();
        mesa.setCapacidad(2);

        Reserva reserva = new Reserva();
        reserva.setMesa(mesa);
        reserva.setNumeroPersonas(5); // excede capacidad

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            reservaService.crearReserva(reserva);
        });

        assertEquals("La cantidad de personas excede la capacidad de la mesa", exception.getMessage());
    }

    @Test
    void testCrearReserva_MesaNoDisponible() {
        Mesa mesa = new Mesa();
        mesa.setCapacidad(4);

        Reserva reserva = new Reserva();
        reserva.setMesa(mesa);
        reserva.setNumeroPersonas(2);
        reserva.setFechaInicioReserva(LocalDateTime.now());

        when(reservaRepository.findReservasSolapadas(any(), any(), any())).thenReturn(List.of(new Reserva()));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            reservaService.crearReserva(reserva);
        });

        assertEquals("La mesa no está disponible en el horario solicitado", exception.getMessage());
    }

    @Test
    void testModificarReserva_Success() {
        Reserva reservaExistente = new Reserva();
        reservaExistente.setId(1L);
        reservaExistente.setMesa(new Mesa());
        reservaExistente.setEstado(Reserva.EstadoReserva.PENDIENTE);
        reservaExistente.getMesa().setCapacidad(4);

        Reserva reservaModificada = new Reserva();
        reservaModificada.setFechaInicioReserva(LocalDateTime.now().plusHours(1));
        reservaModificada.setFechaFinReserva(LocalDateTime.now().plusMinutes(30));
        reservaModificada.setNumeroPersonas(2);
        reservaModificada.setComentarios("Comentario nuevo");

        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reservaExistente));
        when(reservaRepository.save(any(Reserva.class))).thenReturn(reservaExistente);

        Reserva resultado = reservaService.modificarReserva(1L, reservaModificada);

        assertEquals(reservaModificada.getComentarios(), resultado.getComentarios());
        assertEquals(2, resultado.getNumeroPersonas());
        assertEquals(reservaModificada.getFechaInicioReserva(), resultado.getFechaInicioReserva());
    }

    @Test
    void testCancelarReserva() {
        Reserva reserva = new Reserva();
        Mesa mesa = new Mesa();
        mesa.setId(1L);
        reserva.setMesa(mesa);

        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reserva));

        reservaService.cancelarReserva(1L);

        assertEquals(Reserva.EstadoReserva.CANCELADA, reserva.getEstado());
        verify(mesaService).liberarMesa(1L);
        verify(reservaRepository).save(reserva);
    }

    @Test
    void testEnviarRecordatorios() {
        Reserva reserva = new Reserva();
        reserva.setFechaInicioReserva(LocalDateTime.now().plusHours(6));
        reserva.setRecordatorioEnviado(false);

        when(reservaRepository.findReservasParaRecordatorio(any(), any())).thenReturn(List.of(reserva));

        reservaService.enviarRecordatorios();

        assertTrue(reserva.getRecordatorioEnviado());
        verify(emailService).enviarRecordatorioReserva(reserva);
        verify(reservaRepository).save(reserva);
    }

    @Test
    void testObtenerReservaPorId_Existe() {
        Reserva reserva = new Reserva();
        reserva.setId(1L);

        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reserva));

        Reserva resultado = reservaService.obtenerReservaPorId(1L).orElse(null);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
    }

    @Test
    void testObtenerReservaPorId_NoExiste() {
        when(reservaRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<Reserva> resultado = reservaService.obtenerReservaPorId(1L);

        assertTrue(resultado.isEmpty());
    }

    @Test
    void testObtenerTodasLasReservas() {
        Reserva reserva1 = new Reserva();
        reserva1.setId(1L);

        Reserva reserva2 = new Reserva();
        reserva2.setId(2L);

        List<Reserva> lista = List.of(reserva1, reserva2);

        when(reservaRepository.findAll()).thenReturn(lista);

        List<Reserva> resultado = reservaService.obtenerTodasLasReservas();

        assertEquals(2, resultado.size());
        assertTrue(resultado.contains(reserva1));
        assertTrue(resultado.contains(reserva2));
    }

}
