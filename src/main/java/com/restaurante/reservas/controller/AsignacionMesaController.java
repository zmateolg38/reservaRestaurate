package com.restaurante.reservas.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.restaurante.reservas.entity.AsignacionMesa;
import com.restaurante.reservas.entity.Usuario;
import com.restaurante.reservas.service.AsignacionMesaService;
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
    
    // US014: Asignar mesas a meseros por turnos
    @PostMapping
    public ResponseEntity<?> asignarMesaAMesero(@RequestBody AsignacionMesa asignacion) {
        try {
            AsignacionMesa asignacionCreada = asignacionMesaService.asignarMesaAMesero(asignacion);
            return ResponseEntity.ok(asignacionCreada);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @GetMapping("/mesero/{meseroId}")
    public ResponseEntity<?> obtenerAsignacionesPorMesero(@PathVariable Long meseroId) {
        Optional<Usuario> mesero = usuarioService.obtenerUsuarioPorId(meseroId);
        if (mesero.isPresent()) {
            List<AsignacionMesa> asignaciones = asignacionMesaService.obtenerAsignacionesPorMesero(mesero.get());
            return ResponseEntity.ok(asignaciones);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/fecha")
    public ResponseEntity<List<AsignacionMesa>> obtenerAsignacionesPorFecha(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return ResponseEntity.ok(asignacionMesaService.obtenerAsignacionesPorFecha(fecha));
    }
    
    @GetMapping("/mesero/{meseroId}/fecha")
    public ResponseEntity<List<AsignacionMesa>> obtenerAsignacionesMeseroPorFecha(
            @PathVariable Long meseroId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return ResponseEntity.ok(asignacionMesaService.obtenerAsignacionesMeseroPorFecha(meseroId, fecha));
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
            return ResponseEntity.ok(asignacion.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
