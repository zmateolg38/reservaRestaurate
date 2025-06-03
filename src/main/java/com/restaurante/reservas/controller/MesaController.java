package com.restaurante.reservas.controller;

import lombok.AllArgsConstructor;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.restaurante.reservas.dto.MesaDTO;
import com.restaurante.reservas.entity.Mesa;
import com.restaurante.reservas.service.MesaService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/mesas")
@CrossOrigin(origins = "*")
@AllArgsConstructor
public class MesaController {

    private MesaService mesaService;

    // US004: Consultar disponibilidad de mesas
    @GetMapping("/disponibilidad")
    public ResponseEntity<List<MesaDTO>> consultarDisponibilidad(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaReserva,
            @RequestParam Integer numeroPersonas) {
        List<Mesa> mesasDisponibles = mesaService.verificarDisponibilidad(fechaReserva, numeroPersonas);
        List<MesaDTO> mesaDTOs = mesasDisponibles.stream()
                .map(mesa -> convertirAMesaDTO(mesa))
                .collect(Collectors.toList());

        return ResponseEntity.ok(mesaDTOs);
    }

    @GetMapping
    public ResponseEntity<List<MesaDTO>> obtenerTodasLasMesas() {

        List<Mesa> mesas = mesaService.obtenerTodasLasMesas();
        List<MesaDTO> mesaDTOs = mesas.stream()
                .map(this::convertirAMesaDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(mesaDTOs);

    }

    @GetMapping("/disponibles")
    public ResponseEntity<List<MesaDTO>> obtenerMesasDisponibles() {

        
        List<Mesa> mesas = mesaService.obtenerMesasDisponibles();
        List<MesaDTO> mesaDTOs = mesas.stream()
                .map(this::convertirAMesaDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(mesaDTOs);

    }

    @GetMapping("/{id}")
    public ResponseEntity<MesaDTO> obtenerMesaPorId(@PathVariable Long id) {
        Optional<Mesa> mesa = mesaService.obtenerMesaPorId(id);
        if (mesa.isPresent()) {
            return ResponseEntity.ok( convertirAMesaDTO(mesa.get()));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<MesaDTO> crearMesa(@RequestBody Mesa mesa) {
        Mesa mesaCreada = mesaService.crearMesa(mesa);

        MesaDTO mesaDTO = convertirAMesaDTO(mesaCreada);
        return ResponseEntity.status(HttpStatus.CREATED).body(mesaDTO);

    }

    // US013: Liberar mesa (mesero)
    @PutMapping("/{id}/liberar")
    public ResponseEntity<?> liberarMesa(@PathVariable Long id) {
        try {
            mesaService.liberarMesa(id);
            return ResponseEntity.ok("Mesa liberada correctamente");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}/estado")
    public ResponseEntity<?> actualizarEstadoMesa(@PathVariable Long id,
            @RequestParam Mesa.EstadoMesa estado) {
        try {
            Mesa mesaActualizada = mesaService.actualizarEstadoMesa(id, estado);
            return ResponseEntity.ok(convertirAMesaDTO(mesaActualizada));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/estadisticas/total")
    public ResponseEntity<Long> contarMesasActivas() {
        return ResponseEntity.ok(mesaService.contarMesasActivas());
    }

    private MesaDTO convertirAMesaDTO(Mesa mesa) {
        MesaDTO dto = new MesaDTO();
        dto.setNumero(mesa.getNumero());
        dto.setCapacidad(mesa.getCapacidad());
        dto.setEstado(mesa.getEstado().name());
        dto.setActiva(mesa.getActiva());
        return dto;
    }

}
