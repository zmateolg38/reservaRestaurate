package com.restaurante.reservas.dto;


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
    private String estado;
    private boolean activa;

}
