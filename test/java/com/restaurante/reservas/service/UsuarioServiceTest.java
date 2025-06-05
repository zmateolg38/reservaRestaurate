package com.restaurante.reservas.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.restaurante.reservas.entity.Usuario;
import com.restaurante.reservas.entity.Usuario.TipoUsuario;
import com.restaurante.reservas.repository.UsuarioRepository;
import com.restaurante.reservas.service.impl.UsuarioServiceImpl;

@ExtendWith(MockitoExtension.class)
public class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioServiceImpl usuarioService;

    @Test
    void registrarUsuario_DeberiaGuardarUsuarioSiEmailNoExiste() {
        Usuario usuario = new Usuario();
        usuario.setEmail("correo@example.com");
        usuario.setPassword("123456");
        usuario.setTipoUsuario(TipoUsuario.CLIENTE);
        usuario.setNombre("Juan");

        when(usuarioRepository.existsByEmail("correo@example.com")).thenReturn(false);
        when(passwordEncoder.encode("123456")).thenReturn("encrypted123");
        when(usuarioRepository.save(usuario)).thenReturn(usuario);


        Usuario usuarioGuardado = usuarioService.registrarUsuario(usuario);

        assertEquals("encrypted123", usuarioGuardado.getPassword());
        assertEquals(TipoUsuario.CLIENTE, usuarioGuardado.getTipoUsuario());
    }

    @Test
    void registrarUsuario_DeberiaLanzarExcepcionSiEmailExiste() {
        Usuario usuario = new Usuario();
        usuario.setEmail("correo@example.com");

        when(usuarioRepository.existsByEmail("correo@example.com")).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                usuarioService.registrarUsuario(usuario)
        );

        assertEquals("El email ya está registrado", exception.getMessage());
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void iniciarSesion_ExitosoConCredencialesValidas() {
        Usuario usuario = new Usuario();
        usuario.setEmail("correo@example.com");
        usuario.setPassword("encodedPassword");

        when(usuarioRepository.findByEmail("correo@example.com"))
                .thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("123456", "encodedPassword"))
                .thenReturn(true);

        Optional<Usuario> resultado = usuarioService.iniciarSesion("correo@example.com", "123456");

        assertTrue(resultado.isPresent());
        assertEquals(usuario, resultado.get());
    }

    @Test
    void iniciarSesion_DeberiaRetornarVacioConCredencialesInvalidas() {
        Usuario usuario = new Usuario();
        usuario.setEmail("correo@example.com");
        usuario.setPassword("encodedPassword");

        when(usuarioRepository.findByEmail("correo@example.com"))
                .thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("wrongpass", "encodedPassword"))
                .thenReturn(false);

        Optional<Usuario> resultado = usuarioService.iniciarSesion("correo@example.com", "wrongpass");

        assertTrue(resultado.isEmpty());
    }

    @Test
    void obtenerTodosLosUsuarios_DeberiaRetornarUsuariosActivos() {
        List<Usuario> usuarios = List.of(new Usuario(), new Usuario());
        when(usuarioRepository.findByActivoTrue()).thenReturn(usuarios);

        List<Usuario> resultado = usuarioService.obtenerTodosLosUsuarios();

        assertEquals(2, resultado.size());
    }

    @Test
    void actualizarUsuario_DeberiaActualizarUsuarioExistente() {
        Usuario existente = new Usuario();
        existente.setId(1L);
        existente.setNombre("Antiguo");
        existente.setTelefono("123");

        Usuario actualizado = new Usuario();
        actualizado.setNombre("Nuevo");
        actualizado.setTelefono("456");
        actualizado.setTipoUsuario(Usuario.TipoUsuario.MESERO);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(i -> i.getArguments()[0]);

        Usuario resultado = usuarioService.actualizarUsuario(1L, actualizado);

        assertEquals("Nuevo", resultado.getNombre());
        assertEquals("456", resultado.getTelefono());
        assertEquals(Usuario.TipoUsuario.MESERO, resultado.getTipoUsuario());
    }

    @Test
    void eliminarUsuario_DeberiaMarcarUsuarioComoInactivo() {
        Usuario usuario = new Usuario();
        usuario.setActivo(true);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        usuarioService.eliminarUsuario(1L);

        assertFalse(usuario.getActivo());
        verify(usuarioRepository).save(usuario);
    }

    @Test
    void obtenerMeseros_DeberiaRetornarMeserosActivos() {
        List<Usuario> meseros = List.of(new Usuario());
        when(usuarioRepository.findByTipoUsuarioAndActivo(Usuario.TipoUsuario.MESERO)).thenReturn(meseros);

        List<Usuario> resultado = usuarioService.obtenerMeseros();

        assertEquals(1, resultado.size());
    }



    
}