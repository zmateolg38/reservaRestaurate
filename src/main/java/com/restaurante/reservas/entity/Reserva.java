package com.restaurante.reservas.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "reservas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Reserva {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Usuario cliente;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mesa_id", nullable = false)
    private Mesa mesa;
    
    @Column(name = "fecha_inicio_reserva", nullable = false)
    private LocalDateTime fechaInicioReserva;

    @Column(name = "fecha_fin_reserva", nullable = false)
    private LocalDateTime fechaFinReserva;
    
    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;
    
    @Column(name = "numero_personas", nullable = false)
    private Integer numeroPersonas;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoReserva estado;
    
    @Column(name = "comentarios")
    private String comentarios;
    
    @Column(name = "recordatorio_enviado")
    private Boolean recordatorioEnviado = false;
    
    public enum EstadoReserva {
        PENDIENTE, CONFIRMADA, CANCELADA, COMPLETADA, NO_ASISTIO
    }
}
