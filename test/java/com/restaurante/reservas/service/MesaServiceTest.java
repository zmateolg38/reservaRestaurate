package com.restaurante.reservas.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
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
import com.restaurante.reservas.repository.MesaRepository;
import com.restaurante.reservas.service.impl.MesaServiceImpl;

@ExtendWith(MockitoExtension.class)
public class MesaServiceTest {
    
    @Mock
    private MesaRepository mesaRepository;

    @InjectMocks
    private MesaServiceImpl mesaService;

    @Test
    void obtenerTodasLasMesas_deberiaRetornarMesasActivas() {
        List<Mesa> mesas = List.of(new Mesa(), new Mesa());
        when(mesaRepository.findByActivaTrue()).thenReturn(mesas);

        List<Mesa> resultado = mesaService.obtenerTodasLasMesas();

        assertEquals(2, resultado.size());
        verify(mesaRepository).findByActivaTrue();
    }

    @Test
    void obtenerMesasDisponibles_deberiaRetornarMesasDisponibles() {
        List<Mesa> mesas = List.of(new Mesa());
        when(mesaRepository.findByEstado(Mesa.EstadoMesa.DISPONIBLE)).thenReturn(mesas);

        List<Mesa> resultado = mesaService.obtenerMesasDisponibles();

        assertEquals(1, resultado.size());
        verify(mesaRepository).findByEstado(Mesa.EstadoMesa.DISPONIBLE);
    }

    @Test
    void verificarDisponibilidad_deberiaRetornarMesas() {
        LocalDateTime fecha = LocalDateTime.now();
        int personas = 4;
        List<Mesa> mesas = List.of(new Mesa());
        when(mesaRepository.findMesasDisponiblesPorFechaYCapacidad(fecha, personas)).thenReturn(mesas);

        List<Mesa> resultado = mesaService.verificarDisponibilidad(fecha, personas);

        assertEquals(1, resultado.size());
        verify(mesaRepository).findMesasDisponiblesPorFechaYCapacidad(fecha, personas);
    }

    @Test
    void crearMesa_deberiaGuardarConEstadoDisponibleYActivaTrue() {
        Mesa nuevaMesa = new Mesa();
        Mesa mesaGuardada = new Mesa();
        mesaGuardada.setEstado(Mesa.EstadoMesa.DISPONIBLE);
        mesaGuardada.setActiva(true);

        when(mesaRepository.save(any(Mesa.class))).thenReturn(mesaGuardada);

        Mesa resultado = mesaService.crearMesa(nuevaMesa);

        assertEquals(Mesa.EstadoMesa.DISPONIBLE, resultado.getEstado());
        assertTrue(resultado.getActiva());
        verify(mesaRepository).save(any(Mesa.class));
    }

    @Test
    void actualizarEstadoMesa_deberiaActualizarEstadoSiExiste() {
        Mesa mesa = new Mesa();
        mesa.setId(1L);
        mesa.setEstado(Mesa.EstadoMesa.DISPONIBLE);

        when(mesaRepository.findById(1L)).thenReturn(Optional.of(mesa));
        when(mesaRepository.save(any(Mesa.class))).thenReturn(mesa);

        Mesa resultado = mesaService.actualizarEstadoMesa(1L, Mesa.EstadoMesa.OCUPADA);

        assertEquals(Mesa.EstadoMesa.OCUPADA, resultado.getEstado());
        verify(mesaRepository).findById(1L);
        verify(mesaRepository).save(mesa);
    }

    @Test
    void liberarMesa_deberiaCambiarEstadoADisponible() {
        Mesa mesa = new Mesa();
        mesa.setId(1L);
        mesa.setEstado(Mesa.EstadoMesa.OCUPADA);

        when(mesaRepository.findById(1L)).thenReturn(Optional.of(mesa));

        mesaService.liberarMesa(1L);

        assertEquals(Mesa.EstadoMesa.DISPONIBLE, mesa.getEstado());
        verify(mesaRepository).save(mesa);
    }

    @Test
    void obtenerMesaPorId_deberiaRetornarMesaSiExiste() {
        Mesa mesa = new Mesa();
        mesa.setId(1L);

        when(mesaRepository.findById(1L)).thenReturn(Optional.of(mesa));

        Optional<Mesa> resultado = mesaService.obtenerMesaPorId(1L);

        assertTrue(resultado.isPresent());
        assertEquals(1L, resultado.get().getId());
    }

    @Test
    void contarMesasActivas_deberiaRetornarCantidad() {
        when(mesaRepository.countMesasActivas()).thenReturn(5L);

        Long resultado = mesaService.contarMesasActivas();

        assertEquals(5L, resultado);
    }

    @Test
    void estaDisponible_deberiaRetornarTrueSiMesaEstaEnListaDisponible() {
        Mesa mesa = new Mesa();
        mesa.setId(10L);
        List<Mesa> disponibles = List.of(mesa);

        when(mesaRepository.findMesasDisponiblesPorFechaYCapacidad(any(), anyInt())).thenReturn(disponibles);

        boolean resultado = mesaService.estaDisponible(mesa, LocalDateTime.now(), 4);

        assertTrue(resultado);
    }

    @Test
    void estaDisponible_deberiaRetornarFalseSiMesaNoEstaEnListaDisponible() {
        Mesa mesa = new Mesa();
        mesa.setId(10L);
        Mesa otra = new Mesa();
        otra.setId(20L);

        when(mesaRepository.findMesasDisponiblesPorFechaYCapacidad(any(), anyInt())).thenReturn(List.of(otra));

        boolean resultado = mesaService.estaDisponible(mesa, LocalDateTime.now(), 4);

        assertFalse(resultado);
    }

}
