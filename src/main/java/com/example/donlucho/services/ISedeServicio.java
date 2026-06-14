package com.example.donlucho.services;

import com.example.donlucho.model.Sede;
import java.util.List;
import java.util.Optional;

public interface ISedeServicio {
    List<Sede> listarSedes();
    Sede guardarSede(Sede sede);
    Sede buscarPorId(Integer id);
    void eliminarSede(Integer id);
    List<Sede> listarSedesOrdenadasPorNombre();
    Optional<Sede> buscarPorNombre(String nombreSede);
}
