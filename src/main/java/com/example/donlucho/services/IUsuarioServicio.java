package com.example.donlucho.services;

import com.example.donlucho.model.Usuario;
import java.util.List;
import java.util.Optional;

public interface IUsuarioServicio {
    List<Usuario> listarUsuarios();
    Usuario guardarUsuario(Usuario usuario);
    Usuario buscarPorId(Integer id);
    void eliminarUsuario(Integer id);
    Optional<Usuario> buscarPorEmail(String email);
}
