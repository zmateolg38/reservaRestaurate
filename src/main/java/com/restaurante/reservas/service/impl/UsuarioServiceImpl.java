package com.restaurante.reservas.service.impl;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.restaurante.reservas.entity.Usuario;
import com.restaurante.reservas.repository.UsuarioRepository;
import com.restaurante.reservas.service.UsuarioService;

import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {

    private UsuarioRepository usuarioRepository;

    private PasswordEncoder passwordEncoder;

    /**
     * Registra un nuevo usuario en el sistema.
     * 
     * @param usuario Objeto Usuario con los datos del nuevo usuario.
     * @return Usuario registrado y guardado en la base de datos.
     */
    @Override
    public Usuario registrarUsuario(Usuario usuario) {
        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new RuntimeException("El email ya está registrado");
        }

        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        usuario.setFechaRegistro(LocalDateTime.now());
        usuario.setActivo(true);

        if (usuario.getTipoUsuario() == null) {
            usuario.setTipoUsuario(Usuario.TipoUsuario.CLIENTE);
        }

        return usuarioRepository.save(usuario);
    }

    /**
     * Inicia sesión de un usuario verificando email y contraseña.
     * 
     * @param email    Email del usuario.
     * @param password Contraseña del usuario.
     * @return Usuario autenticado si las credenciales son correctas, vacío en caso
     *         contrario.
     */
    @Override
    public Optional<Usuario> iniciarSesion(String email, String password) {
        Optional<Usuario> usuario = usuarioRepository.findByEmail(email);

        if (usuario.isPresent() && passwordEncoder.matches(password, usuario.get().getPassword())) {
            return usuario;
        }

        return Optional.empty();
    }

    /**
     * Obtiene todos los usuarios activos del sistema.
     * 
     * @return Lista de usuarios activos.
     */
    @Override
    public List<Usuario> obtenerTodosLosUsuarios() {
        return usuarioRepository.findByActivoTrue();
    }

    /**
     * Obtiene usuarios por tipo de usuario.
     * 
     * @param tipo Tipo de usuario a filtrar.
     * @return Lista de usuarios del tipo especificado.
     */
    @Override
    public List<Usuario> obtenerUsuariosPorTipo(Usuario.TipoUsuario tipo) {
        return usuarioRepository.findByTipoUsuarioAndActivo(tipo);
    }

    /**
     * Obtiene un usuario por su ID.
     * 
     * @param id ID del usuario a buscar.
     * @return Usuario encontrado, si existe.
     */
    @Override
    public Optional<Usuario> obtenerUsuarioPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    /**
     * Actualiza los datos de un usuario existente.
     * 
     * @param id                 ID del usuario a actualizar.
     * @param usuarioActualizado Objeto Usuario con los nuevos datos.
     * @return Usuario actualizado y guardado en la base de datos.
     */
    @Override
    public Usuario actualizarUsuario(Long id, Usuario usuarioActualizado) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        usuario.setNombre(usuarioActualizado.getNombre());
        usuario.setTelefono(usuarioActualizado.getTelefono());
        usuario.setTipoUsuario(usuarioActualizado.getTipoUsuario());

        return usuarioRepository.save(usuario);
    }

    /**
     * Elimina un usuario del sistema, marcándolo como inactivo.
     * 
     * @param id ID del usuario a eliminar.
     */
    @Override
    public void eliminarUsuario(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        usuario.setActivo(false);
        usuarioRepository.save(usuario);
    }

    /**
     * Obtiene todos los meseros activos del sistema.
     * 
     * @return Lista de usuarios con tipo MESERO.
     */
    @Override
    public List<Usuario> obtenerMeseros() {
        return usuarioRepository.findByTipoUsuarioAndActivo(Usuario.TipoUsuario.MESERO);
    }
}
