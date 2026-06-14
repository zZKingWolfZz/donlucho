package com.example.donlucho.dao;

import com.example.donlucho.model.Rol;
import java.util.Optional;

public interface IRolDAO extends IGenericDAO<Rol, Integer> {
    Optional<Rol> findByNombreRol(String nombreRol);
}
