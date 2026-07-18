package com.example.donlucho.services;

import com.example.donlucho.repository.ReservaRepositorio;
import com.example.donlucho.model.Reserva;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ReservaServicio implements IReservaServicio {

    @Autowired
    private ReservaRepositorio reservaRepositorio;

    @Override
    public List<Reserva> listarReservas() {
        return reservaRepositorio.findAll();
    }

    @Override
    public Reserva guardarReserva(Reserva reserva) {
        return reservaRepositorio.save(reserva);
    }

    @Override
    public Reserva buscarPorId(Integer id) {
        return reservaRepositorio.findById(id).orElse(null);
    }

    @Override
    public void eliminarReserva(Integer id) {
        reservaRepositorio.deleteById(id);
    }

    @Override
    public List<Reserva> buscarPorUsuario(Integer idUsuario) {
        return reservaRepositorio.findByIdUsuario(idUsuario);
    }

    @Override
    public List<Reserva> buscarPorHabitacion(Integer idHabitacion) {
        return reservaRepositorio.findByIdHabitacion(idHabitacion);
    }

    @Override
    public List<Reserva> buscarPorUsuarioOrdenadoPorFechaDesc(Integer idUsuario) {
        return reservaRepositorio.findByIdUsuarioOrderByFechaReservaDesc(idUsuario);
    }

    @Override
    public Optional<Reserva> buscarPorIdYUsuario(Integer idReserva, Integer idUsuario) {
        return reservaRepositorio.findByIdReservaAndIdUsuario(idReserva, idUsuario);
    }

    @Override
    public List<Reserva> buscarPorDni(String dni) {
        return reservaRepositorio.findByDniContaining(dni);
    }
}
