package com.example.donlucho.services;

import com.example.donlucho.model.Habitacion;
import java.util.List;
import java.util.Optional;

public interface IHabitacionServicio {
    List<Habitacion> listarHabitaciones();
    Habitacion guardarHabitacion(Habitacion habitacion);
    Habitacion buscarPorId(Integer id);
    void eliminarHabitacion(Integer id);
    List<Habitacion> buscarPorSede(Integer idSede);
    List<Habitacion> buscarPorEstado(String estado);
    List<Habitacion> buscarPorSedeYEstadoOrdenadoPorNumeroAsc(Integer idSede, String estado);
    Optional<Habitacion> buscarPorIdYEstado(Integer idHabitacion, String estado);
    Optional<Habitacion> buscarPorIdYSedeYEstado(Integer idHabitacion, Integer idSede, String estado);
}
