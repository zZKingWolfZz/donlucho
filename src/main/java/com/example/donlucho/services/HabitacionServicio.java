package com.example.donlucho.services;

import com.example.donlucho.repository.HabitacionRepositorio;
import com.example.donlucho.model.Habitacion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class HabitacionServicio implements IHabitacionServicio {

    @Autowired
    private HabitacionRepositorio habitacionRepositorio;

    @Override
    public List<Habitacion> listarHabitaciones() {
        return habitacionRepositorio.findAll();
    }

    @Override
    public Habitacion guardarHabitacion(Habitacion habitacion) {
        return habitacionRepositorio.save(habitacion);
    }

    @Override
    public Habitacion buscarPorId(Integer id) {
        return habitacionRepositorio.findById(id).orElse(null);
    }

    @Override
    public void eliminarHabitacion(Integer id) {
        habitacionRepositorio.deleteById(id);
    }

    @Override
    public List<Habitacion> buscarPorSede(Integer idSede) {
        return habitacionRepositorio.findByIdSede(idSede);
    }

    @Override
    public List<Habitacion> buscarPorEstado(String estado) {
        return habitacionRepositorio.findByEstado(estado);
    }

    @Override
    public List<Habitacion> buscarPorSedeYEstadoOrdenadoPorNumeroAsc(Integer idSede, String estado) {
        return habitacionRepositorio.findByIdSedeAndEstadoOrderByNumeroHabitacionAsc(idSede, estado);
    }

    @Override
    public Optional<Habitacion> buscarPorIdYEstado(Integer idHabitacion, String estado) {
        return habitacionRepositorio.findByIdHabitacionAndEstado(idHabitacion, estado);
    }

    @Override
    public Optional<Habitacion> buscarPorIdYSedeYEstado(Integer idHabitacion, Integer idSede, String estado) {
        return habitacionRepositorio.findByIdHabitacionAndIdSedeAndEstado(idHabitacion, idSede, estado);
    }
}
