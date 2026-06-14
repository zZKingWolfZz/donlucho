package com.example.donlucho.dao.impl;

import com.example.donlucho.dao.IReservaDAO;
import com.example.donlucho.model.Reserva;
import com.example.donlucho.repository.ReservaRepositorio;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public class ReservaDAOImpl extends GenericDAOImpl<Reserva, Integer, ReservaRepositorio> implements IReservaDAO {

    public ReservaDAOImpl(ReservaRepositorio repository) {
        super(repository);
    }

    @Override
    public List<Reserva> findByIdUsuario(Integer idUsuario) {
        return repository.findByIdUsuario(idUsuario);
    }

    @Override
    public List<Reserva> findByIdHabitacion(Integer idHabitacion) {
        return repository.findByIdHabitacion(idHabitacion);
    }

    @Override
    public List<Reserva> findByIdUsuarioOrderByFechaReservaDesc(Integer idUsuario) {
        return repository.findByIdUsuarioOrderByFechaReservaDesc(idUsuario);
    }

    @Override
    public Optional<Reserva> findByIdReservaAndIdUsuario(Integer idReserva, Integer idUsuario) {
        return repository.findByIdReservaAndIdUsuario(idReserva, idUsuario);
    }

    @Override
    public List<Reserva> findByDniContaining(String dni) {
        return repository.findByDniContaining(dni);
    }
}
