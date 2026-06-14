package com.example.donlucho.dao.impl;

import com.example.donlucho.dao.IRolDAO;
import com.example.donlucho.model.Rol;
import com.example.donlucho.repository.RolRepositorio;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public class RolDAOImpl extends GenericDAOImpl<Rol, Integer, RolRepositorio> implements IRolDAO {

    public RolDAOImpl(RolRepositorio repository) {
        super(repository);
    }

    @Override
    public Optional<Rol> findByNombreRol(String nombreRol) {
        return repository.findByNombreRol(nombreRol);
    }
}
