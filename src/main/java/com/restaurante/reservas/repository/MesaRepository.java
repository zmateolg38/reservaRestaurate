package com.restaurante.reservas.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.restaurante.reservas.entity.Mesa;

@Repository
public interface MesaRepository extends JpaRepository<Mesa, Long> {
    List<Mesa> findByActivaTrue();
    
    List<Mesa> findByEstado(Mesa.EstadoMesa estado);
    
    List<Mesa> findByCapacidadGreaterThanEqual(Integer capacidad);
    
    @Query("SELECT m FROM Mesa m WHERE m.activa = true AND m.estado = 'DISPONIBLE' " +
           "AND m.capacidad >= :capacidad " +
           "AND m.id NOT IN (SELECT r.mesa.id FROM Reserva r WHERE r.fechaInicioReserva = :fechaInicioReserva " +
           "AND r.estado IN ('PENDIENTE', 'CONFIRMADA'))")
    List<Mesa> findMesasDisponiblesPorFechaYCapacidad(@Param("fechaInicioReserva") LocalDateTime fechaInicioReserva, 
                                                      @Param("capacidad") Integer capacidad);
    
    @Query("SELECT COUNT(m) FROM Mesa m WHERE m.activa = true")
    Long countMesasActivas();
}
