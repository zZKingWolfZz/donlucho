package com.example.donlucho.services;

import com.example.donlucho.repository.RolRepositorio;
import com.example.donlucho.model.Rol;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class RolServicio implements IRolServicio {

    @Autowired
    private RolRepositorio rolRepositorio;

    @Override
    public Optional<Rol> buscarPorNombre(String nombreRol) {
        return rolRepositorio.findByNombreRol(nombreRol);
    }
}
