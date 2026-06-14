package com.example.donlucho.repository;

import com.example.donlucho.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ClienteRepositorio extends JpaRepository<Cliente, Integer> {
    List<Cliente> findByDni(String dni);

    List<Cliente> findByNombresContainingIgnoreCase(String nombres);

    List<Cliente> findByApellidosContainingIgnoreCase(String apellidos);

    List<Cliente> findByEmailContainingIgnoreCase(String email);

    List<Cliente> findByTelefonoContainingIgnoreCase(String telefono);

    List<Cliente> findByFechaRegistroGreaterThan(LocalDateTime fechaRegistro);
}
