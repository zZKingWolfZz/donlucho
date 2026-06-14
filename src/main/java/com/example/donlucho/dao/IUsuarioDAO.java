package com.example.donlucho.dao;

import com.example.donlucho.model.Usuario;
import java.util.Optional;

public interface IUsuarioDAO extends IGenericDAO<Usuario, Integer> {
    Optional<Usuario> findByEmail(String email);
}
