package com.example.donlucho.dao.impl;

import com.example.donlucho.dao.IUsuarioDAO;
import com.example.donlucho.model.Usuario;
import com.example.donlucho.repository.UsuarioRepositorio;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public class UsuarioDAOImpl extends GenericDAOImpl<Usuario, Integer, UsuarioRepositorio> implements IUsuarioDAO {

    public UsuarioDAOImpl(UsuarioRepositorio repository) {
        super(repository);
    }

    @Override
    public Optional<Usuario> findByEmail(String email) {
        return repository.findByEmail(email);
    }
}
