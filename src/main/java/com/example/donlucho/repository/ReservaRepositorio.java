package com.example.donlucho.repository;

import com.example.donlucho.model.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservaRepositorio extends JpaRepository<Reserva, Integer> {
    List<Reserva> findByIdUsuario(Integer idUsuario);
    List<Reserva> findByIdHabitacion(Integer idHabitacion);
    List<Reserva> findByIdUsuarioOrderByFechaReservaDesc(Integer idUsuario);
    Optional<Reserva> findByIdReservaAndIdUsuario(Integer idReserva, Integer idUsuario);
    List<Reserva> findByDniContaining(String dni);
}
