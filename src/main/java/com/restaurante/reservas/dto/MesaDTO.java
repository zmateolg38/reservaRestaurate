package com.restaurante.reservas.dto;


import com.restaurante.reservas.entity.Mesa;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MesaDTO {

    private String numero;
    private int capacidad;
    private Mesa.EstadoMesa estado;
    private boolean activa;

}
