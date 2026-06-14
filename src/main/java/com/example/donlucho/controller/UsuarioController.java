package com.example.donlucho.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.donlucho.model.Rol;
import com.example.donlucho.model.Usuario;
import com.example.donlucho.services.IRolServicio;
import com.example.donlucho.services.IUsuarioServicio;

@Controller
public class UsuarioController {

    @Autowired
    private IUsuarioServicio usuarioServicio;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private IRolServicio rolServicio;

    @GetMapping("/miembros")
    public String usuarios(@RequestParam(value = "search", required = false) String search, Model model) {
        List<Usuario> users = usuarioServicio.listarUsuarios();
        List<Usuario> members = new ArrayList<>();
        for (Usuario u : users) {
            boolean isAdmin = u.getRoles().stream().anyMatch(r -> "administrador".equalsIgnoreCase(r.getNombreRol()))
                    || (u.getEmail() != null
                            && (u.getEmail().contains("admin") || u.getEmail().endsWith("@donlucho.com")));
            if (!isAdmin) {
                if (search == null || search.trim().isEmpty()
                        || (u.getNombre_completo() != null
                                && u.getNombre_completo().toLowerCase().contains(search.toLowerCase()))
                        || (u.getEmail() != null && u.getEmail().toLowerCase().contains(search.toLowerCase()))) {
                    members.add(u);
                }
            }
        }
        model.addAttribute("usuarios", members);
        return "dasboard/miembros";
    }

    @PostMapping("/miembros")
    public String usuariosPost(@RequestParam("action") String action,
            @RequestParam("id_usuario") Integer idUsuario,
            RedirectAttributes redirectAttributes) {
        if ("eliminar".equals(action)) {
            try {
                usuarioServicio.eliminarUsuario(idUsuario);
                redirectAttributes.addFlashAttribute("success", "Miembro eliminado con éxito.");
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("error", "No se puede eliminar el miembro porque está relacionado con otros registros (ej. reservas).");
            }
        }
        return "redirect:/miembros";
    }

    @PostMapping("/administradores")
    public String eliminarAdministrador(@RequestParam("id_usuario") Integer idUsuario,
            RedirectAttributes redirectAttributes) {
        try {
            usuarioServicio.eliminarUsuario(idUsuario);
            redirectAttributes.addFlashAttribute("success", "Administrador eliminado con éxito.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "No se puede eliminar el administrador porque está relacionado con otros registros (ej. reservas).");
        }
        return "redirect:/administradores";
    }

    @GetMapping("/administradores")
    public String administradores(Model model) {
        List<Usuario> users = usuarioServicio.listarUsuarios();
        List<Usuario> admins = new ArrayList<>();
        for (Usuario u : users) {
            boolean isAdmin = u.getRoles().stream().anyMatch(r -> "administrador".equalsIgnoreCase(r.getNombreRol()))
                    || (u.getEmail() != null
                            && (u.getEmail().contains("admin") || u.getEmail().endsWith("@donlucho.com")));
            if (isAdmin) {
                admins.add(u);
            }
        }
        model.addAttribute("administradores", admins);
        return "dasboard/administradores";
    }

    @GetMapping({ "/agregar_admin", "/agregar_admin.php" })
    public String agregarAdminForm(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "dasboard/agregar_admin";
    }

    @PostMapping({ "/agregar_admin", "/agregar_admin.php" })
    public String guardarAdmin(
            @RequestParam("nombre") String fullName,
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            @RequestParam("confirm_password") String confirmPassword,
            RedirectAttributes redirectAttributes) {

        if (!password.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "Las contraseñas no coinciden.");
            return "redirect:/agregar_admin";
        }

        Optional<Usuario> existing = usuarioServicio.buscarPorEmail(email);
        if (existing.isPresent()) {
            redirectAttributes.addFlashAttribute("error", "El correo ya está registrado.");
            return "redirect:/agregar_admin";
        }

        Usuario user = new Usuario();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setEstado(true);
        user.setDni("");

        String[] parts = fullName.split("\\s+");
        if (parts.length >= 3) {
            user.setNombre(parts[0]);
            user.setApellidoPaterno(parts[1]);
            user.setApellidoMaterno(parts[2]);
        } else if (parts.length == 2) {
            user.setNombre(parts[0]);
            user.setApellidoPaterno(parts[1]);
            user.setApellidoMaterno("");
        } else {
            user.setNombre(fullName);
            user.setApellidoPaterno("");
            user.setApellidoMaterno("");
        }

        Optional<Rol> adminRolOpt = rolServicio.buscarPorNombre("administrador");
        if (adminRolOpt.isPresent()) {
            user.getRoles().add(adminRolOpt.get());
        }

        usuarioServicio.guardarUsuario(user);
        redirectAttributes.addFlashAttribute("success", "Administrador agregado con éxito.");
        return "redirect:/administradores";
    }
}
