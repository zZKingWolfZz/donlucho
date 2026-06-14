package com.example.donlucho.services;

import com.example.donlucho.model.Reserva;
import java.util.List;
import java.util.Optional;

public interface IReservaServicio {
    List<Reserva> listarReservas();
    Reserva guardarReserva(Reserva reserva);
    Reserva buscarPorId(Integer id);
    void eliminarReserva(Integer id);
    List<Reserva> buscarPorUsuario(Integer idUsuario);
    List<Reserva> buscarPorHabitacion(Integer idHabitacion);
    List<Reserva> buscarPorUsuarioOrdenadoPorFechaDesc(Integer idUsuario);
    Optional<Reserva> buscarPorIdYUsuario(Integer idReserva, Integer idUsuario);
    List<Reserva> buscarPorDni(String dni);
}
