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
import com.example.donlucho.services.IClienteServicio;
import com.example.donlucho.model.Cliente;
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

    @Autowired
    private IClienteServicio clienteServicio;

    @Value("${reniec.api.token:}")
    private String reniecApiToken;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String[] MOCK_NOMBRES = {
        "Alejandro", "María", "Carlos", "José", "Luis", "Ana", "Jorge", "Sofía", "Miguel", "Andrea",
        "Daniel", "Lucía", "Fernando", "Elena", "Roberto", "Rosa", "David", "Gabriela", "Hugo", "Carmen",
        "Mario", "Patricia", "Víctor", "Isabel", "Javier", "Teresa", "Ricardo", "Silvia", "Manuel", "Juana"
    };
    private static final String[] MOCK_PATERNOS = {
        "Quispe", "Flores", "Sánchez", "García", "Rodríguez", "Rojas", "Huamán", "Mamani", "López", "Díaz",
        "Vásquez", "Ramos", "Torres", "Mendoza", "Espinoza", "Chávez", "Álvarez", "Castillo", "Gutiérrez", "Castro"
    };
    private static final String[] MOCK_MATERNOS = {
        "Salazar", "Rivera", "León", "Villanueva", "Fernández", "Pérez", "Guerrero", "Cárdenas", "Delgado", "Vega",
        "Romero", "Carrasco", "Soto", "Aguilar", "Campos", "Vargas", "Cabrera", "Miranda", "Muñoz", "Silva"
    };

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

        System.out.println("=== INICIO CONSULTA DNI (ReservasController): " + dni + " ===");

        // 1. Si el token está configurado, intentar llamar a la API real de Decolecta
        if (reniecApiToken != null && !reniecApiToken.trim().isEmpty()) {
            System.out.println("Token configurado, intentando API real de Decolecta...");
            try {
                java.net.http.HttpClient client = java.net.http.HttpClient.newHttpClient();
                java.net.http.HttpRequest req = java.net.http.HttpRequest.newBuilder()
                    .uri(java.net.URI.create("https://api.decolecta.com/v1/reniec/dni?numero=" + dni))
                    .header("Authorization", "Bearer " + reniecApiToken)
                    .header("Accept", "application/json")
                    .GET()
                    .build();
                System.out.println("Llamando a URL: " + req.uri());
                java.net.http.HttpResponse<String> resp = client.send(req, java.net.http.HttpResponse.BodyHandlers.ofString());
                System.out.println("Código de estado: " + resp.statusCode());
                System.out.println("Respuesta completa: " + resp.body());

                if (resp.statusCode() == 200) {
                    String body = resp.body();
                    JsonNode rootNode = objectMapper.readTree(body);

                    String nombres = "";
                    String apPaterno = "";
                    String apMaterno = "";

                    if (rootNode.has("first_name")) {
                        nombres = rootNode.get("first_name").asText("");
                        apPaterno = rootNode.has("first_last_name") ? rootNode.get("first_last_name").asText("") : "";
                        apMaterno = rootNode.has("second_last_name") ? rootNode.get("second_last_name").asText("") : "";
                    } else if (rootNode.has("nombres")) {
                        nombres = rootNode.get("nombres").asText("");
                        apPaterno = rootNode.has("apellidoPaterno") ? rootNode.get("apellidoPaterno").asText("") :
                                    rootNode.has("apellido_paterno") ? rootNode.get("apellido_paterno").asText("") : "";
                        apMaterno = rootNode.has("apellidoMaterno") ? rootNode.get("apellidoMaterno").asText("") :
                                    rootNode.has("apellido_materno") ? rootNode.get("apellido_materno").asText("") : "";
                    }

                    System.out.println("Nombres extraídos: " + nombres);
                    System.out.println("Ap Paterno: " + apPaterno);
                    System.out.println("Ap Materno: " + apMaterno);

                    if (!nombres.trim().isEmpty()) {
                        System.out.println("✅ Usando datos de API real (Decolecta)!");
                        response.put("success", true);
                        response.put("nombres", nombres);
                        response.put("apellidoPaterno", apPaterno);
                        response.put("apellidoMaterno", apMaterno);
                        return response;
                    } else {
                        System.out.println("⚠️ Nombres vacío, usando fallback...");
                    }
                } else {
                    System.out.println("⚠️ Código de estado no es 200, usando fallback...");
                }
            } catch (Exception e) {
                System.out.println("❌ Error en la llamada API, usando fallback:");
                e.printStackTrace();
            }
        } else {
            System.out.println("⚠️ Token NO configurado, usando fallback...");
        }

        // 2. Buscar si el cliente ya existe en la base de datos local
        try {
            Cliente cliente = clienteServicio.buscarPorDni(dni);
            if (cliente != null) {
                String apellidos = cliente.getApellidos() != null ? cliente.getApellidos() : "";
                String[] parts = apellidos.split("\\s+");
                response.put("success", true);
                response.put("nombres", cliente.getNombres());
                response.put("apellidoPaterno", parts.length >= 1 ? parts[0] : "");
                response.put("apellidoMaterno", parts.length >= 2 ? parts[1] : "");
                System.out.println("✅ Usando datos de DB local para DNI: " + dni);
                return response;
            }
        } catch (Exception e) {
            System.out.println("❌ Error consultando DB local, usando mock...");
            e.printStackTrace();
        }

        // 3. Fallback: simulación determinista (coherente por DNI para el testing)
        int hash = Math.abs(dni.hashCode());
        String nombresGen = MOCK_NOMBRES[hash % MOCK_NOMBRES.length];
        String apPaternoGen = MOCK_PATERNOS[(hash / MOCK_NOMBRES.length) % MOCK_PATERNOS.length];
        String apMaternoGen = MOCK_MATERNOS[(hash / (MOCK_NOMBRES.length * MOCK_PATERNOS.length)) % MOCK_MATERNOS.length];

        System.out.println("✅ Usando datos de mock determinista para DNI: " + dni);
        response.put("success", true);
        response.put("nombres", nombresGen);
        response.put("apellidoPaterno", apPaternoGen);
        response.put("apellidoMaterno", apMaternoGen);
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
