package com.restaurante.reservas.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.restaurante.reservas.entity.Mesa;
import com.restaurante.reservas.entity.Reserva;
import com.restaurante.reservas.entity.Usuario;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {
    List<Reserva> findByCliente(Usuario cliente);
    
    List<Reserva> findByEstado(Reserva.EstadoReserva estado);
    
    @Query("SELECT r FROM Reserva r WHERE DATE(r.fechaInicioReserva) = DATE(:fecha)")
    List<Reserva> findReservasPorFecha(@Param("fecha") LocalDateTime fecha);
    
    @Query("SELECT r FROM Reserva r WHERE r.cliente.id = :clienteId AND r.estado = :estado")
    List<Reserva> findByClienteIdAndEstado(@Param("clienteId") Long clienteId, 
                                           @Param("estado") Reserva.EstadoReserva estado);
    
    @Query("SELECT r FROM Reserva r WHERE r.fechaInicioReserva BETWEEN :inicio AND :fin")
    List<Reserva> findReservasEntreFechas(@Param("inicio") LocalDateTime inicio, 
                                          @Param("fin") LocalDateTime fin);
    
    @Query("SELECT r FROM Reserva r WHERE r.recordatorioEnviado = false " +
           "AND r.fechaInicioReserva BETWEEN :ahora AND :limite " +
           "AND r.estado = 'PENDIENTE'")
    List<Reserva> findReservasParaRecordatorio(@Param("ahora") LocalDateTime ahora, 
                                               @Param("limite") LocalDateTime limite);
    
    @Query("SELECT HOUR(r.fechaInicioReserva) as hora, COUNT(r) as cantidad " +
           "FROM Reserva r WHERE r.fechaInicioReserva BETWEEN :inicio AND :fin " +
           "GROUP BY HOUR(r.fechaInicioReserva) ORDER BY cantidad DESC")
    List<Object[]> findEstadisticasHorarios(@Param("inicio") LocalDateTime inicio, 
                                            @Param("fin") LocalDateTime fin);


@Query("SELECT r FROM Reserva r WHERE r.mesa = :mesa " +
       "AND r.estado <> 'CANCELADA' " +
       "AND (:inicio < r.fechaFinReserva AND :fin > r.fechaInicioReserva)")
List<Reserva> findReservasSolapadas(@Param("mesa") Mesa mesa,
                                    @Param("inicio") LocalDateTime inicio,
                                    @Param("fin") LocalDateTime fin);



}


