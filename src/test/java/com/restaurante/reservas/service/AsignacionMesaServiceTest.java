package com.restaurante.reservas.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.restaurante.reservas.entity.AsignacionMesa;
import com.restaurante.reservas.entity.Usuario;
import com.restaurante.reservas.repository.AsignacionMesaRepository;
import com.restaurante.reservas.service.impl.AsignacionMesaServiceImpl;

@ExtendWith(MockitoExtension.class)
public class AsignacionMesaServiceTest {

    @Mock
    private AsignacionMesaRepository asignacionMesaRepository;

    @InjectMocks
    private AsignacionMesaServiceImpl asignacionMesaService;

    @Test
    void asignarMesaAMesero_ShouldSetEstadoActivaAndSave() {
        AsignacionMesa asignacion = new AsignacionMesa();

        when(asignacionMesaRepository.save(asignacion)).thenReturn(asignacion);

        AsignacionMesa resultado = asignacionMesaService.asignarMesaAMesero(asignacion);

        assertEquals(AsignacionMesa.EstadoAsignacion.ACTIVA, resultado.getEstado());
        verify(asignacionMesaRepository).save(asignacion);
    }

    @Test
    void obtenerAsignacionesPorMesero_ShouldReturnAsignaciones() {
        Usuario mesero = new Usuario();
        List<AsignacionMesa> listaMock = List.of(new AsignacionMesa());
        when(asignacionMesaRepository.findByMesero(mesero)).thenReturn(listaMock);

        List<AsignacionMesa> resultado = asignacionMesaService.obtenerAsignacionesPorMesero(mesero);

        assertEquals(listaMock, resultado);
        verify(asignacionMesaRepository).findByMesero(mesero);
    }

    @Test
    void obtenerAsignacionesPorFecha_ShouldReturnAsignaciones() {
        LocalDate fecha = LocalDate.now();
        List<AsignacionMesa> listaMock = List.of(new AsignacionMesa());
        when(asignacionMesaRepository.findAsignacionesActivasPorFecha(fecha)).thenReturn(listaMock);

        List<AsignacionMesa> resultado = asignacionMesaService.obtenerAsignacionesPorFecha(fecha);

        assertEquals(listaMock, resultado);
        verify(asignacionMesaRepository).findAsignacionesActivasPorFecha(fecha);
    }

    @Test
    void obtenerAsignacionesMeseroPorFecha_ShouldReturnAsignaciones() {
        Long meseroId = 1L;
        LocalDate fecha = LocalDate.now();
        List<AsignacionMesa> listaMock = List.of(new AsignacionMesa());
        when(asignacionMesaRepository.findByMeseroIdAndFecha(meseroId, fecha)).thenReturn(listaMock);

        List<AsignacionMesa> resultado = asignacionMesaService.obtenerAsignacionesMeseroPorFecha(meseroId, fecha);

        assertEquals(listaMock, resultado);
        verify(asignacionMesaRepository).findByMeseroIdAndFecha(meseroId, fecha);
    }

    @Test
    void completarAsignacion_WhenAsignacionExists_ShouldSetEstadoCompletadaAndSave() {
        Long asignacionId = 1L;
        AsignacionMesa asignacion = new AsignacionMesa();
        asignacion.setEstado(AsignacionMesa.EstadoAsignacion.ACTIVA);

        when(asignacionMesaRepository.findById(asignacionId)).thenReturn(Optional.of(asignacion));
        when(asignacionMesaRepository.save(asignacion)).thenReturn(asignacion);

        asignacionMesaService.completarAsignacion(asignacionId);

        assertEquals(AsignacionMesa.EstadoAsignacion.COMPLETADA, asignacion.getEstado());
        verify(asignacionMesaRepository).save(asignacion);
    }

    @Test
    void completarAsignacion_WhenAsignacionNoExiste_ShouldThrowException() {
        Long asignacionId = 1L;

        when(asignacionMesaRepository.findById(asignacionId)).thenReturn(Optional.empty());

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            asignacionMesaService.completarAsignacion(asignacionId);
        });

        assertEquals("Asignación no encontrada", thrown.getMessage());
        verify(asignacionMesaRepository, never()).save(any());
    }

    @Test
    void obtenerAsignacionPorId_ShouldReturnOptional() {
        Long id = 1L;
        AsignacionMesa asignacion = new AsignacionMesa();

        when(asignacionMesaRepository.findById(id)).thenReturn(Optional.of(asignacion));

        Optional<AsignacionMesa> resultado = asignacionMesaService.obtenerAsignacionPorId(id);

        assertTrue(resultado.isPresent());
        assertEquals(asignacion, resultado.get());
    }

}
