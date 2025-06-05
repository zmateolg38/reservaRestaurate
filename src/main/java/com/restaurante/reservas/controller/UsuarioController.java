package com.restaurante.reservas.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.restaurante.reservas.dto.UsuarioDTO;
import com.restaurante.reservas.entity.Usuario;
import com.restaurante.reservas.service.UsuarioService;

import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
@AllArgsConstructor
public class UsuarioController {

    private UsuarioService usuarioService;

    // US001: Registro de usuario
    @PostMapping("/registro")
    public ResponseEntity<?> registrarUsuario(@RequestBody UsuarioDTO dto) {
        try {

            Usuario usuario = new Usuario();
            usuario.setEmail(dto.getEmail());
            usuario.setPassword(dto.getPassword());
            usuario.setNombre(dto.getNombre());
            usuario.setTelefono(dto.getTelefono());
            usuario.setTipoUsuario(dto.getTipoUsuario());
            usuario.setFechaRegistro(LocalDateTime.now());
            usuario.setActivo(true);

            usuarioService.registrarUsuario(usuario);
            return ResponseEntity.status(HttpStatus.CREATED).body(dto);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // US002: Inicio de sesión
    @PostMapping("/login")
    public ResponseEntity<?> iniciarSesion(@RequestBody LoginRequest loginRequest) {
        Optional<Usuario> usuario = usuarioService.iniciarSesion(
                loginRequest.getEmail(), loginRequest.getPassword());

        if (usuario.isPresent()) {
            UsuarioDTO dto = new UsuarioDTO();
            dto.setEmail(usuario.map(Usuario::getEmail).orElse(null));
            dto.setNombre(usuario.map(Usuario::getNombre).orElse(null));
            dto.setTipoUsuario(usuario.map(Usuario::getTipoUsuario).orElse(null));
            dto.setTelefono(usuario.map(Usuario::getTelefono).orElse(null));
            dto.setPassword(usuario.map(Usuario::getPassword).orElse(null));
            return ResponseEntity.ok(dto);
        } else {
            return ResponseEntity.badRequest().body("Credenciales inválidas");
        }
    }

    // US008: Gestión de usuarios (administrador)
    @GetMapping
    public ResponseEntity<?> obtenerTodosLosUsuarios() {

        List<Usuario> usuarios = usuarioService.obtenerTodosLosUsuarios();
        if (usuarios.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        List<UsuarioDTO> usuariosDTO = usuarios.stream().map(usuario -> {
            UsuarioDTO dto = new UsuarioDTO();
            dto.setEmail(usuario.getEmail());
            dto.setNombre(usuario.getNombre());
            dto.setTipoUsuario(usuario.getTipoUsuario());
            dto.setTelefono(usuario.getTelefono());
            dto.setPassword(usuario.getPassword());
            return dto;
        }).toList();

        return ResponseEntity.ok(usuariosDTO);

    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerUsuarioPorId(@PathVariable Long id) {
        Optional<Usuario> usuario = usuarioService.obtenerUsuarioPorId(id);
        if (usuario.isPresent()) {

            UsuarioDTO dto = new UsuarioDTO();
            dto.setEmail(usuario.get().getEmail());
            dto.setNombre(usuario.get().getNombre());
            dto.setTipoUsuario(usuario.get().getTipoUsuario());
            dto.setTelefono(usuario.get().getTelefono());
            dto.setPassword(usuario.get().getPassword());

            return ResponseEntity.ok(dto);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarUsuario(@PathVariable Long id, @RequestBody UsuarioDTO dto) {
        try {

            Usuario usuario = new Usuario();
            usuario.setEmail(dto.getEmail());
            usuario.setPassword(dto.getPassword());
            usuario.setNombre(dto.getNombre());
            usuario.setTelefono(dto.getTelefono());
            usuario.setTipoUsuario(dto.getTipoUsuario());
            usuario.setActivo(true);

            usuarioService.actualizarUsuario(id, usuario);
            return ResponseEntity.ok(dto);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarUsuario(@PathVariable Long id) {
        try {
            usuarioService.eliminarUsuario(id);
            return ResponseEntity.ok("Usuario eliminado correctamente");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/meseros")
    public ResponseEntity<?> obtenerMeseros() {

        // US009: Obtener lista de meseros
        List<Usuario> meseros = usuarioService.obtenerMeseros();
        if (meseros.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        List<UsuarioDTO> meserosDTO = meseros.stream().map(mesero -> {
            UsuarioDTO dto = new UsuarioDTO();
            dto.setEmail(mesero.getEmail());
            dto.setNombre(mesero.getNombre());
            dto.setTipoUsuario(mesero.getTipoUsuario());
            dto.setTelefono(mesero.getTelefono());
            dto.setPassword(mesero.getPassword());
            return dto;
        }).toList();

        return ResponseEntity.ok(meserosDTO);


    }

    // Clase interna para el request de login
    public static class LoginRequest {
        private String email;
        private String password;

        // Getters y Setters
        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}
