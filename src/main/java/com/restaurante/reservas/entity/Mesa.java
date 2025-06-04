package com.restaurante.reservas.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

@Entity
@Table(name = "mesas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Mesa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String numero;
    
    @Column(nullable = false)
    private Integer capacidad;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoMesa estado;
    
    @Column(name = "activa")
    private Boolean activa = true;
    
    @OneToMany(mappedBy = "mesa", cascade = CascadeType.ALL)
    private List<Reserva> reservas;
    
    @OneToMany(mappedBy = "mesa", cascade = CascadeType.ALL)
    private List<AsignacionMesa> asignaciones;
    
    public enum EstadoMesa {
        DISPONIBLE, OCUPADA, RESERVADA, FUERA_DE_SERVICIO
    }
}
