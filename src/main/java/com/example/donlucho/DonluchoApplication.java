package com.example.donlucho;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import com.example.donlucho.model.*;
import com.example.donlucho.repository.*;

import org.springframework.security.crypto.password.PasswordEncoder;
import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.time.LocalDateTime;

@SpringBootApplication
public class DonluchoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DonluchoApplication.class, args);
	}

	@Bean
	@Profile("!prod")
	public CommandLineRunner initDatabase(
			RolRepositorio rolRepo,
			UsuarioRepositorio userRepo,
			SedeRepositorio sedeRepo,
			HabitacionRepositorio habRepo,
			ClienteRepositorio cliRepo,
			ReservaRepositorio resRepo,
			DataSource dataSource,
			PasswordEncoder passwordEncoder) {
		return args -> {
			try (java.sql.Connection conn = dataSource.getConnection();
				 java.sql.PreparedStatement ps = conn.prepareStatement("UPDATE rol SET nombre_rol = 'miembro' WHERE nombre_rol IN ('usuario', 'mienbro')")) {
				int updated = ps.executeUpdate();
				if (updated > 0) {
					System.out.println("====== DB Seeder: Updated role to 'miembro' in the database! ======");
				}
			} catch (Exception e) {
				System.err.println("Error updating role to 'miembro': " + e.getMessage());
			}

			// Seed default users (admin and member) only if no users exist
			if (userRepo.count() == 0) {
				try {
					Rol adminRol = rolRepo.findByNombreRol("administrador")
							.orElseGet(() -> rolRepo.save(new Rol(null, "administrador")));
					Rol userRol = rolRepo.findByNombreRol("miembro")
							.orElseGet(() -> rolRepo.save(new Rol(null, "miembro")));

					// Admin User
					Usuario admin = new Usuario();
					admin.setEmail("admin@donlucho.com");
					admin.setPassword(passwordEncoder.encode("admin"));
					admin.setEstado(true);
					admin.setNombre("Administrador");
					admin.setApellidoPaterno("Don");
					admin.setApellidoMaterno("Lucho");
					admin.setDni("00000000");
					admin.getRoles().add(adminRol);
					userRepo.save(admin);

					// Member User
					Usuario member = new Usuario();
					member.setEmail("miembro@donlucho.com");
					member.setPassword(passwordEncoder.encode("miembro"));
					member.setEstado(true);
					member.setNombre("Miembro");
					member.setApellidoPaterno("Don");
					member.setApellidoMaterno("Lucho");
					member.setDni("11111111");
					member.getRoles().add(userRol);
					userRepo.save(member);

					System.out.println("====== DB Seeder: Seeded default admin (admin@donlucho.com / admin) and member (miembro@donlucho.com / miembro) ======");
				} catch (Exception e) {
					System.err.println("Error seeding default users: " + e.getMessage());
				}
			}

			if (rolRepo.count() == 0) {
				System.out.println("====== DB Seeder: Seeding database... ======");
				
				// Seed Roles
				Rol adminRol = rolRepo.save(new Rol(null, "administrador"));
				Rol userRol = rolRepo.save(new Rol(null, "miembro"));

				// Seed Sedes
				Sede sede1 = sedeRepo.save(new Sede(null, "Sede Central Huancayo", "Calle Real 123", "064-123456", "Huancayo"));
				Sede sede2 = sedeRepo.save(new Sede(null, "Sede El Tambo", "Av. Julio Sumar 456", "064-654321", "Huancayo"));

				// Seed Habitaciones
				Habitacion hab101 = new Habitacion();
				hab101.setNumeroHabitacion("101");
				hab101.setPiso(1);
				hab101.setIdSede(sede1.getIdSede());
				hab101.setEstado("Disponible");
				hab101.setImagenUrl("/utiles/images/habitacion_default.jpg");
				hab101.setPrecioNoche(new BigDecimal("120.00"));
				hab101.setNombreTipo("Estándar");
				hab101.setDescripcion("Habitación doble muy confortable con baño privado y TV.");
				hab101.setCapacidadMaxima(2);
				hab101 = habRepo.save(hab101);

				Habitacion hab102 = new Habitacion();
				hab102.setNumeroHabitacion("102");
				hab102.setPiso(1);
				hab102.setIdSede(sede1.getIdSede());
				hab102.setEstado("Disponible");
				hab102.setImagenUrl("/utiles/images/habitacion_default.jpg");
				hab102.setPrecioNoche(new BigDecimal("180.00"));
				hab102.setNombreTipo("Suite");
				hab102.setDescripcion("Suite presidencial con jacuzzi y vista panorámica.");
				hab102.setCapacidadMaxima(2);
				hab102 = habRepo.save(hab102);

				Habitacion hab201 = new Habitacion();
				hab201.setNumeroHabitacion("201");
				hab201.setPiso(2);
				hab201.setIdSede(sede2.getIdSede());
				hab201.setEstado("Disponible");
				hab201.setImagenUrl("/utiles/images/habitacion_default.jpg");
				hab201.setPrecioNoche(new BigDecimal("90.00"));
				hab201.setNombreTipo("Simple");
				hab201.setDescripcion("Habitación individual ideal para viajes de negocios.");
				hab201.setCapacidadMaxima(1);
				hab201 = habRepo.save(hab201);

				// Seed Clientes (Presenciales)
				Cliente cli1 = new Cliente();
				cli1.setNombres("Juan Carlos");
				cli1.setApellidos("Pérez Quispe");
				cli1.setDni("77777777");
				cli1.setTelefono("987654321");
				cli1.setEmail("juan@gmail.com");
				cli1.setFechaRegistro(LocalDateTime.now().minusDays(1));
				cli1.setIdHabitacion(hab101.getIdHabitacion());
				cliRepo.save(cli1);

				System.out.println("====== DB Seeder: Database seeded successfully! ======");
			}
		};
	}
}
