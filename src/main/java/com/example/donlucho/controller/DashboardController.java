package com.example.donlucho.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.donlucho.model.Cliente;
import com.example.donlucho.model.Habitacion;
import com.example.donlucho.model.Reserva;
import com.example.donlucho.model.Sede;
import com.example.donlucho.model.Usuario;
import com.example.donlucho.services.IClienteServicio;
import com.example.donlucho.services.IHabitacionServicio;
import com.example.donlucho.services.IReservaServicio;
import com.example.donlucho.services.ISedeServicio;
import com.example.donlucho.services.IUsuarioServicio;

@Controller
public class DashboardController {

    @Autowired
    private IClienteServicio clienteServicio;

    @Autowired
    private IHabitacionServicio habitacionServicio;

    @Autowired
    private IReservaServicio reservaServicio;

    @Autowired
    private ISedeServicio sedeServicio;

    @Autowired
    private IUsuarioServicio usuarioServicio;

    @GetMapping({"/dashboard", "/admin/dashboard"})
    public String dashboard(Model model) {
        List<Reserva> reservas = reservaServicio.listarReservas();
        for (Reserva res : reservas) {
            Usuario u = usuarioServicio.buscarPorId(res.getIdUsuario());
            if (u != null) {
                res.setEmail(u.getEmail());
                res.setNombreUsuario(u.getNombre_completo());
            }
            Habitacion h = habitacionServicio.buscarPorId(res.getIdHabitacion());
            if (h != null) {
                res.setNumero_habitacion(h.getNumeroHabitacion());
            }
            Sede s = sedeServicio.buscarPorId(res.getIdSede());
            if (s != null) {
                res.setNombre_sede(s.getNombreSede());
            }
        }
        model.addAttribute("ultimas_reservas", reservas);

        List<Cliente> clientes = clienteServicio.listarClientes();
        for (Cliente cli : clientes) {
            if (cli.getIdHabitacion() != null) {
                Habitacion h = habitacionServicio.buscarPorId(cli.getIdHabitacion());
                if (h != null) {
                    cli.setNumeroHabitacion(h.getNumeroHabitacion());
                }
            }
        }
        model.addAttribute("ultimos_clientes", clientes);

        return "dasboard/dashboard";
    }
}
