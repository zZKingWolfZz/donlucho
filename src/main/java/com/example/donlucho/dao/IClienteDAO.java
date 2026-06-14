package com.example.donlucho.dao;

import com.example.donlucho.model.Cliente;
import java.time.LocalDateTime;
import java.util.List;

public interface IClienteDAO extends IGenericDAO<Cliente, Integer> {
    List<Cliente> findByDni(String dni);
    List<Cliente> findByNombres(String nombres);
    List<Cliente> findByApellidos(String apellidos);
    List<Cliente> findByEmail(String email);
    List<Cliente> findByTelefono(String telefono);
    List<Cliente> findByFechaRegistroGreaterThan(LocalDateTime fechaRegistro);
}
