package com.restaurante.reservas.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalTime;

@Entity
@Table(name = "configuracion_horarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConfiguracionHorario {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DiaSemana diaSemana;
    
    @Column(name = "hora_inicio", nullable = false)
    private LocalTime horaInicio;
    
    @Column(name = "hora_fin", nullable = false)
    private LocalTime horaFin;
    
    @Column(name = "mesas_disponibles", nullable = false)
    private Integer mesasDisponibles;
    
    @Column(name = "duracion_reserva_minutos", nullable = false)
    private Integer duracionReservaMinutos;
    
    @Column(name = "activo")
    private Boolean activo = true;
    
    public enum DiaSemana {
        LUNES, MARTES, MIERCOLES, JUEVES, VIERNES, SABADO, DOMINGO
    }
}
