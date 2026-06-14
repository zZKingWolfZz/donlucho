package com.example.donlucho.services;

import com.example.donlucho.dao.IHabitacionDAO;
import com.example.donlucho.model.Habitacion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class HabitacionServicio implements IHabitacionServicio {

    @Autowired
    private IHabitacionDAO habitacionDAO;

    @Override
    public List<Habitacion> listarHabitaciones() {
        return habitacionDAO.findAll();
    }

    @Override
    public Habitacion guardarHabitacion(Habitacion habitacion) {
        return habitacionDAO.save(habitacion);
    }

    @Override
    public Habitacion buscarPorId(Integer id) {
        return habitacionDAO.findById(id).orElse(null);
    }

    @Override
    public void eliminarHabitacion(Integer id) {
        habitacionDAO.deleteById(id);
    }

    @Override
    public List<Habitacion> buscarPorSede(Integer idSede) {
        return habitacionDAO.findByIdSede(idSede);
    }

    @Override
    public List<Habitacion> buscarPorEstado(String estado) {
        return habitacionDAO.findByEstado(estado);
    }

    @Override
    public List<Habitacion> buscarPorSedeYEstadoOrdenadoPorNumeroAsc(Integer idSede, String estado) {
        return habitacionDAO.findByIdSedeAndEstadoOrderByNumeroHabitacionAsc(idSede, estado);
    }

    @Override
    public Optional<Habitacion> buscarPorIdYEstado(Integer idHabitacion, String estado) {
        return habitacionDAO.findByIdHabitacionAndEstado(idHabitacion, estado);
    }

    @Override
    public Optional<Habitacion> buscarPorIdYSedeYEstado(Integer idHabitacion, Integer idSede, String estado) {
        return habitacionDAO.findByIdHabitacionAndIdSedeAndEstado(idHabitacion, idSede, estado);
    }
}
