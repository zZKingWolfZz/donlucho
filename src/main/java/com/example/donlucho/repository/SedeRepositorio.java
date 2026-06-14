package com.example.donlucho.repository;

import com.example.donlucho.model.Sede;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SedeRepositorio extends JpaRepository<Sede, Integer> {
    Optional<Sede> findByNombreSede(String nombreSede);
    List<Sede> findAllByOrderByNombreSedeAsc();
}
