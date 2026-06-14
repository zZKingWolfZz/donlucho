package com.example.donlucho.services;

import com.example.donlucho.model.Rol;
import java.util.Optional;

public interface IRolServicio {
    Optional<Rol> buscarPorNombre(String nombreRol);
}
