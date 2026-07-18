package com.example.donlucho.services;

import com.example.donlucho.repository.SedeRepositorio;
import com.example.donlucho.model.Sede;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class SedeServicio implements ISedeServicio {

    @Autowired
    private SedeRepositorio sedeRepositorio;

    @Override
    public List<Sede> listarSedes() {
        return sedeRepositorio.findAll();
    }

    @Override
    public Sede guardarSede(Sede Sede) {
        return sedeRepositorio.save(Sede);
    }

    @Override
    public Sede buscarPorId(Integer id) {
        return sedeRepositorio.findById(id).orElse(null);
    }

    @Override
    public void eliminarSede(Integer id) {
        sedeRepositorio.deleteById(id);
    }

    @Override
    public List<Sede> listarSedesOrdenadasPorNombre() {
        return sedeRepositorio.findAllByOrderByNombreSedeAsc();
    }

    @Override
    public Optional<Sede> buscarPorNombre(String nombreSede) {
        return sedeRepositorio.findByNombreSede(nombreSede);
    }
}
