package com.restaurante.reservas.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Entity
@Table(name = "usuarios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String email;
    
    @Column(nullable = false)
    private String password;
    
    @Column(nullable = false)
    private String nombre;
    
    @Column(nullable = false)
    private String telefono;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoUsuario tipoUsuario;
    
    @Column(name = "fecha_registro")
    private LocalDateTime fechaRegistro;
    
    @Column(name = "activo")
    private Boolean activo = true;
    
    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL)
    private List<Reserva> reservas;
    
    @OneToMany(mappedBy = "mesero", cascade = CascadeType.ALL)
    private List<AsignacionMesa> asignacionesMesas;
    
    public enum TipoUsuario {
        CLIENTE, ADMINISTRADOR, MESERO
    }

}
