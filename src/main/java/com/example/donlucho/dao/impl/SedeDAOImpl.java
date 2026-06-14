package com.example.donlucho.dao.impl;

import com.example.donlucho.dao.ISedeDAO;
import com.example.donlucho.model.Sede;
import com.example.donlucho.repository.SedeRepositorio;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public class SedeDAOImpl extends GenericDAOImpl<Sede, Integer, SedeRepositorio> implements ISedeDAO {

    public SedeDAOImpl(SedeRepositorio repository) {
        super(repository);
    }

    @Override
    public Optional<Sede> findByNombreSede(String nombreSede) {
        return repository.findByNombreSede(nombreSede);
    }

    @Override
    public List<Sede> findAllByOrderByNombreSedeAsc() {
        return repository.findAllByOrderByNombreSedeAsc();
    }
}
