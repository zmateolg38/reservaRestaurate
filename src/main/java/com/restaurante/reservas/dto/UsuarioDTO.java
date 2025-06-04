package com.restaurante.reservas.dto;

import com.restaurante.reservas.entity.Usuario;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioDTO {
    private String email;
    private String password;
    private String nombre;
    private String telefono;
    private Usuario.TipoUsuario tipoUsuario;
}