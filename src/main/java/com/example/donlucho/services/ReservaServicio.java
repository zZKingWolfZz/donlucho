package com.example.donlucho.services;

import com.example.donlucho.dao.IReservaDAO;
import com.example.donlucho.model.Reserva;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ReservaServicio implements IReservaServicio {

    @Autowired
    private IReservaDAO reservaDAO;

    @Override
    public List<Reserva> listarReservas() {
        return reservaDAO.findAll();
    }

    @Override
    public Reserva guardarReserva(Reserva reserva) {
        return reservaDAO.save(reserva);
    }

    @Override
    public Reserva buscarPorId(Integer id) {
        return reservaDAO.findById(id).orElse(null);
    }

    @Override
    public void eliminarReserva(Integer id) {
        reservaDAO.deleteById(id);
    }

    @Override
    public List<Reserva> buscarPorUsuario(Integer idUsuario) {
        return reservaDAO.findByIdUsuario(idUsuario);
    }

    @Override
    public List<Reserva> buscarPorHabitacion(Integer idHabitacion) {
        return reservaDAO.findByIdHabitacion(idHabitacion);
    }

    @Override
    public List<Reserva> buscarPorUsuarioOrdenadoPorFechaDesc(Integer idUsuario) {
        return reservaDAO.findByIdUsuarioOrderByFechaReservaDesc(idUsuario);
    }

    @Override
    public Optional<Reserva> buscarPorIdYUsuario(Integer idReserva, Integer idUsuario) {
        return reservaDAO.findByIdReservaAndIdUsuario(idReserva, idUsuario);
    }

    @Override
    public List<Reserva> buscarPorDni(String dni) {
        return reservaDAO.findByDniContaining(dni);
    }
}
