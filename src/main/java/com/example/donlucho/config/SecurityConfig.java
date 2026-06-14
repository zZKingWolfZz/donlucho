package com.example.donlucho.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.DelegatingSecurityContextRepository;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.RequestAttributeSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityContextRepository securityContextRepository() {
        return new DelegatingSecurityContextRepository(
                new RequestAttributeSecurityContextRepository(),
                new HttpSessionSecurityContextRepository()
        );
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .securityContext(context -> context.securityContextRepository(securityContextRepository()))
            .authorizeHttpRequests(auth -> auth
                // Allow static resources
                .requestMatchers(
                    "/css/**", "/js/**", "/images/**", "/utiles/**",
                    "/style.css", "/bot.css", "/footer.css", "/script.js",
                    "/videofondo.mp4", "/favicon.png", "/info/style_info.css"
                ).permitAll()
                // Allow public endpoints
                .requestMatchers(
                    "/", "/info/**", "/game", "/login", "/logup",
                    "/api/login", "/api/register", "/api/consultar_dni",
                    "/habitaciones", "/register-admin", "/api/register-admin",
                    "/api/chat"
                ).permitAll()
                // Restrict dashboard and administration tools to admin role
                .requestMatchers(
                    "/dashboard", "/admin/**", "/reservas/**", "/clientes/**",
                    "/miembros", "/miembros/**", "/administradores", "/administradores/**", "/agregar_admin", "/agregar_admin.php",
                    "/agregar_habitacion", "/agregar_habitacion.php", "/habitaciones/agregar", "/habitaciones/guardar",
                    "/editar_habitacion", "/editar_habitacion.php", "/habitaciones/editar/**",
                    "/agregar_cliente", "/agregar_cliente.php", "/clientes/agregar", "/clientes/guardar",
                    "/editar_cliente", "/editar_cliente.php", "/clientes/editar/**"
                ).hasRole("administrador")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form.disable())
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
            );

        return http.build();
    }
}
