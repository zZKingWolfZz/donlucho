package com.example.donlucho.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.donlucho.model.Habitacion;
import com.example.donlucho.model.Reserva;
import com.example.donlucho.model.Sede;
import com.example.donlucho.model.Usuario;
import com.example.donlucho.services.IHabitacionServicio;
import com.example.donlucho.services.IReservaServicio;
import com.example.donlucho.services.ISedeServicio;
import com.example.donlucho.services.IUsuarioServicio;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.transaction.Transactional;

@Controller
public class ReservasController {

    @Autowired
    private ISedeServicio sedeServicio;

    @Autowired
    private IHabitacionServicio habitacionServicio;

    @Autowired
    private IReservaServicio reservaServicio;

    @Autowired
    private IUsuarioServicio usuarioServicio;

    @Value("${reniec.api.token:}")
    private String reniecApiToken;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @GetMapping("/reservar/reservas")
    public String mostrarFormularioReservas(Model model, @RequestParam(required = false) Integer habitacion) {
        List<Sede> sedes = sedeServicio.listarSedesOrdenadasPorNombre();
        model.addAttribute("sedes", sedes);
        
        if (habitacion != null) {
            Optional<Habitacion> habitacionOpt = habitacionServicio.buscarPorIdYEstado(habitacion, "disponible");
            if (habitacionOpt.isPresent()) {
                Habitacion hab = habitacionOpt.get();
                Sede s = sedeServicio.buscarPorId(hab.getIdSede());
                if (s != null) {
                    hab.setNombreSede(s.getNombreSede());
                    hab.setNombre_sede(s.getNombreSede());
                }
                model.addAttribute("preselectedRoom", hab);
            }
        }
        
        return "reservas/reservas";
    }

    @GetMapping("/api/consultar-dni")
    @ResponseBody
    public Map<String, Object> consultarDni(@RequestParam String dni) {
        Map<String, Object> response = new HashMap<>();
        if (dni == null || dni.trim().length() != 8) {
            response.put("success", false);
            response.put("message", "DNI no encontrado o formato inválido (debe ser de 8 dígitos)");
            return response;
        }

        try {
            java.net.http.HttpClient client = java.net.http.HttpClient.newHttpClient();
            java.net.http.HttpRequest req = java.net.http.HttpRequest.newBuilder()
                .uri(java.net.URI.create("https://api.decolecta.com/v1/reniec/dni?numero=" + dni))
                .header("Authorization", "Bearer " + reniecApiToken)
                .header("Accept", "application/json")
                .GET()
                .build();
            java.net.http.HttpResponse<String> resp = client.send(req, java.net.http.HttpResponse.BodyHandlers.ofString());
            
            System.out.println("Respuesta Decolecta: " + resp.body());
            
            JsonNode rootNode = objectMapper.readTree(resp.body());
            
            String nombres = "";
            String apellidoPaterno = "";
            String apellidoMaterno = "";
            
            if (rootNode.has("first_name")) {
                nombres = rootNode.get("first_name").asText("");
                apellidoPaterno = rootNode.has("first_last_name") ? rootNode.get("first_last_name").asText("") : "";
                apellidoMaterno = rootNode.has("second_last_name") ? rootNode.get("second_last_name").asText("") : "";
            } else if (rootNode.has("nombres")) {
                nombres = rootNode.get("nombres").asText("");
                apellidoPaterno = rootNode.has("apellidoPaterno") ? rootNode.get("apellidoPaterno").asText("") : 
                                   rootNode.has("apellido_paterno") ? rootNode.get("apellido_paterno").asText("") : "";
                apellidoMaterno = rootNode.has("apellidoMaterno") ? rootNode.get("apellidoMaterno").asText("") : 
                                   rootNode.has("apellido_materno") ? rootNode.get("apellido_materno").asText("") : "";
            }
            
            if (!nombres.trim().isEmpty()) {
                response.put("success", true);
                response.put("nombres", nombres);
                response.put("apellidoPaterno", apellidoPaterno);
                response.put("apellidoMaterno", apellidoMaterno);
                return response;
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        response.put("success", false);
        response.put("message", "No se encontraron datos para este DNI.");
        return response;
    }

    @GetMapping("/api/habitaciones")
    @ResponseBody
    public List<Map<String, Object>> getHabitaciones(@RequestParam String sede) {
        List<Map<String, Object>> result = new ArrayList<>();
        try {
            Optional<Sede> sedeOpt = sedeServicio.buscarPorNombre(sede);
            if (sedeOpt.isPresent()) {
                List<Habitacion> habitaciones = habitacionServicio.buscarPorSedeYEstadoOrdenadoPorNumeroAsc(
                    sedeOpt.get().getIdSede(), "disponible"
                );
                
                for (Habitacion h : habitaciones) {
                    Map<String, Object> room = new HashMap<>();
                    room.put("id_habitacion", h.getIdHabitacion());
                    room.put("numero_habitacion", h.getNumeroHabitacion());
                    room.put("imagen_url", h.getImagenUrl());
                    room.put("nombre_tipo", h.getNombreTipo() != null ? h.getNombreTipo() : "");
                    room.put("precio_noche", h.getPrecioNoche());
                    result.add(room);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @GetMapping("/reservar/mis_reservas")
    public String misReservas(Model model, @RequestParam(required = false) String success) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        
        Optional<Usuario> usuarioOpt = usuarioServicio.buscarPorEmail(email);
        if (usuarioOpt.isPresent()) {
            List<Reserva> reservas = reservaServicio.buscarPorUsuarioOrdenadoPorFechaDesc(usuarioOpt.get().getIdUsuario());
            for (Reserva res : reservas) {
                if (res.getIdHabitacion() != null) {
                    Optional<Habitacion> hOpt = Optional.ofNullable(habitacionServicio.buscarPorId(res.getIdHabitacion()));
                    hOpt.ifPresent(h -> res.setNumero_habitacion(h.getNumeroHabitacion()));
                }
                if (res.getIdSede() != null) {
                    Optional<Sede> sOpt = Optional.ofNullable(sedeServicio.buscarPorId(res.getIdSede()));
                    sOpt.ifPresent(s -> res.setNombre_sede(s.getNombreSede()));
                }
            }
            model.addAttribute("reservas", reservas);
        }
        
        if (success != null) {
            model.addAttribute("success", success);
        }
        
        return "reservas/mis_reservas";
    }

    @PostMapping("/api/cancelar-reserva")
    @ResponseBody
    public Map<String, Object> cancelarReserva(@RequestParam Integer id_reserva) {
        Map<String, Object> response = new HashMap<>();
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getName();
            Optional<Usuario> usuarioOpt = usuarioServicio.buscarPorEmail(email);
            
            if (usuarioOpt.isPresent()) {
                Optional<Reserva> reservaOpt = reservaServicio.buscarPorIdYUsuario(id_reserva, usuarioOpt.get().getIdUsuario());
                if (reservaOpt.isPresent()) {
                    Reserva reserva = reservaOpt.get();
                    if ("cancelada".equals(reserva.getEstado()) || "finalizada".equals(reserva.getEstado())) {
                        response.put("success", false);
                        response.put("message", "No se puede cancelar esta reserva.");
                    } else {
                        reserva.setEstado("cancelada");
                        reservaServicio.guardarReserva(reserva);
                        response.put("success", true);
                    }
                } else {
                    response.put("success", false);
                    response.put("message", "Reserva no encontrada.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "Error al cancelar la reserva.");
        }
        return response;
    }

    @PostMapping("/api/realizar-reserva")
    @ResponseBody
    @Transactional
    public Map<String, Object> realizarReserva(
            @RequestParam String dni,
            @RequestParam String nombres,
            @RequestParam String apellidos,
            @RequestParam String telefono,
            @RequestParam String pais,
            @RequestParam String sede,
            @RequestParam Integer habitacion,
            @RequestParam String fecha_entrada,
            @RequestParam String fecha_salida,
            @RequestParam Integer adultos,
            @RequestParam Integer ninos) {
        
        Map<String, Object> response = new HashMap<>();
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getName();
            Optional<Usuario> usuarioOpt = usuarioServicio.buscarPorEmail(email);
            
            if (!usuarioOpt.isPresent()) {
                response.put("success", false);
                response.put("message", "Usuario no encontrado.");
                return response;
            }
            
            Optional<Sede> sedeOpt = sedeServicio.buscarPorNombre(sede);
            if (!sedeOpt.isPresent()) {
                response.put("success", false);
                response.put("message", "Sede no válida.");
                return response;
            }
            
            Optional<Habitacion> habitacionOpt = habitacionServicio.buscarPorIdYSedeYEstado(
                habitacion, sedeOpt.get().getIdSede(), "disponible"
            );
            
            if (!habitacionOpt.isPresent()) {
                response.put("success", false);
                response.put("message", "Habitación no disponible.");
                return response;
            }
            
            Reserva reserva = new Reserva();
            reserva.setIdUsuario(usuarioOpt.get().getIdUsuario());
            reserva.setIdHabitacion(habitacion);
            reserva.setIdSede(sedeOpt.get().getIdSede());
            reserva.setFechaEntrada(java.time.LocalDate.parse(fecha_entrada));
            reserva.setFechaSalida(java.time.LocalDate.parse(fecha_salida));
            reserva.setCantidadAdultos(adultos);
            reserva.setCantidadNinos(ninos);
            reserva.setEstado("confirmada");
            reserva.setDni(dni);
            reserva.setNombres(nombres);
            reserva.setApellidos(apellidos);
            
            reservaServicio.guardarReserva(reserva);
            
            response.put("success", true);
            response.put("redirect", "/reservar/mis_reservas?success=Reserva realizada correctamente");
            
        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "Error al realizar la reserva: " + e.getMessage());
        }
        return response;
    }
}
