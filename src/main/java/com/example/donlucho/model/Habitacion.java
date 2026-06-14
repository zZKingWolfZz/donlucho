package com.example.donlucho.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Entity
@Table(name = "habitaciones")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Habitacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_habitacion")
    private Integer idHabitacion;

    @Column(name = "numero_habitacion", nullable = false, unique = true, length = 10)
    private String numeroHabitacion;

    @Column(name = "piso")
    private Integer piso;

    @Column(name = "id_sede")
    private Integer idSede;

    @Column(name = "estado", length = 20)
    private String estado;

    @Column(name = "imagen_url", length = 255)
    private String imagenUrl;

    @Column(name = "precio_noche", precision = 10, scale = 2)
    private BigDecimal precioNoche;

    @Column(name = "nombre_tipo", length = 50)
    private String nombreTipo;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "capacidad_maxima")
    private Integer capacidadMaxima;

    @Transient
    private String nombreSede;

    public String getNombre_sede() {
        return nombreSede;
    }

    public void setNombre_sede(String nombreSede) {
        this.nombreSede = nombreSede;
    }

    public String getDescTipo() {
        return descripcion;
    }

    public Integer getCapacidad() {
        return capacidadMaxima;
    }
}
