package com.example.donlucho.services;

import com.example.donlucho.model.Cliente;
import java.time.LocalDateTime;
import java.util.List;

public interface IClienteServicio {
    List<Cliente> listarClientes();

    Cliente guardarCliente(Cliente cliente);

    Cliente buscarPorId(Integer id);

    void eliminarCliente(Integer id);

    Cliente buscarPorDni(String dni);

    List<Cliente> buscarPorNombre(String nombre);

    List<Cliente> buscarPorApellido(String apellido);

    List<Cliente> buscarPorCorreo(String correo);

    List<Cliente> buscarPorTelefono(String telefono);

    List<Cliente> buscarPorFechaRegistro(LocalDateTime fechaRegistro);
}