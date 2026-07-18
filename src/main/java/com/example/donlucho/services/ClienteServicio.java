package com.example.donlucho.services;

import com.example.donlucho.repository.ClienteRepositorio;
import com.example.donlucho.model.Cliente;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ClienteServicio implements IClienteServicio {

    @Autowired
    private ClienteRepositorio clienteRepositorio;

    @Override
    public List<Cliente> listarClientes() {
        return clienteRepositorio.findAll();
    }

    @Override
    public Cliente guardarCliente(Cliente cliente) {
        return clienteRepositorio.save(cliente);
    }

    @Override
    public Cliente buscarPorId(Integer id) {
        return clienteRepositorio.findById(id).orElse(null);
    }

    @Override
    public void eliminarCliente(Integer id) {
        clienteRepositorio.deleteById(id);
    }

    @Override
    public Cliente buscarPorDni(String dni) {
        List<Cliente> list = clienteRepositorio.findByDni(dni);
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    public List<Cliente> buscarPorNombre(String nombre) {
        return clienteRepositorio.findByNombresContainingIgnoreCase(nombre);
    }

    @Override
    public List<Cliente> buscarPorApellido(String apellido) {
        return clienteRepositorio.findByApellidosContainingIgnoreCase(apellido);
    }

    @Override
    public List<Cliente> buscarPorCorreo(String correo) {
        return clienteRepositorio.findByEmailContainingIgnoreCase(correo);
    }

    @Override
    public List<Cliente> buscarPorTelefono(String telefono) {
        return clienteRepositorio.findByTelefonoContainingIgnoreCase(telefono);
    }

    @Override
    public List<Cliente> buscarPorFechaRegistro(LocalDateTime fechaRegistro) {
        return clienteRepositorio.findByFechaRegistroGreaterThan(fechaRegistro);
    }
}
