package com.example.donlucho.dao;

import com.example.donlucho.model.Reserva;
import java.util.List;
import java.util.Optional;

public interface IReservaDAO extends IGenericDAO<Reserva, Integer> {
    List<Reserva> findByIdUsuario(Integer idUsuario);
    List<Reserva> findByIdHabitacion(Integer idHabitacion);
    List<Reserva> findByIdUsuarioOrderByFechaReservaDesc(Integer idUsuario);
    Optional<Reserva> findByIdReservaAndIdUsuario(Integer idReserva, Integer idUsuario);
    List<Reserva> findByDniContaining(String dni);
}
