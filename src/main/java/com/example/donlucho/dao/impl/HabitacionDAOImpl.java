package com.example.donlucho.dao.impl;

import com.example.donlucho.dao.IHabitacionDAO;
import com.example.donlucho.model.Habitacion;
import com.example.donlucho.repository.HabitacionRepositorio;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public class HabitacionDAOImpl extends GenericDAOImpl<Habitacion, Integer, HabitacionRepositorio> implements IHabitacionDAO {

    public HabitacionDAOImpl(HabitacionRepositorio repository) {
        super(repository);
    }

    @Override
    public List<Habitacion> findByIdSede(Integer idSede) {
        return repository.findByIdSede(idSede);
    }

    @Override
    public List<Habitacion> findByEstado(String estado) {
        return repository.findByEstado(estado);
    }

    @Override
    public List<Habitacion> findByIdSedeAndEstadoOrderByNumeroHabitacionAsc(Integer idSede, String estado) {
        return repository.findByIdSedeAndEstadoOrderByNumeroHabitacionAsc(idSede, estado);
    }

    @Override
    public Optional<Habitacion> findByIdHabitacionAndIdSedeAndEstado(Integer idHabitacion, Integer idSede, String estado) {
        return repository.findByIdHabitacionAndIdSedeAndEstado(idHabitacion, idSede, estado);
    }

    @Override
    public Optional<Habitacion> findByIdHabitacionAndEstado(Integer idHabitacion, String estado) {
        return repository.findByIdHabitacionAndEstado(idHabitacion, estado);
    }
}
