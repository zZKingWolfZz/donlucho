package com.example.donlucho.dao.impl;

import com.example.donlucho.dao.IClienteDAO;
import com.example.donlucho.model.Cliente;
import com.example.donlucho.repository.ClienteRepositorio;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public class ClienteDAOImpl extends GenericDAOImpl<Cliente, Integer, ClienteRepositorio> implements IClienteDAO {

    public ClienteDAOImpl(ClienteRepositorio repository) {
        super(repository);
    }

    @Override
    public List<Cliente> findByDni(String dni) {
        return repository.findByDni(dni);
    }

    @Override
    public List<Cliente> findByNombres(String nombres) {
        return repository.findByNombresContainingIgnoreCase(nombres);
    }

    @Override
    public List<Cliente> findByApellidos(String apellidos) {
        return repository.findByApellidosContainingIgnoreCase(apellidos);
    }

    @Override
    public List<Cliente> findByEmail(String email) {
        return repository.findByEmailContainingIgnoreCase(email);
    }

    @Override
    public List<Cliente> findByTelefono(String telefono) {
        return repository.findByTelefonoContainingIgnoreCase(telefono);
    }

    @Override
    public List<Cliente> findByFechaRegistroGreaterThan(LocalDateTime fechaRegistro) {
        return repository.findByFechaRegistroGreaterThan(fechaRegistro);
    }
}
