package com.example.donlucho.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.donlucho.model.Cliente;
import com.example.donlucho.model.Habitacion;
import com.example.donlucho.model.Sede;
import com.example.donlucho.services.IClienteServicio;
import com.example.donlucho.services.IHabitacionServicio;
import com.example.donlucho.services.ISedeServicio;

@Controller
public class ClienteController {

    @Autowired
    private IClienteServicio clienteServicio;

    @Autowired
    private IHabitacionServicio habitacionServicio;

    @Autowired
    private ISedeServicio sedeServicio;

    @GetMapping("/clientes")
    public String clientes(@RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "palabraClave", required = false) String palabraClave,
            Model model) {
        String term = search != null ? search : palabraClave;
        List<Cliente> list;
        if (term != null && !term.trim().isEmpty()) {
            list = clienteServicio.buscarPorNombre(term);
            if (list.isEmpty()) {
                list = clienteServicio.buscarPorApellido(term);
            }
        } else {
            list = clienteServicio.listarClientes();
        }

        for (Cliente cli : list) {
            if (cli.getIdHabitacion() != null) {
                Habitacion h = habitacionServicio.buscarPorId(cli.getIdHabitacion());
                if (h != null) {
                    cli.setNumeroHabitacion(h.getNumeroHabitacion());
                }
            }
        }
        model.addAttribute("clientes", list);
        model.addAttribute("cliente", new Cliente());
        return "dasboard/clientes";
    }

    @PostMapping("/clientes")
    public String clientesPost(@RequestParam("action") String action,
            @RequestParam("id_cliente") Integer idCliente,
            RedirectAttributes redirectAttributes) {
        if ("eliminar".equals(action)) {
            clienteServicio.eliminarCliente(idCliente);
            redirectAttributes.addFlashAttribute("success", "Cliente eliminado con éxito.");
        }
        return "redirect:/clientes";
    }

    @GetMapping({ "/clientes/agregar", "/agregar_cliente.php", "/agregar_cliente" })
    public String agregarClienteForm(Model model) {
        model.addAttribute("cliente", new Cliente());
        List<Habitacion> habitaciones = habitacionServicio.listarHabitaciones();
        for (Habitacion hab : habitaciones) {
            Sede s = sedeServicio.buscarPorId(hab.getIdSede());
            if (s != null) {
                hab.setNombreSede(s.getNombreSede());
            }
        }
        model.addAttribute("habitaciones", habitaciones);
        return "dasboard/agregar_cliente";
    }

    @PostMapping({ "/clientes/guardar", "/agregar_cliente.php", "/agregar_cliente" })
    public String guardarCliente(
            @ModelAttribute("cliente") Cliente cliente,
            RedirectAttributes redirectAttributes) {

        if (cliente.getFechaRegistro() == null) {
            cliente.setFechaRegistro(LocalDateTime.now());
        }
        clienteServicio.guardarCliente(cliente);
        redirectAttributes.addFlashAttribute("success", "Cliente guardado con éxito.");
        return "redirect:/clientes";
    }

    @GetMapping({ "/clientes/editar/{id}" })
    public String editarClienteForm(@PathVariable(value = "id", required = false) Integer idPath,
            @RequestParam(value = "id", required = false) Integer idQuery,
            Model model) {
        Integer id = idPath != null ? idPath : idQuery;
        model.addAttribute("cliente", clienteServicio.buscarPorId(id));
        List<Habitacion> habitaciones = habitacionServicio.listarHabitaciones();
        for (Habitacion hab : habitaciones) {
            Sede s = sedeServicio.buscarPorId(hab.getIdSede());
            if (s != null) {
                hab.setNombreSede(s.getNombreSede());
            }
        }
        model.addAttribute("habitaciones", habitaciones);
        return "dasboard/editar_cliente";
    }

    @PostMapping({ "/clientes/editar/{id}" })
    public String guardarEditarCliente(
            @ModelAttribute("cliente") Cliente cliente,
            RedirectAttributes redirectAttributes) {

        if (cliente.getIdCliente() != null) {
            Cliente existing = clienteServicio.buscarPorId(cliente.getIdCliente());
            if (existing != null) {
                existing.setDni(cliente.getDni());
                existing.setNombres(cliente.getNombres());
                existing.setApellidos(cliente.getApellidos());
                existing.setTelefono(cliente.getTelefono());
                existing.setEmail(cliente.getEmail());
                existing.setIdHabitacion(cliente.getIdHabitacion());
                clienteServicio.guardarCliente(existing);
            }
        }
        redirectAttributes.addFlashAttribute("success", "Cliente editado con éxito.");
        return "redirect:/clientes";
    }
}
