package com.restaurante.reservas.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.restaurante.reservas.entity.AsignacionMesa;
import com.restaurante.reservas.entity.Usuario;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AsignacionMesaRepository extends JpaRepository<AsignacionMesa, Long> {
    List<AsignacionMesa> findByMesero(Usuario mesero);
    
    List<AsignacionMesa> findByFecha(LocalDate fecha);
    
    @Query("SELECT a FROM AsignacionMesa a WHERE a.mesero.id = :meseroId AND a.fecha = :fecha")
    List<AsignacionMesa> findByMeseroIdAndFecha(@Param("meseroId") Long meseroId, 
                                                @Param("fecha") LocalDate fecha);
    
    @Query("SELECT a FROM AsignacionMesa a WHERE a.fecha = :fecha AND a.estado = 'ACTIVA'")
    List<AsignacionMesa> findAsignacionesActivasPorFecha(@Param("fecha") LocalDate fecha);
    
    @Query("SELECT a FROM AsignacionMesa a WHERE a.mesa.id = :mesaId AND a.fecha = :fecha " +
           "AND a.estado = 'ACTIVA'")
    List<AsignacionMesa> findByMesaIdAndFechaAndEstadoActiva(@Param("mesaId") Long mesaId, 
                                                            @Param("fecha") LocalDate fecha);
}
