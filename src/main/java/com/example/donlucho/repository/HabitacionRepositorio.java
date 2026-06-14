package com.example.donlucho.repository;

import com.example.donlucho.model.Habitacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface HabitacionRepositorio extends JpaRepository<Habitacion, Integer> {
	List<Habitacion> findByIdSede(Integer idSede);
	List<Habitacion> findByEstado(String estado);
	List<Habitacion> findByIdSedeAndEstadoOrderByNumeroHabitacionAsc(Integer idSede, String estado);
	Optional<Habitacion> findByIdHabitacionAndIdSedeAndEstado(Integer idHabitacion, Integer idSede, String estado);
	Optional<Habitacion> findByIdHabitacionAndEstado(Integer idHabitacion, String estado);
}
