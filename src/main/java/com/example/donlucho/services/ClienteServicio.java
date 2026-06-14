package com.example.donlucho.services;

import com.example.donlucho.dao.IClienteDAO;
import com.example.donlucho.model.Cliente;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ClienteServicio implements IClienteServicio {

    @Autowired
    private IClienteDAO clienteDAO;

    @Override
    public List<Cliente> listarClientes() {
        return clienteDAO.findAll();
    }

    @Override
    public Cliente guardarCliente(Cliente cliente) {
        return clienteDAO.save(cliente);
    }

    @Override
    public Cliente buscarPorId(Integer id) {
        return clienteDAO.findById(id).orElse(null);
    }

    @Override
    public void eliminarCliente(Integer id) {
        clienteDAO.deleteById(id);
    }

    @Override
    public Cliente buscarPorDni(String dni) {
        List<Cliente> list = clienteDAO.findByDni(dni);
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    public List<Cliente> buscarPorNombre(String nombre) {
        return clienteDAO.findByNombres(nombre);
    }

    @Override
    public List<Cliente> buscarPorApellido(String apellido) {
        return clienteDAO.findByApellidos(apellido);
    }

    @Override
    public List<Cliente> buscarPorCorreo(String correo) {
        return clienteDAO.findByEmail(correo);
    }

    @Override
    public List<Cliente> buscarPorTelefono(String telefono) {
        return clienteDAO.findByTelefono(telefono);
    }

    @Override
    public List<Cliente> buscarPorFechaRegistro(LocalDateTime fechaRegistro) {
        return clienteDAO.findByFechaRegistroGreaterThan(fechaRegistro);
    }
}
