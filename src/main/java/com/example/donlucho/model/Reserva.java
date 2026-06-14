package com.example.donlucho.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "reservas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_reserva")
    private Integer idReserva;

    @Column(name = "id_usuario")
    private Integer idUsuario;

    @Column(name = "id_habitacion")
    private Integer idHabitacion;

    @Column(name = "id_sede")
    private Integer idSede;

    @Column(name = "fecha_entrada", nullable = false)
    private LocalDate fechaEntrada;

    @Column(name = "fecha_salida", nullable = false)
    private LocalDate fechaSalida;

    @Column(name = "cantidad_adultos", nullable = false)
    private Integer cantidadAdultos;

    @Column(name = "cantidad_ninos")
    private Integer cantidadNinos;

    @Column(name = "estado", length = 20)
    private String estado;

    @Column(name = "dni", length = 8)
    private String dni;

    @Column(name = "nombres")
    private String nombres;

    @Column(name = "apellidos")
    private String apellidos;

    @Column(name = "fecha_reserva", insertable = false, updatable = false)
    private LocalDateTime fechaReserva;

    @Transient
    private String email;

    @Transient
    private String numeroHabitacion;

    @Transient
    private String nombreSede;

    @Transient
    private String nombreUsuario;

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public Integer getId_reserva() {
        return idReserva;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNumero_habitacion() {
        return numeroHabitacion;
    }

    public void setNumero_habitacion(String numeroHabitacion) {
        this.numeroHabitacion = numeroHabitacion;
    }

    public String getNombre_sede() {
        return nombreSede;
    }

    public void setNombre_sede(String nombreSede) {
        this.nombreSede = nombreSede;
    }

    public String getFecha_entrada() {
        return fechaEntrada != null ? fechaEntrada.toString() : "";
    }

    public String getFecha_salida() {
        return fechaSalida != null ? fechaSalida.toString() : "";
    }

    public Integer getCantidad_adultos() {
        return cantidadAdultos;
    }

    public Integer getCantidad_ninos() {
        return cantidadNinos;
    }

    public String getFecha_reserva() {
        return fechaReserva != null ? fechaReserva.toString() : "";
    }
}
