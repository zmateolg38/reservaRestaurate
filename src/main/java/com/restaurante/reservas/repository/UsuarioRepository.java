package com.restaurante.reservas.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.restaurante.reservas.entity.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);
    
    List<Usuario> findByTipoUsuario(Usuario.TipoUsuario tipoUsuario);
    
    List<Usuario> findByActivoTrue();
    
    @Query("SELECT u FROM Usuario u WHERE u.tipoUsuario = :tipo AND u.activo = true")
    List<Usuario> findByTipoUsuarioAndActivo(@Param("tipo") Usuario.TipoUsuario tipo);
    
    boolean existsByEmail(String email);
}
