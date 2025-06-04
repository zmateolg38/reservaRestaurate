package com.restaurante.reservas.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.restaurante.reservas.entity.Mesa;
import com.restaurante.reservas.entity.Reserva;
import com.restaurante.reservas.entity.Usuario;
import com.restaurante.reservas.repository.ReservaRepository;
import com.restaurante.reservas.service.EmailService;
import com.restaurante.reservas.service.MesaService;
import com.restaurante.reservas.service.ReservaService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ReservaServiceImpl implements ReservaService {

    private ReservaRepository reservaRepository;

    private MesaService mesaService;

    private EmailService emailService;

    @Override
    public Reserva crearReserva(Reserva reserva) {

        // Validar capacidad de la mesa
        if (reserva.getNumeroPersonas() > reserva.getMesa().getCapacidad()) {
            throw new RuntimeException("La cantidad de personas excede la capacidad de la mesa");
        }

        // Asegurarse de que fechaFinReserva sea 30 minutos después de la
        // fechaInicioReserva
        reserva.setFechaFinReserva(reserva.getFechaInicioReserva().plusMinutes(30));

        // Verificar disponibilidad en ese rango de tiempo
        boolean disponible = estaDisponible(
                reserva.getMesa(),
                reserva.getFechaInicioReserva(),
                reserva.getFechaFinReserva());

        if (!disponible) {
            throw new RuntimeException("La mesa no está disponible en el horario solicitado");
        }

        // Configurar estado inicial
        reserva.setEstado(Reserva.EstadoReserva.PENDIENTE);
        reserva.setFechaCreacion(LocalDateTime.now());
        reserva.setRecordatorioEnviado(false);

        // Guardar la reserva
        Reserva reservaGuardada = reservaRepository.save(reserva);

        // Cambiar estado de la mesa si aplicable
        mesaService.actualizarEstadoMesa(reserva.getMesa().getId(), Mesa.EstadoMesa.RESERVADA);

        return reservaGuardada;

    }

    /**
     * Obtiene todas las reservas realizadas por un cliente.
     * 
     * @param cliente Usuario que representa al cliente.
     * @return Lista de reservas del cliente.
     */
    @Override
    public List<Reserva> obtenerReservasPorCliente(Usuario cliente) {
        return reservaRepository.findByCliente(cliente);
    }

    /**
     * Obtiene todas las reservas en el sistema.
     * 
     * @return Lista de todas las reservas.
     */
    @Override
    public List<Reserva> obtenerTodasLasReservas() {
        return reservaRepository.findAll();
    }

    /**
     * Obtiene las reservas del día especificado.
     * 
     * @param fecha Fecha y hora del día a consultar.
     * @return Lista de reservas para el día especificado.
     */
    @Override
    public List<Reserva> obtenerReservasDelDia(LocalDateTime fecha) {
        return reservaRepository.findReservasPorFecha(fecha);
    }

    /**
     * Modifica una reserva existente.
     * 
     * @param id                ID de la reserva a modificar.
     * @param reservaModificada Objeto Reserva con los nuevos detalles.
     * @return Reserva modificada y guardada en la base de datos.
     */
    @Override
    public Reserva modificarReserva(Long id, Reserva reservaModificada) {

        Reserva reservaExistente = reservaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));

        if (reservaExistente.getEstado() == Reserva.EstadoReserva.CANCELADA) {
            throw new RuntimeException("No se puede modificar una reserva cancelada");
        }

        // Validar capacidad de la mesa
        if (reservaModificada.getNumeroPersonas() > reservaExistente.getMesa().getCapacidad()) {
            throw new RuntimeException("El número de personas excede la capacidad de la mesa");
        }

        reservaExistente.setFechaInicioReserva(reservaModificada.getFechaInicioReserva());
        reservaExistente.setFechaFinReserva(reservaModificada.getFechaFinReserva());
        reservaExistente.setNumeroPersonas(reservaModificada.getNumeroPersonas());
        reservaExistente.setComentarios(reservaModificada.getComentarios());

        return reservaRepository.save(reservaExistente);

    }

    /**
     * Cancela una reserva existente.
     * 
     * @param id ID de la reserva a cancelar.
     */
    @Override
    public void cancelarReserva(Long id) {
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));

        reserva.setEstado(Reserva.EstadoReserva.CANCELADA);
        reservaRepository.save(reserva);

        // Liberar la mesa
        mesaService.liberarMesa(reserva.getMesa().getId());
    }

    /**
     * Confirma una reserva existente.
     * 
     * @param id ID de la reserva a confirmar.
     */
    @Override
    public void confirmarReserva(Long id) {
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));

        reserva.setEstado(Reserva.EstadoReserva.CONFIRMADA);
        reservaRepository.save(reserva);
    }

    /**
     * Envía recordatorios de reservas a los clientes.
     * Busca reservas que estén próximas (12 horas antes) y envía un recordatorio
     * por email.
     */
    @Override
    public void enviarRecordatorios() {
        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime limite = ahora.plusHours(12); // Recordatorio 12 horas antes

        List<Reserva> reservasParaRecordatorio = reservaRepository.findReservasParaRecordatorio(ahora, limite);

        for (Reserva reserva : reservasParaRecordatorio) {
            emailService.enviarRecordatorioReserva(reserva);
            reserva.setRecordatorioEnviado(true);
            reservaRepository.save(reserva);
        }
    }

    /**
     * Obtiene estadísticas de reservas por horarios en un rango de fechas.
     * 
     * @param inicio Fecha y hora de inicio del rango.
     * @param fin    Fecha y hora de fin del rango.
     * @return Lista de objetos con estadísticas de reservas por horario.
     */
    @Override
    public List<Object[]> obtenerEstadisticasHorarios(LocalDateTime inicio, LocalDateTime fin) {
        return reservaRepository.findEstadisticasHorarios(inicio, fin);
    }

    /**
     * Obtiene una reserva por su ID.
     * 
     * @param id ID de la reserva a buscar.
     * @return Reserva encontrada, si existe.
     */
    @Override
    public Optional<Reserva> obtenerReservaPorId(Long id) {
        return reservaRepository.findById(id);
    }

    public boolean estaDisponible(Mesa mesa, LocalDateTime inicio, LocalDateTime fin) {
        List<Reserva> reservasSolapadas = reservaRepository.findReservasSolapadas(mesa, inicio, fin);
        return reservasSolapadas.isEmpty();
    }

}
