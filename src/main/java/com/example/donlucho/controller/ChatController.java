package com.example.donlucho.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import java.util.*;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    @Value("${gemini.api.key:}")
    private String apiKey;

    @Value("${gemini.model:gemini-2.5-flash}")
    private String modelName;

    private final RestTemplate restTemplate = new RestTemplate();

    @PostMapping
    public ResponseEntity<Map<String, String>> chat(@RequestBody Map<String, String> request) {
        String message = request.get("message");
        Map<String, String> response = new HashMap<>();

        if (apiKey == null || apiKey.trim().isEmpty() || apiKey.equals("YOUR_API_KEY")) {
            // Fallback to local rule-based responses if no key is configured
            response.put("reply", getLocalResponse(message));
            return ResponseEntity.ok(response);
        }

        try {
            String url = "https://generativelanguage.googleapis.com/v1beta/models/" + modelName + ":generateContent?key=" + apiKey;

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // Construct Gemini request body
            Map<String, Object> body = new HashMap<>();
            
            // System instruction to guide the AI personality
            Map<String, Object> systemInstruction = new HashMap<>();
            Map<String, Object> instructionPart = new HashMap<>();
            instructionPart.put("text", "Eres el Agente IA del Hotel y Hostal Don Lucho en Huancayo, Perú. " +
                    "Tu objetivo es ayudar a los clientes a reservar habitaciones, darles información sobre las sedes, " +
                    "tarifas, servicios, desayunos y destinos de turismo local (como el Parque de la Identidad Wanka, " +
                    "Torre Torre, Huarihuilca). Responde de forma muy amable, cálida, profesional y atenta en español. " +
                    "Mantén las respuestas concisas (máximo 2 o 3 párrafos cortos) y amigables.");
            systemInstruction.put("parts", Collections.singletonList(instructionPart));
            body.put("systemInstruction", systemInstruction);

            // Contents list
            Map<String, Object> content = new HashMap<>();
            Map<String, Object> part = new HashMap<>();
            part.put("text", message);
            content.put("parts", Collections.singletonList(part));
            body.put("contents", Collections.singletonList(content));

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
            
            ResponseEntity<Map> apiResponse = restTemplate.postForEntity(url, entity, Map.class);
            
            if (apiResponse.getStatusCode() == HttpStatus.OK && apiResponse.getBody() != null) {
                List<Map> candidates = (List<Map>) apiResponse.getBody().get("candidates");
                if (candidates != null && !candidates.isEmpty()) {
                    Map contentMap = (Map) candidates.get(0).get("content");
                    if (contentMap != null) {
                        List<Map> parts = (List<Map>) contentMap.get("parts");
                        if (parts != null && !parts.isEmpty()) {
                            String reply = (String) parts.get(0).get("text");
                            response.put("reply", reply);
                            return ResponseEntity.ok(response);
                        }
                    }
                }
            }
            
            response.put("reply", "Lo siento, tuve un problema al conectarme con mi cerebro de IA. ¿Te puedo ayudar en algo más?");
        } catch (Exception e) {
            System.err.println("Gemini API Error: " + e.getMessage());
            response.put("reply", getLocalResponse(message)); // Safe fallback on API error
        }

        return ResponseEntity.ok(response);
    }

    private String getLocalResponse(String userMsg) {
        if (userMsg == null) return "Hola, ¿en qué te puedo colaborar hoy?";
        String msg = userMsg.toLowerCase().trim();
        
        if (msg.contains("hola") || msg.contains("buenos dias") || msg.contains("buenas tardes") || msg.contains("buenas noches") || msg.contains("hello") || msg.contains("hi")) {
            return "¡Hola! Es un placer saludararte. Soy el Agente IA de Don Lucho. Estoy aquí para resolver tus dudas y ayudarte con tu hospedaje en Huancayo. ¿En qué te puedo asistir hoy?";
        }
        if (msg.contains("quien eres") || msg.contains("que eres") || msg.contains("como te llamas") || msg.contains("ia") || msg.contains("inteligencia") || msg.contains("antigravity") || msg.contains("robot")) {
            return "Soy el Agente IA de Don Lucho. Mi objetivo es guiarte durante tu estancia en Huancayo. Por favor, configura tu clave API de Gemini en `application.properties` para activar mi cerebro dinámico de lenguaje.";
        }
        if (msg.contains("habitacion") || msg.contains("habitaciones") || msg.contains("cuarto") || msg.contains("cuartos") || msg.contains("cama") || msg.contains("camas") || msg.contains("precio") || msg.contains("tarifa") || msg.contains("costo") || msg.contains("precios") || msg.contains("tarifas")) {
            return "En Don Lucho contamos con habitaciones individuales, dobles y familiares. Todas incluyen baño privado, agua caliente 24h, TV por cable y WiFi rápido. Consulta disponibilidad y tarifas en la pestaña 'Reservar' o en 'Habitaciones'.";
        }
        if (msg.contains("reservar") || msg.contains("reserva") || msg.contains("reservas") || msg.contains("alojamiento") || msg.contains("hospedarme")) {
            return "Puedes reservar fácilmente desde la pestaña 'Reservar' del menú superior. Solo elige la sede, fechas, tipo de habitación y completa tus datos para asegurar tu estancia.";
        }
        if (msg.contains("sede") || msg.contains("sedes") || msg.contains("huancayo") || msg.contains("ubicacion") || msg.contains("donde") || msg.contains("direccion")) {
            return "Nuestra Sede Central está en el centro de Huancayo, Perú. Ubicación segura con fácil acceso a los principales restaurantes y atractivos turísticos de la zona.";
        }
        if (msg.contains("contacto") || msg.contains("telefono") || msg.contains("correo") || msg.contains("whatsapp") || msg.contains("celular")) {
            return "Comunícate con recepción al WhatsApp +51 987 654 321 o escríbenos a reservas@hoteldonlucho.com. ¡Estamos disponibles para atenderte las 24 horas!";
        }
        return "Entiendo tu consulta. Para brindarte detalles específicos o si tienes alguna solicitud especial, te sugerimos contactar directamente a nuestra recepción vía WhatsApp al +51 987 654 321.";
    }
}
