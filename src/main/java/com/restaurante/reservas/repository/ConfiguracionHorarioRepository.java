package com.restaurante.reservas.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.restaurante.reservas.entity.ConfiguracionHorario;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConfiguracionHorarioRepository extends JpaRepository<ConfiguracionHorario, Long> {
    List<ConfiguracionHorario> findByActivoTrue();
    
    Optional<ConfiguracionHorario> findByDiaSemanaAndActivoTrue(ConfiguracionHorario.DiaSemana diaSemana);
    
    @Query("SELECT c FROM ConfiguracionHorario c WHERE c.diaSemana = :dia AND c.activo = true")
    List<ConfiguracionHorario> findConfiguracionPorDia(@Param("dia") ConfiguracionHorario.DiaSemana dia);
    
    @Query("SELECT SUM(c.mesasDisponibles) FROM ConfiguracionHorario c WHERE c.activo = true")
    Long getTotalMesasDisponibles();
}
