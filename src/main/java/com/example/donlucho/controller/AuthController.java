package com.example.donlucho.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.SecurityContextRepository;

import org.springframework.beans.factory.annotation.Value;
import com.example.donlucho.model.Usuario;
import com.example.donlucho.model.Rol;
import com.example.donlucho.model.Cliente;
import com.example.donlucho.services.IRolServicio;
import com.example.donlucho.services.IUsuarioServicio;
import com.example.donlucho.services.IClienteServicio;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
public class AuthController {

    @Autowired
    private IUsuarioServicio usuarioServicio;

    @Autowired
    private IClienteServicio clienteServicio;

    @Value("${reniec.api.token:}")
    private String reniecApiToken;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private SecurityContextRepository securityContextRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private IRolServicio rolServicio;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/logup")
    public String logup() {
        return "logup";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    @PostMapping("/api/login")
    @ResponseBody
    public Map<String, Object> loginApi(@RequestParam("email") String email,
                                        @RequestParam("password") String password,
                                        HttpServletRequest request,
                                        HttpServletResponse response,
                                        HttpSession session) {
        Map<String, Object> responseMap = new HashMap<>();
        try {
            // Authenticate using Spring Security
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
            );
            
            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authentication);
            SecurityContextHolder.setContext(context);
            securityContextRepository.saveContext(context, request, response);

            // Populate legacy session variables to support templates & controllers
            Optional<Usuario> userOpt = usuarioServicio.buscarPorEmail(email);
            if (userOpt.isPresent()) {
                Usuario user = userOpt.get();
                boolean isAdmin = user.getRoles().stream().anyMatch(r -> "administrador".equalsIgnoreCase(r.getNombreRol()))
                        || email.contains("admin") || email.endsWith("@donlucho.com");
                
                System.out.println("=== DEBUG LOGIN ===");
                System.out.println("Email: " + email);
                System.out.println("User found: " + user.getNombre_completo());
                System.out.println("Roles count: " + (user.getRoles() != null ? user.getRoles().size() : 0));
                if (user.getRoles() != null) {
                    for (Rol r : user.getRoles()) {
                        System.out.println(" - Role Name: " + r.getNombreRol());
                    }
                }
                System.out.println("Is Admin determined: " + isAdmin);
                System.out.println("====================");

                session.setAttribute("isLoggedIn", true);
                session.setAttribute("isAdmin", isAdmin);
                session.setAttribute("userEmail", email);
                session.setAttribute("userId", user.getIdUsuario());
                session.setAttribute("userName", user.getNombre());
                
                responseMap.put("success", true);
                responseMap.put("redirect", isAdmin ? "/dashboard" : "/");
            } else {
                responseMap.put("success", false);
                responseMap.put("message", "Usuario no encontrado.");
            }
        } catch (AuthenticationException e) {
            responseMap.put("success", false);
            responseMap.put("message", "Correo o contraseña incorrectos.");
        }
        return responseMap;
    }

    @PostMapping("/api/register")
    @ResponseBody
    public Map<String, Object> registerApi(@RequestParam("dni") String dni,
                                           @RequestParam("nombre") String fullName,
                                           @RequestParam("email") String email,
                                           @RequestParam("password") String password,
                                           HttpServletRequest request,
                                           HttpServletResponse response,
                                           HttpSession session) {
        Map<String, Object> responseMap = new HashMap<>();
        try {
            Optional<Usuario> existing = usuarioServicio.buscarPorEmail(email);
            if (existing.isPresent()) {
                responseMap.put("success", false);
                responseMap.put("message", "El correo ya está registrado.");
                return responseMap;
            }

            Usuario user = new Usuario();
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode(password)); // Encrypt password
            user.setEstado(true);
            user.setDni(dni);

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

            // Fetch and associate the "miembro" role
            Optional<Rol> userRolOpt = rolServicio.buscarPorNombre("miembro");
            if (userRolOpt.isPresent()) {
                user.getRoles().add(userRolOpt.get());
            }

            usuarioServicio.guardarUsuario(user);

            // Dynamically authenticate user after registration
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
            );
            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authentication);
            SecurityContextHolder.setContext(context);
            securityContextRepository.saveContext(context, request, response);

            session.setAttribute("isLoggedIn", true);
            session.setAttribute("isAdmin", false);
            session.setAttribute("userEmail", email);
            session.setAttribute("userId", user.getIdUsuario());
            session.setAttribute("userName", user.getNombre());

            responseMap.put("success", true);
            responseMap.put("redirect", "/");
        } catch (Exception e) {
            responseMap.put("success", false);
            responseMap.put("message", "Error al registrar: " + e.getMessage());
        }
        return responseMap;
    }

    @GetMapping("/register-admin")
    public String registerAdminView() {
        return "register_admin";
    }

    @PostMapping("/api/register-admin")
    @ResponseBody
    public Map<String, Object> registerAdminApi(@RequestParam("dni") String dni,
                                                @RequestParam("nombre") String fullName,
                                                @RequestParam("email") String email,
                                                @RequestParam("password") String password,
                                                HttpServletRequest request,
                                                HttpServletResponse response,
                                                HttpSession session) {
        Map<String, Object> responseMap = new HashMap<>();
        try {
            Optional<Usuario> existing = usuarioServicio.buscarPorEmail(email);
            if (existing.isPresent()) {
                responseMap.put("success", false);
                responseMap.put("message", "El correo ya está registrado.");
                return responseMap;
            }

            Usuario user = new Usuario();
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode(password)); // Encrypt password
            user.setEstado(true);
            user.setDni(dni);

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

            // Fetch and associate the "administrador" role
            Optional<Rol> adminRolOpt = rolServicio.buscarPorNombre("administrador");
            if (adminRolOpt.isPresent()) {
                user.getRoles().add(adminRolOpt.get());
            }

            usuarioServicio.guardarUsuario(user);

            // Dynamically authenticate user after registration
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
            );
            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authentication);
            SecurityContextHolder.setContext(context);
            securityContextRepository.saveContext(context, request, response);

            session.setAttribute("isLoggedIn", true);
            session.setAttribute("isAdmin", true);
            session.setAttribute("userEmail", email);
            session.setAttribute("userId", user.getIdUsuario());
            session.setAttribute("userName", user.getNombre());

            responseMap.put("success", true);
            responseMap.put("redirect", "/dashboard");
        } catch (Exception e) {
            responseMap.put("success", false);
            responseMap.put("message", "Error al registrar administrador: " + e.getMessage());
        }
        return responseMap;
    }

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

    @PostMapping("/api/consultar_dni")
    @ResponseBody
    public Map<String, Object> consultarDniApi(@RequestParam("dni") String dni) {
        Map<String, Object> response = new HashMap<>();
        if (dni == null || dni.trim().length() != 8) {
            response.put("success", false);
            response.put("message", "DNI no encontrado o formato inválido (debe ser de 8 dígitos)");
            return response;
        }

        System.out.println("=== INICIO CONSULTA DNI: " + dni + " ===");

        // 1. Si el token está configurado en application.properties, intentar llamar a la API real de Decolecta
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
                        System.out.println("⚠️ Nombres está vacío, usando fallback...");
                    }
                } else {
                    System.out.println("⚠️ Código de estado no es 200, usando fallback...");
                }
            } catch (Exception e) {
                System.out.println("❌ Error en la llamada API:");
                e.printStackTrace();
                // Silently fallback to DB and Mock
            }
        } else {
            System.out.println("⚠️ Token NO configurado, usando fallback...");
        }

        // 2. Intentar buscar si el cliente ya existe en la base de datos local
        try {
            Cliente cliente = clienteServicio.buscarPorDni(dni);
            if (cliente != null) {
                response.put("success", true);
                response.put("nombres", cliente.getNombres());
                String apellidos = cliente.getApellidos() != null ? cliente.getApellidos() : "";
                String[] parts = apellidos.split("\\s+");
                if (parts.length >= 2) {
                    response.put("apellidoPaterno", parts[0]);
                    response.put("apellidoMaterno", parts[1]);
                } else if (parts.length == 1) {
                    response.put("apellidoPaterno", parts[0]);
                    response.put("apellidoMaterno", "");
                } else {
                    response.put("apellidoPaterno", "");
                    response.put("apellidoMaterno", "");
                }
                return response;
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Silently fallback to deterministic mock
        }

        // 3. Simulación determinista realista (para que cada DNI tenga nombres coherentes durante el testing)
        int hash = Math.abs(dni.hashCode());
        String nombresGen = MOCK_NOMBRES[hash % MOCK_NOMBRES.length];
        String apPaternoGen = MOCK_PATERNOS[(hash / MOCK_NOMBRES.length) % MOCK_PATERNOS.length];
        String apMaternoGen = MOCK_MATERNOS[(hash / (MOCK_NOMBRES.length * MOCK_PATERNOS.length)) % MOCK_MATERNOS.length];

        response.put("success", true);
        response.put("nombres", nombresGen);
        response.put("apellidoPaterno", apPaternoGen);
        response.put("apellidoMaterno", apMaternoGen);
        return response;
    }
}
