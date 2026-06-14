package com.example.donlucho.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.donlucho.model.Habitacion;
import com.example.donlucho.model.Reserva;
import com.example.donlucho.model.Sede;
import com.example.donlucho.model.Usuario;
import com.example.donlucho.services.IHabitacionServicio;
import com.example.donlucho.services.IReservaServicio;
import com.example.donlucho.services.ISedeServicio;
import com.example.donlucho.services.IUsuarioServicio;

@Controller
public class ReservaController {

    @Autowired
    private IReservaServicio reservaServicio;

    @Autowired
    private IUsuarioServicio usuarioServicio;

    @Autowired
    private IHabitacionServicio habitacionServicio;

    @Autowired
    private ISedeServicio sedeServicio;

    @GetMapping("/reservas")
    public String reservas(@RequestParam(value = "search", required = false) String search, Model model) {
        List<Reserva> reservas;
        if (search != null && !search.trim().isEmpty()) {
            reservas = reservaServicio.buscarPorDni(search.trim());
        } else {
            reservas = reservaServicio.listarReservas();
        }
        
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
        model.addAttribute("reservas", reservas);
        model.addAttribute("total_registros", reservas.size());
        return "dasboard/reservas";
    }

    @PostMapping({"/reservas_actions.php", "/reservas_actions", "/admin/reservas_actions"})
    public String actionReserva(@RequestParam("action") String action,
                                @RequestParam("id_reserva") Integer idReserva,
                                RedirectAttributes redirectAttributes) {
        Reserva res = reservaServicio.buscarPorId(idReserva);
        if (res != null) {
            if ("confirmar".equals(action)) {
                res.setEstado("confirmada");
                reservaServicio.guardarReserva(res);
                redirectAttributes.addFlashAttribute("success", "Reserva confirmada.");
            } else if ("finalizar".equals(action)) {
                res.setEstado("finalizada");
                reservaServicio.guardarReserva(res);
                redirectAttributes.addFlashAttribute("success", "Reserva finalizada.");
            } else if ("cancelar".equals(action)) {
                res.setEstado("cancelada");
                reservaServicio.guardarReserva(res);
                redirectAttributes.addFlashAttribute("success", "Reserva cancelada.");
            } else if ("eliminar".equals(action)) {
                reservaServicio.eliminarReserva(idReserva);
                redirectAttributes.addFlashAttribute("success", "Reserva eliminada.");
            }
        }
        return "redirect:/reservas";
    }
}
