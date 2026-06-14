package com.example.donlucho.dao;

import com.example.donlucho.model.Habitacion;
import java.util.List;
import java.util.Optional;

public interface IHabitacionDAO extends IGenericDAO<Habitacion, Integer> {
    List<Habitacion> findByIdSede(Integer idSede);
    List<Habitacion> findByEstado(String estado);
    List<Habitacion> findByIdSedeAndEstadoOrderByNumeroHabitacionAsc(Integer idSede, String estado);
    Optional<Habitacion> findByIdHabitacionAndIdSedeAndEstado(Integer idHabitacion, Integer idSede, String estado);
    Optional<Habitacion> findByIdHabitacionAndEstado(Integer idHabitacion, String estado);
}
