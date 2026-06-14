package com.example.donlucho.services;

import com.example.donlucho.dao.ISedeDAO;
import com.example.donlucho.model.Sede;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class SedeServicio implements ISedeServicio {

    @Autowired
    private ISedeDAO sedeDAO;

    @Override
    public List<Sede> listarSedes() {
        return sedeDAO.findAll();
    }

    @Override
    public Sede guardarSede(Sede Sede) {
        return sedeDAO.save(Sede);
    }

    @Override
    public Sede buscarPorId(Integer id) {
        return sedeDAO.findById(id).orElse(null);
    }

    @Override
    public void eliminarSede(Integer id) {
        sedeDAO.deleteById(id);
    }

    @Override
    public List<Sede> listarSedesOrdenadasPorNombre() {
        return sedeDAO.findAllByOrderByNombreSedeAsc();
    }

    @Override
    public Optional<Sede> buscarPorNombre(String nombreSede) {
        return sedeDAO.findByNombreSede(nombreSede);
    }
}
