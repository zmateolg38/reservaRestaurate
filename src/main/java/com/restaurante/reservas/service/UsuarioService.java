package com.restaurante.reservas.service;

import java.util.List;
import java.util.Optional;

import com.restaurante.reservas.entity.Usuario;

public interface UsuarioService {

    Usuario registrarUsuario(Usuario usuario);
    Optional<Usuario> iniciarSesion(String email, String password);
    List<Usuario> obtenerTodosLosUsuarios();
    List<Usuario> obtenerUsuariosPorTipo(Usuario.TipoUsuario tipo);
    Optional<Usuario> obtenerUsuarioPorId(Long id);
    Usuario actualizarUsuario(Long id, Usuario usuarioActualizado);
    void eliminarUsuario(Long id);
    List<Usuario> obtenerMeseros();
    
}
