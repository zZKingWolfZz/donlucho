package com.example.donlucho.dao;

import com.example.donlucho.model.Sede;
import java.util.List;
import java.util.Optional;

public interface ISedeDAO extends IGenericDAO<Sede, Integer> {
    Optional<Sede> findByNombreSede(String nombreSede);
    List<Sede> findAllByOrderByNombreSedeAsc();
}
