package com.restaurante.reservas.controller;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.restaurante.reservas.dto.ReservaCreacionDTO;
import com.restaurante.reservas.dto.ReservaRespuestaDTO;
import com.restaurante.reservas.entity.Mesa;
import com.restaurante.reservas.entity.Reserva;
import com.restaurante.reservas.entity.Usuario;
import com.restaurante.reservas.service.MesaService;
import com.restaurante.reservas.service.ReservaService;
import com.restaurante.reservas.service.UsuarioService;

import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/reservas")
@CrossOrigin(origins = "*")
@AllArgsConstructor
public class ReservaController {
    
    private ReservaService reservaService;
    
    private UsuarioService usuarioService;

    private MesaService mesaService;
    
    // US003: Hacer reserva en línea
    @PostMapping
    public ResponseEntity<?> crearReserva(@RequestBody ReservaCreacionDTO dto) {
        try {
            Optional<Usuario> clienteOpt = usuarioService.obtenerUsuarioPorId(dto.getClienteId());
            
            if (!clienteOpt.isPresent()) {
                return ResponseEntity.badRequest().body("Cliente no encontrado");
            }

            Optional<Mesa> mesaOpt = mesaService.obtenerMesaPorId(dto.getMesaId());
            if (!mesaOpt.isPresent()) {
                return ResponseEntity.badRequest().body("Mesa no encontrada");
            }

            Mesa mesa = mesaOpt.get();
            Usuario cliente = clienteOpt.get();

            // LocalDateTime fechaInicio = dto.getFechaInicioReserva();
            // LocalDateTime fechaFin = fechaInicio.plusMinutes(30);

            // Crear reserva
            Reserva reserva = new Reserva();
            reserva.setCliente(cliente);
            reserva.setMesa(mesa);
            reserva.setFechaInicioReserva(dto.getFechaInicioReserva());
            reserva.setFechaFinReserva(dto.getFechaFinReserva());
            reserva.setFechaCreacion(LocalDateTime.now());
            reserva.setNumeroPersonas(dto.getNumeroPersonas());
            reserva.setComentarios(dto.getComentarios());
            reserva.setEstado(Reserva.EstadoReserva.PENDIENTE);
            reserva.setRecordatorioEnviado(false);

            Reserva reservaCreada = reservaService.crearReserva(reserva);
            return ResponseEntity.ok(convertirARespuestaDTO(reservaCreada));

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // US005: Modificar reserva
    @PutMapping("/{id}")
    public ResponseEntity<?> modificarReserva(@PathVariable Long id, @RequestBody ReservaCreacionDTO dto) {
        try {

            Optional<Reserva> reservaExistente = reservaService.obtenerReservaPorId(id);
            if (!reservaExistente.isPresent()) {
                return ResponseEntity.notFound().build();
            }

            Reserva reserva = reservaExistente.get();
            Optional<Usuario> cliente = usuarioService.obtenerUsuarioPorId(dto.getClienteId());
            if (!cliente.isPresent()) {
                return ResponseEntity.badRequest().body("Cliente no encontrado");
            }

            Optional<Mesa> mesa = mesaService.obtenerMesaPorId(dto.getMesaId());
            if (!mesa.isPresent()) {
                return ResponseEntity.badRequest().body("Mesa no encontrada");
            }
            reserva.setCliente(cliente.get());
            reserva.setMesa(mesa.get());
            reserva.setFechaInicioReserva(dto.getFechaInicioReserva());
            reserva.setFechaFinReserva(dto.getFechaFinReserva());
            reserva.setNumeroPersonas(dto.getNumeroPersonas());
            reserva.setComentarios(dto.getComentarios());
            // reserva.setEstado(Reserva.EstadoReserva.PENDIENTE);

            Reserva reservaModificada = reservaService.modificarReserva(id, reserva);
            return ResponseEntity.ok(reservaModificada);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    // US006: Cancelar reserva
    @DeleteMapping("/{id}")
    public ResponseEntity<?> cancelarReserva(@PathVariable Long id) {
        try {
            reservaService.cancelarReserva(id);
            return ResponseEntity.ok("Reserva cancelada correctamente");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    // US009: Ver todas las reservas (administrador)
    @GetMapping
    public ResponseEntity<?> obtenerTodasLasReservas() {


        List<?> reservasDTO = reservaService.obtenerTodasLasReservas().stream()
            .map(this::convertirARespuestaDTO)
            .toList();

        return ResponseEntity.ok(reservasDTO);
    }
    
    // US012: Ver reservas del día (mesero)
    @GetMapping("/fecha")
    public ResponseEntity<List<ReservaRespuestaDTO>> obtenerReservasDelDia(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fecha) {

                List<ReservaRespuestaDTO> reservas = reservaService.obtenerReservasDelDia(fecha)
                .stream()
                .map( reserva -> convertirARespuestaDTO(reserva) )
                .toList();

        return ResponseEntity.ok(reservas);
    }
    
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<?> obtenerReservasPorCliente(@PathVariable Long clienteId) {
        Optional<Usuario> cliente = usuarioService.obtenerUsuarioPorId(clienteId);
        if (cliente.isPresent()) {
            List<Reserva> reservas = reservaService.obtenerReservasPorCliente(cliente.get());
            List<ReservaRespuestaDTO> reservaRespuestaDTOs = reservas.stream()
                .map( reserva -> convertirARespuestaDTO(reserva) )
                .toList();
            return ResponseEntity.ok(reservaRespuestaDTOs);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerReservaPorId(@PathVariable Long id) {
        Optional<Reserva> reserva = reservaService.obtenerReservaPorId(id);
        if (reserva.isPresent()) {
            return ResponseEntity.ok( convertirARespuestaDTO(reserva.get()) );
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    // Confirmar reserva
    @PutMapping("/{id}/confirmar")
    public ResponseEntity<?> confirmarReserva(@PathVariable Long id) {
        try {
            reservaService.confirmarReserva(id);
            return ResponseEntity.ok("Reserva confirmada");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    // US011: Estadísticas de reservas
    @GetMapping("/estadisticas")
    public ResponseEntity<List<Object[]>> obtenerEstadisticasHorarios(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {
        return ResponseEntity.ok(reservaService.obtenerEstadisticasHorarios(inicio, fin));
    }
    
    // US007: Enviar recordatorios
    @PostMapping("/recordatorios")
    public ResponseEntity<?> enviarRecordatorios() {
        try {
            reservaService.enviarRecordatorios();
            return ResponseEntity.ok("Recordatorios enviados");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al enviar recordatorios: " + e.getMessage());
        }
    }

    public ReservaRespuestaDTO convertirARespuestaDTO(Reserva reserva) {
    return new ReservaRespuestaDTO(
        reserva.getCliente().getNombre(),
        reserva.getCliente().getEmail(),
        reserva.getMesa().getId(),
        reserva.getFechaInicioReserva(),
        reserva.getFechaFinReserva(),
        reserva.getNumeroPersonas(),
        reserva.getEstado().name(),
        reserva.getComentarios()
    );
}


}