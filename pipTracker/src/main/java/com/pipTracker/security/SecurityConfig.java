package com.pipTracker.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/users/login",
                                "/api/users/register",
                                "/addEmployee/{id}",
                                "/addManager/{hrId}"


                        ).permitAll()
                        //employee related
                        .requestMatchers(HttpMethod.POST,  "/api/employees/addHr").hasAnyRole("ADMIN", "HR")
                        .requestMatchers(HttpMethod.POST,  "/api/employees/addManager/{hrId}").hasAnyRole("ADMIN", "HR","MANAGER")
                        .requestMatchers(HttpMethod.POST, "/api/employees/addEmployee/{managerId}").hasAnyRole("ADMIN", "HR","MANAGER")
                        .requestMatchers(HttpMethod.GET,"/api/employees/getAll").hasAnyRole("ADMIN","HR","MANAGER")
                        .requestMatchers(HttpMethod.GET,"/api/employees/{id}").hasAnyRole("ADMIN","HR","MANAGER")
                        .requestMatchers(HttpMethod.GET,"/api/employees/getEmployeeByName/{name}").hasAnyRole("ADMIN","HR","MANAGER")
                        .requestMatchers(HttpMethod.GET,"/api/employeesUnderHr/{hrId}").hasAnyRole("ADMIN","HR","MANAGER")
                        .requestMatchers(HttpMethod.GET,"/api/employeesUnderManager/{managerId}").hasAnyRole("ADMIN","HR","MANAGER")
                        .requestMatchers(HttpMethod.PUT, "/api/employees/{id}").hasRole("HR")
                        .requestMatchers(HttpMethod.PUT, "/api/employees/updateRole/{id}").hasAnyRole("ADMIN", "HR")
                        .requestMatchers(HttpMethod.DELETE, "/employees/{id}").hasAnyRole(" ADMIN","HR")
                        .requestMatchers(HttpMethod.PUT, "/api/users/updatePassword/{employeeId}").hasRole("HR")

                        //feedback related
                        .requestMatchers(HttpMethod.POST,"/api/feedback/add/{employeeId}").hasAnyRole("HR","MANAGER")
                        .requestMatchers(HttpMethod.PUT,"/api/feedback/update/{employeeId}").hasAnyRole("HR","MANAGER")
                        .requestMatchers(HttpMethod.GET,"/api/feedback/get/{employeeId}").permitAll()
                        .requestMatchers(HttpMethod.GET,"/api/feedback/getall").hasAnyRole("HR","MANAGER","ADMIN")
                        .requestMatchers(HttpMethod.DELETE,"/api/feedback/delete/{employeeId}").hasAnyRole("HR","MANAGER")
                        .requestMatchers(HttpMethod.DELETE,"/api/feedback/delete/{employeeId}/{feedbackId}").hasAnyRole("HR","MANAGER")
                        //pip related
                        .requestMatchers(HttpMethod.POST,"/api/pip/save").hasAnyRole("HR","MANAGER")
                        .requestMatchers(HttpMethod.PUT,"/api/pip/update/{id}").hasAnyRole("HR","MANAGER")
                        .requestMatchers(HttpMethod.GET,"/api/pip/getById/{id}").permitAll()
                        .requestMatchers(HttpMethod.GET,"/api/pip/get").hasAnyRole("HR","MANAGER","ADMIN")
                        .requestMatchers(HttpMethod.DELETE,"/api/pip/delete/{id}").hasAnyRole("HR","MANAGER")
                        .anyRequest().authenticated()
                )
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(Arrays.asList("http://localhost:3000")); // your React app
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(Arrays.asList("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
