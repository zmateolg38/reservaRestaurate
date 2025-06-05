package com.restaurante.reservas.controller;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.restaurante.reservas.dto.AsignacionMesaDTO;
import com.restaurante.reservas.entity.AsignacionMesa;
import com.restaurante.reservas.entity.Mesa;
import com.restaurante.reservas.entity.Usuario;
import com.restaurante.reservas.service.AsignacionMesaService;
import com.restaurante.reservas.service.MesaService;
import com.restaurante.reservas.service.UsuarioService;

import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/asignaciones")
@CrossOrigin(origins = "*")
@AllArgsConstructor
public class AsignacionMesaController {

    private AsignacionMesaService asignacionMesaService;

    private UsuarioService usuarioService;

    private MesaService mesaService;

    // US014: Asignar mesas a meseros por turnos
    @PostMapping
    public ResponseEntity<?> asignarMesaAMesero(@RequestBody AsignacionMesaDTO dto) {
        try {

            // Convertir DTO a entidad
            AsignacionMesa asignacion = new AsignacionMesa();
            asignacion.setFecha(dto.getFecha());
            asignacion.setHoraInicio(dto.getHoraInicio());
            asignacion.setHoraFin(dto.getHoraFin());
            asignacion.setEstado(dto.getEstado());

            // Obtener relaciones: Mesero y Mesa
            Usuario mesero = usuarioService.obtenerUsuarioPorId(dto.getMeseroId())
                    .orElseThrow(() -> new RuntimeException("Mesero no encontrado con ID: " + dto.getMeseroId()));
            Mesa mesa = mesaService.obtenerMesaPorId(dto.getMesaId())
                    .orElseThrow(() -> new RuntimeException("Mesa no encontrada con ID: " + dto.getMesaId()));

            asignacion.setMesero(mesero);
            asignacion.setMesa(mesa);

            AsignacionMesa asignacionCreada = asignacionMesaService.asignarMesaAMesero(asignacion);
            // Convertir a DTO para la respuesta
            AsignacionMesaDTO dtoRespuesta = convertirADTO(asignacionCreada);
            return ResponseEntity.ok(dtoRespuesta);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/mesero/{meseroId}")
    public ResponseEntity<?> obtenerAsignacionesPorMesero(@PathVariable Long meseroId) {
        Optional<Usuario> mesero = usuarioService.obtenerUsuarioPorId(meseroId);
        if (mesero.isPresent()) {

            List<AsignacionMesa> asignaciones = asignacionMesaService.obtenerAsignacionesPorMesero(mesero.get());
            List<AsignacionMesaDTO> dtoList = asignaciones.stream()
                    .map(this::convertirADTO)
                    .toList();
            return ResponseEntity.ok(dtoList);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/fecha")
    public ResponseEntity<List<AsignacionMesaDTO>> obtenerAsignacionesPorFecha(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        List<AsignacionMesa> asignaciones = asignacionMesaService.obtenerAsignacionesPorFecha(fecha);

        List<AsignacionMesaDTO> dtoList = asignaciones.stream()
                .map(this::convertirADTO)
                .toList();
        return ResponseEntity.ok(dtoList);
    }

    @GetMapping("/mesero/{meseroId}/fecha")
    public ResponseEntity<List<AsignacionMesaDTO>> obtenerAsignacionesMeseroPorFecha(
            @PathVariable Long meseroId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {

        List<AsignacionMesa> asignaciones = asignacionMesaService.obtenerAsignacionesMeseroPorFecha(meseroId, fecha);

        List<AsignacionMesaDTO> dtoList = asignaciones.stream()
                .map(this::convertirADTO)
                .toList();
        return ResponseEntity.ok(dtoList);

    }

    @PutMapping("/{id}/completar")
    public ResponseEntity<?> completarAsignacion(@PathVariable Long id) {
        try {
            asignacionMesaService.completarAsignacion(id);
            return ResponseEntity.ok("Asignación completada");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerAsignacionPorId(@PathVariable Long id) {
        Optional<AsignacionMesa> asignacion = asignacionMesaService.obtenerAsignacionPorId(id);
        if (asignacion.isPresent()) {
            return ResponseEntity.ok(convertirADTO(asignacion.get()));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    private AsignacionMesaDTO convertirADTO(AsignacionMesa asignacion) {
        AsignacionMesaDTO dto = new AsignacionMesaDTO();
        dto.setId(asignacion.getId());

        dto.setMeseroId(asignacion.getMesero().getId());
        dto.setMeseroNombre(asignacion.getMesero().getNombre());

        dto.setMesaId(asignacion.getMesa().getId());
        dto.setNumeroMesa(asignacion.getMesa().getNumero());

        dto.setFecha(asignacion.getFecha());
        dto.setHoraInicio(asignacion.getHoraInicio());
        dto.setHoraFin(asignacion.getHoraFin());

        dto.setEstado(asignacion.getEstado());

        return dto;
    }

}
