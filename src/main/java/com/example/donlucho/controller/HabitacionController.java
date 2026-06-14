package com.example.donlucho.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;

import com.example.donlucho.model.Habitacion;
import com.example.donlucho.model.Sede;
import com.example.donlucho.services.IHabitacionServicio;
import com.example.donlucho.services.ISedeServicio;

@Controller
public class HabitacionController {

    @Autowired
    private IHabitacionServicio habitacionServicio;

    @Autowired
    private ISedeServicio sedeServicio;

    // Directory where uploaded images are saved (relative to classpath static)
    private static final String UPLOAD_DIR = "src/main/resources/static/images/habitaciones/";

    // ----- LISTING -----

    @GetMapping("/habitaciones")
    public String habitaciones(@RequestParam(value = "id_sede", required = false) Integer idSede,
            @RequestParam(value = "sede", required = false) Integer sedeParam,
            HttpSession session, Model model) {
        Boolean isAdmin = (Boolean) session.getAttribute("isAdmin");
        Integer selectedSede = idSede != null ? idSede : sedeParam;

        List<Sede> sedes = sedeServicio.listarSedes();
        model.addAttribute("sedes", sedes);

        List<Habitacion> habitaciones;
        if (isAdmin != null && isAdmin) {
            if (selectedSede != null) {
                habitaciones = habitacionServicio.buscarPorSede(selectedSede);
            } else {
                habitaciones = habitacionServicio.listarHabitaciones();
            }
        } else {
            if (selectedSede != null) {
                habitaciones = habitacionServicio.buscarPorSedeYEstadoOrdenadoPorNumeroAsc(selectedSede, "disponible");
            } else {
                habitaciones = habitacionServicio.buscarPorEstado("disponible");
            }
        }

        for (Habitacion hab : habitaciones) {
            Sede s = sedeServicio.buscarPorId(hab.getIdSede());
            if (s != null) {
                hab.setNombreSede(s.getNombreSede());
            }
        }
        model.addAttribute("habitaciones", habitaciones);

        if (isAdmin != null && isAdmin) {
            model.addAttribute("id_sede_seleccionada", selectedSede);
            return "dasboard/habitaciones";
        } else {
            return "habitaciones/habitaciones";
        }
    }

    @PostMapping("/habitaciones")
    public String habitacionesPost(@RequestParam("action") String action,
            @RequestParam("id_habitacion") Integer idHabitacion,
            RedirectAttributes redirectAttributes) {
        if ("eliminar".equals(action)) {
            habitacionServicio.eliminarHabitacion(idHabitacion);
            redirectAttributes.addFlashAttribute("success_msg", "Habitación eliminada con éxito.");
        }
        return "redirect:/habitaciones";
    }

    // ----- AGREGAR -----

    @GetMapping({ "/agregar_habitacion", "/agregar_habitacion.php", "/habitaciones/agregar" })
    public String agregarHabitacionForm(Model model) {
        model.addAttribute("habitacion", new Habitacion());
        model.addAttribute("sedes", sedeServicio.listarSedes());
        return "dasboard/agregar_habitacion";
    }

    @PostMapping({ "/habitaciones/guardar" })
    public String guardarHabitacion(
            @ModelAttribute("habitacion") Habitacion hab,
            @RequestParam(value = "imagen_file", required = false) MultipartFile imagenFile,
            @RequestParam(value = "imagen_url_input", required = false) String imagenUrlInput,
            @RequestParam(value = "imagen_source", required = false, defaultValue = "url") String imagenSource,
            RedirectAttributes redirectAttributes) {

        // Handle image: file upload takes priority if provided and source is "file"
        String finalImagenUrl = resolveImagenUrl(imagenSource, imagenFile, imagenUrlInput, null);
        if (finalImagenUrl != null) {
            hab.setImagenUrl(finalImagenUrl);
        }

        if (hab.getCapacidadMaxima() == null) {
            hab.setCapacidadMaxima(2);
        }
        habitacionServicio.guardarHabitacion(hab);
        redirectAttributes.addFlashAttribute("success_msg", "Habitación guardada con éxito.");
        return "redirect:/habitaciones";
    }

    // ----- EDITAR -----

    @GetMapping({ "/editar_habitacion", "/editar_habitacion.php", "/habitaciones/editar/{id}" })
    public String editarHabitacionForm(@PathVariable(value = "id", required = false) Integer idPath,
            @RequestParam(value = "id", required = false) Integer idQuery,
            Model model) {
        Integer id = idPath != null ? idPath : idQuery;
        model.addAttribute("habitacion", habitacionServicio.buscarPorId(id));
        model.addAttribute("sedes", sedeServicio.listarSedes());
        return "dasboard/editar_habitacion";
    }

    @PostMapping({ "/editar_habitacion.php", "/editar_habitacion" })
    public String guardarEditarHabitacion(
            @ModelAttribute("habitacion") Habitacion hab,
            @RequestParam(value = "imagen_file", required = false) MultipartFile imagenFile,
            @RequestParam(value = "imagen_url_input", required = false) String imagenUrlInput,
            @RequestParam(value = "imagen_source", required = false, defaultValue = "url") String imagenSource,
            RedirectAttributes redirectAttributes) {

        if (hab.getIdHabitacion() != null) {
            Habitacion existing = habitacionServicio.buscarPorId(hab.getIdHabitacion());
            if (existing != null) {
                existing.setNumeroHabitacion(hab.getNumeroHabitacion());
                existing.setPiso(hab.getPiso());
                existing.setIdSede(hab.getIdSede());
                existing.setEstado(hab.getEstado());
                existing.setPrecioNoche(hab.getPrecioNoche());
                existing.setNombreTipo(hab.getNombreTipo());
                existing.setDescripcion(hab.getDescripcion());
                if (hab.getCapacidadMaxima() != null) {
                    existing.setCapacidadMaxima(hab.getCapacidadMaxima());
                } else if (existing.getCapacidadMaxima() == null) {
                    existing.setCapacidadMaxima(2);
                }

                // Resolve image from file or URL, keeping existing if neither provided
                String finalImagenUrl = resolveImagenUrl(imagenSource, imagenFile, imagenUrlInput, existing.getImagenUrl());
                existing.setImagenUrl(finalImagenUrl);

                habitacionServicio.guardarHabitacion(existing);
            }
        }
        redirectAttributes.addFlashAttribute("success_msg", "Habitación editada con éxito.");
        return "redirect:/habitaciones";
    }

    // ----- HELPER -----

    /**
     * Resolves the final imagen_url based on whether the admin chose to upload a
     * file or paste a URL.
     *
     * @param source      "file" | "url"
     * @param file        the uploaded MultipartFile (may be null or empty)
     * @param urlInput    the pasted URL string (may be null or blank)
     * @param existingUrl the current imagen_url in the DB (fallback)
     * @return the URL string to persist
     */
    private String resolveImagenUrl(String source, MultipartFile file, String urlInput, String existingUrl) {
        if ("file".equals(source) && file != null && !file.isEmpty()) {
            try {
                // Make sure upload directory exists
                Path uploadPath = Paths.get(UPLOAD_DIR);
                Files.createDirectories(uploadPath);

                // Build a unique filename to avoid collisions
                String originalFilename = file.getOriginalFilename();
                String ext = "";
                if (originalFilename != null && originalFilename.contains(".")) {
                    ext = originalFilename.substring(originalFilename.lastIndexOf("."));
                }
                String uniqueFilename = "hab_" + UUID.randomUUID().toString().substring(0, 8) + ext;

                Path targetPath = uploadPath.resolve(uniqueFilename);
                Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

                return "/images/habitaciones/" + uniqueFilename;
            } catch (IOException e) {
                System.err.println("Error al guardar imagen: " + e.getMessage());
                return existingUrl; // keep old on error
            }
        } else if ("url".equals(source) && urlInput != null && !urlInput.trim().isEmpty()) {
            return urlInput.trim();
        }
        // Nothing changed — keep the existing value
        return existingUrl;
    }
}
