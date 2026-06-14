package com.example.donlucho.services;

import com.example.donlucho.dao.IUsuarioDAO;
import com.example.donlucho.model.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioServicio implements IUsuarioServicio {

    @Autowired
    private IUsuarioDAO usuarioDAO;

    @Override
    public List<Usuario> listarUsuarios() {
        return usuarioDAO.findAll();
    }

    @Override
    public Usuario guardarUsuario(Usuario usuario) {
        return usuarioDAO.save(usuario);
    }

    @Override
    public Usuario buscarPorId(Integer id) {
        return usuarioDAO.findById(id).orElse(null);
    }

    @Override
    public void eliminarUsuario(Integer id) {
        usuarioDAO.deleteById(id);
    }

    @Override
    public Optional<Usuario> buscarPorEmail(String email) {
        return usuarioDAO.findByEmail(email);
    }
}
