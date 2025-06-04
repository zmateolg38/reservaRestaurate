package com.restaurante.reservas.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.AllArgsConstructor;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "asignaciones_mesas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AsignacionMesa {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mesero_id", nullable = false)
    private Usuario mesero;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mesa_id", nullable = false)
    private Mesa mesa;
    
    @Column(nullable = false)
    private LocalDate fecha;
    
    @Column(name = "hora_inicio", nullable = false)
    private LocalTime horaInicio;
    
    @Column(name = "hora_fin", nullable = false)
    private LocalTime horaFin;
    
    @Enumerated(EnumType.STRING)
    private EstadoAsignacion estado;
    
    public enum EstadoAsignacion {
        ACTIVA, COMPLETADA, CANCELADA
    }
}
